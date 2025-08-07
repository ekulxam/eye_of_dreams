package survivalblock.eye_of_dreams.mixin.eye.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.eye_of_dreams.client.EyeOfDreamsClient;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.List;
import java.util.Map;

/*
I just had a brilliant horrible idea
What if I mixin into PostEffectPass and add my uniforms via code in a system I know that works
 */
@Mixin(PostEffectPass.class)
public class PostEffectPassMixin {

    @Shadow @Final private String id;

    @Shadow @Final private Map<String, GpuBuffer> uniformBuffers;
    @Nullable
    @Unique
    protected Map<String, List<UniformValue>> eye_of_dreams$originalUniforms = null;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void brilliantHorribleIdea(RenderPipeline pipeline, Identifier outputTargetId, Map<String, List<UniformValue>> uniforms, List<PostEffectPass.Sampler> samplers, CallbackInfo ci) {
        if (!this.id.equals(EyeOfDreams.id("slumber/0").toString())) {
            return;
        }
        this.eye_of_dreams$originalUniforms = ImmutableMap.copyOf(uniforms);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void help(FrameGraphBuilder builder, Map<Identifier, Handle<Framebuffer>> handles, GpuBufferSlice gpuBufferSlice, CallbackInfo ci) {
        this.eye_of_dreams$reset();
    }

    @Unique
    protected void eye_of_dreams$reset() {
        if (this.eye_of_dreams$originalUniforms == null) {
            return;
        }
        this.uniformBuffers.clear();
        for (Map.Entry<String, List<UniformValue>> entry : this.eye_of_dreams$originalUniforms.entrySet()) {
            List<UniformValue> list = entry.getValue();
            if (!list.isEmpty()) {
                Std140SizeCalculator std140SizeCalculator = new Std140SizeCalculator();

                for (UniformValue uniformValue : list) {
                    uniformValue.addSize(std140SizeCalculator);
                }

                int i = std140SizeCalculator.get();

                try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                    Std140Builder std140Builder = Std140Builder.onStack(memoryStack, i);

                    for (UniformValue uniformValue2 : list) {
                        if (uniformValue2 instanceof UniformValue.FloatValue) {
                            uniformValue2 = new UniformValue.FloatValue(EyeOfDreamsClient.getRealProgress());
                        }
                        uniformValue2.write(std140Builder);
                    }

                    this.uniformBuffers
                            .put(entry.getKey(),
                                    RenderSystem.getDevice().createBuffer(() -> this.id + " / " + entry.getKey(),
                                            GpuBuffer.USAGE_UNIFORM,
                                            std140Builder.get()));
                }
            }
        }
    }
}
