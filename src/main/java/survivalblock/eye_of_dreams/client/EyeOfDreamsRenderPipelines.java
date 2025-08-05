package survivalblock.eye_of_dreams.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.gl.UniformValue;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class EyeOfDreamsRenderPipelines {

    public static final String POST_SOBEL = "post/sobel";
    public static final String POST_BLIT = "post/blit";

    public static final RenderPipeline SLUMBER = RenderPipeline.builder(RenderPipelines.POST_EFFECT_PROCESSOR_SNIPPET)
            .withFragmentShader(POST_SOBEL)
            .withVertexShader(EyeOfDreams.id("slumber"))
            .withLocation(EyeOfDreams.id("slumber/0"))
            .withSampler("DiffuseSampler")
            .withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER)
            .withUniform("ColorChange", UniformType.UNIFORM_BUFFER)
            .build();

    public static final RenderPipeline BLIT = RenderPipeline.builder(RenderPipelines.POST_EFFECT_PROCESSOR_SNIPPET)
            .withFragmentShader(POST_BLIT)
            .withVertexShader(POST_BLIT)
            .withLocation(EyeOfDreams.id("slumber/1"))
            .withSampler("InSampler")
            .withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER)
            .withUniform("BlitConfig", UniformType.UNIFORM_BUFFER)
            .build();

    public static void init() {

    }

    public static void renderSlumber(Framebuffer framebuffer) {
        RenderPipeline pipeline = EyeOfDreamsRenderPipelines.SLUMBER;
        RenderSystem.assertOnRenderThread();

        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        String id = pipeline.getLocation().toString();

        GpuBuffer gpuBuffer = RenderSystem.getQuadVertexBuffer();
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer2 = shapeIndexBuffer.getIndexBuffer(6);

        try (RenderPass renderPass = commandEncoder.createRenderPass(
                () -> "Slumber help what am I doing",
                framebuffer.getColorAttachmentView(),
                OptionalInt.empty(),
                framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null,
                OptionalDouble.empty()
        )) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);

            List<UniformValue> uniforms = List.of(
                    new UniformValue.Vec4fValue(new Vector4f(0.5F, 1, 1.05F, 1)),
                    new UniformValue.Vec4fValue(new Vector4f(0, 0, 0.2F, 0)));

            Map<String, GpuBuffer> uniformBuffers = new HashMap<>();

            Std140SizeCalculator std140SizeCalculator = new Std140SizeCalculator();

            for (UniformValue uniformValue : uniforms) {
                uniformValue.addSize(std140SizeCalculator);
            }

            int i = std140SizeCalculator.get();

            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                Std140Builder std140Builder = Std140Builder.onStack(memoryStack, i);

                for (UniformValue uniformValue2 : uniforms) {
                    uniformValue2.write(std140Builder);
                }

                uniformBuffers
                        .put("ColorChange", RenderSystem.getDevice().createBuffer(() -> id + " / " + "ColorChange", 128, std140Builder.get()));
            }

            // set ColorChange uniform
            uniformBuffers.forEach(renderPass::setUniform);

            renderPass.setVertexBuffer(0, gpuBuffer);
            renderPass.setIndexBuffer(gpuBuffer2, shapeIndexBuffer.getIndexType());

            // set DiffuseSampler
            renderPass.bindSampler("DiffuseSampler", framebuffer.getColorAttachmentView());

            renderPass.drawIndexed(0, 0, 6, 1);
        }
    }
}
