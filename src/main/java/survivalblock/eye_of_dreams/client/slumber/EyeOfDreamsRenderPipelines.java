package survivalblock.eye_of_dreams.client.slumber;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.*;
import net.minecraft.util.Identifier;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

public class EyeOfDreamsRenderPipelines {

    public static final Identifier SLUMBER_ID = EyeOfDreams.id("slumber");

    public static final RenderPipeline SLUMBER = RenderPipeline.builder(RenderPipelines.POST_EFFECT_PROCESSOR_SNIPPET)
            .withLocation(SLUMBER_ID)
            .withFragmentShader(EyeOfDreams.id("core/slumber"))
            .withVertexShader("post/sobel")
            .withSampler("DiffuseSampler")
            .withUniform("ColorChange", UniformType.UNIFORM_BUFFER)
            .withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER)
            .build();

    public static final RenderPipeline BLIT = RenderPipeline.builder(RenderPipelines.POST_EFFECT_PROCESSOR_SNIPPET)
            .withLocation(EyeOfDreams.id("blit"))
            .withFragmentShader("post/blit")
            .withVertexShader("post/blit")
            .withSampler("InSampler")
            .withUniform("BlitConfig", UniformType.UNIFORM_BUFFER)
            .withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER)
            .build();

    public static void init() {

    }
}
