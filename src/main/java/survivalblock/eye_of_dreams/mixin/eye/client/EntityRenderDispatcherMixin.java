package survivalblock.eye_of_dreams.mixin.eye.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
    private boolean hideFromPryingEyes(boolean original, Entity entity, Frustum frustum, double x, double y, double z) {
        return EyeOfDreams.canSee(original, MinecraftClient.getInstance().getCameraEntity(), entity);
    }
}
