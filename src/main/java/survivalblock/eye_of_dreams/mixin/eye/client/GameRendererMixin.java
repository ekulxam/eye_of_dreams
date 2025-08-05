package survivalblock.eye_of_dreams.mixin.eye.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Pool;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.eye_of_dreams.client.EyeOfDreamsClient;
import survivalblock.eye_of_dreams.client.EyeOfDreamsRenderPipelines;

import static survivalblock.eye_of_dreams.common.EyeOfDreams.SLUMBERING;

@Mixin(value = GameRenderer.class, priority = 5000)
public abstract class GameRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @SuppressWarnings({"UnstableApiUsage", "DataFlowIssue"})
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = At.Shift.AFTER))
    private void applySlumberShader(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        Entity focused = this.client.getCameraEntity();
        if (focused == null) {
            if (!this.client.player.getAttachedOrCreate(SLUMBERING)) {
                return;
            }
        } else {
            if (!focused.getAttachedOrElse(SLUMBERING, false)) {
                return;
            }
        }
        EyeOfDreamsRenderPipelines.renderSlumber(this.client.getFramebuffer());
    }
}
