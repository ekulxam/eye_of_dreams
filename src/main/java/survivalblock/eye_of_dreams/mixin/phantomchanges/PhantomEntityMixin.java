package survivalblock.eye_of_dreams.mixin.phantomchanges;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PhantomEntity.class)
public abstract class PhantomEntityMixin extends MobEntity {

    protected PhantomEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapMethod(method = "tick")
    private void noClipOn(Operation<Void> original) {
        this.noClip = true;
        original.call();
        this.noClip = false;
    }

    @Debug(export = true)
    @Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$PhantomMoveControl")
    public static class PhantomMoveControlMixin {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(D)Lnet/minecraft/util/math/Vec3d;"))
        private Vec3d fasterPhantoms(Vec3d instance, double value, Operation<Vec3d> original) {
            return original.call(instance, value * 1.5);
        }
    }
}
