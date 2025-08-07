package survivalblock.eye_of_dreams.mixin.eye.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Pool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.eye_of_dreams.client.EyeOfDreamsClient;
import survivalblock.eye_of_dreams.client.slumber.EyeOfDreamsRenderPipelines;

@Mixin(value = GameRenderer.class, priority = 5000)
public abstract class GameRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private Pool pool;

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = At.Shift.AFTER))
    private void applySlumberShader(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        double progress = EyeOfDreamsClient.getRealProgress();
        if (progress >= 0.0001) {
            PostEffectProcessor pep = this.client.getShaderLoader().loadPostEffect(EyeOfDreamsRenderPipelines.SLUMBER_ID, DefaultFramebufferSet.MAIN_ONLY);
            if (pep != null) {
                pep.render(this.client.getFramebuffer(), this.pool);
            }
        }
    }
}
