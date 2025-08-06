package survivalblock.eye_of_dreams.mixin.eye.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.eye_of_dreams.client.EyeOfDreamsClient;
import survivalblock.eye_of_dreams.client.EyeOfDreamsRenderPipelines;

@Mixin(value = GameRenderer.class, priority = 5000)
public abstract class GameRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = At.Shift.AFTER))
    private void applySlumberShader(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        double progress = (double) EyeOfDreamsClient.shaderProgress / EyeOfDreamsClient.MAX_PROGRESS;
        if (progress >= 0.0001) {
            EyeOfDreamsRenderPipelines.renderSlumber(this.client.getFramebuffer(), progress);
        }
    }
}
