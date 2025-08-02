package survivalblock.eye_of_dreams.mixin.phantomchanges;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(EntityType.class)
public class EntityTypeMixin {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "floatValue=0.3375"))
    private static float phantomCarriers(float original) {
        return -0.6625F;
    }
}
