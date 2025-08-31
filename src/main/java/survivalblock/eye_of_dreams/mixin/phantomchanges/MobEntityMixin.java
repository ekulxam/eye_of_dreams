package survivalblock.eye_of_dreams.mixin.phantomchanges;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;onAttacking(Lnet/minecraft/entity/Entity;)V"))
    private void growPhantom(ServerWorld world, Entity target, CallbackInfoReturnable<Boolean> cir, @Local float damage) {
        if (!((MobEntity) (Object) this instanceof PhantomEntity phantomEntity)) {
            return;
        }
        phantomEntity.setPhantomSize(phantomEntity.getPhantomSize() + phantomEntity.getRandom().nextBetween(1, 2));
        phantomEntity.heal(damage * 0.2f);
        if (target instanceof LivingEntity living) {
            living.startRiding(phantomEntity);
        }
    }
}
