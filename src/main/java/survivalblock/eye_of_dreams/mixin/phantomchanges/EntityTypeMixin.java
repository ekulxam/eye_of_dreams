package survivalblock.eye_of_dreams.mixin.phantomchanges;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType$Builder;dimensions(FF)Lnet/minecraft/entity/EntityType$Builder;", ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=phantom")))
    private static <T extends Entity> EntityType.Builder<T> reducePhantomHitbox(EntityType.Builder<T> instance, float width, float height, Operation<EntityType.Builder<T>> original) {
        return original.call(instance, width * 0.8F, height * 0.8F);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "floatValue=0.3375"))
    private static float phantomCarriers(float original) {
        return -0.6625F;
    }
}
