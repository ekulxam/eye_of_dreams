package survivalblock.eye_of_dreams.mixin.eye;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.DataResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.ServerWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.eye_of_dreams.common.EyeItem;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import static survivalblock.eye_of_dreams.common.EyeOfDreams.SLUMBERING;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = LivingEntity.class, priority = 5000)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    boolean eye_of_dreams$wasSlumbering = false;

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow @Final private AttributeContainer attributes;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyReturnValue(method = "canSee(Lnet/minecraft/entity/Entity;)Z", at = @At("RETURN"))
    private boolean canSee(boolean original, Entity entity) {
        return EyeOfDreams.canSee(original, this, entity);
    }

    @ModifyReturnValue(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("RETURN"))
    private boolean canTarget(boolean original, LivingEntity living) {
        return EyeOfDreams.canSee(original, this, living);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void enterExitSlumber(CallbackInfo ci) {
        World world = this.getWorld();
        if (world.isClient()) {
            return;
        }
        boolean slumbering = this.getEquippedStack(EquipmentSlot.HEAD)
                .isOf(EyeOfDreams.EYE_ITEM) &&
                world.isNight();
        if (this.getAttachedOrCreate(SLUMBERING) != slumbering) {
            this.setAttached(SLUMBERING, slumbering);
        }
        if (slumbering != eye_of_dreams$wasSlumbering) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(), EyeOfDreams.REALM, this.getSoundCategory());
            eye_of_dreams$wasSlumbering = slumbering;
            if (slumbering) {
                EyeItem.EYE_MODIFIERS_MAP.forEach((attribute, modifier) -> {
                    EntityAttributeInstance entityAttributeInstance = this.attributes.getCustomInstance(attribute);
                    if (entityAttributeInstance != null) {
                        entityAttributeInstance.removeModifier(modifier.id());
                        entityAttributeInstance.addTemporaryModifier(modifier);
                    }
                });
            } else {
                EyeItem.EYE_MODIFIERS_MAP.forEach((attribute, modifier) -> {
                    EntityAttributeInstance entityAttributeInstance = this.attributes.getCustomInstance(attribute);
                    if (entityAttributeInstance != null) {
                        entityAttributeInstance.removeModifier(modifier);
                    }
                });
            }
            if (this instanceof ServerWaypoint serverWaypoint && serverWaypoint.hasWaypoint()) {
                ((ServerWorld) world).getWaypointHandler().onTrack(serverWaypoint); // should be a safe cast
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void cancelDamageWhenDreaming(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        if (attacker == null) {
            return;
        }
        if (EyeOfDreams.canSee(true, attacker, (LivingEntity) (Object) this)) {
            return;
        }
        cir.setReturnValue(false);
    }

    @ModifyExpressionValue(method = "createTracker", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;waypointConfig:Lnet/minecraft/world/waypoint/Waypoint$Config;"))
    private Waypoint.Config slumberingWaypointMixinHack(Waypoint.Config original) {
        if (!this.getAttachedOrCreate(SLUMBERING)) {
            return original;
        }
        DataResult<NbtElement> encode = Waypoint.Config.CODEC.encodeStart(NbtOps.INSTANCE, original);
        if (encode.isError()) {
            return original;
        }
        NbtElement nbtElement = encode.getOrThrow();
        DataResult<Waypoint.Config> decode = Waypoint.Config.CODEC.parse(NbtOps.INSTANCE, nbtElement);
        if (decode.isError()) {
            return original;
        }
        Waypoint.Config copy = decode.getOrThrow();
        copy.style = EyeOfDreams.STAR;
        return copy;
    }
}
