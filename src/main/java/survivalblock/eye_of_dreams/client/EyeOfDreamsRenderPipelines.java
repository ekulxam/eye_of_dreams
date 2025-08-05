package survivalblock.eye_of_dreams.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.util.Identifier;
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
            .withLocation(EyeOfDreams.id("slumber"))
            .withFragmentShader(EyeOfDreams.id("core/slumber"))
            .withVertexShader("core/blit_screen")
            .withSampler("DiffuseSampler")
            .withUniform("ColorChange", UniformType.UNIFORM_BUFFER)
            .withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER)
            .build();

    // ref https://github.com/neoforged/.github/blob/main/primers/1.21.6/index.md#writing-custom-uniforms
    private static MappableRingBuffer colorChangeUniforms;
    private static MappableRingBuffer samplerInfoUniforms;

    private static SimpleFramebuffer swapBuffer;

    private static boolean initialized = false;

    public static void init() {

    }

    private static void initialize() {
        // TODO DO NOT FORGET TO RESIZE THIS WHEN THE MAIN WINDOW SIZE CHANGES (mixin trigger ok)
        // see net.minecraft.client.util.tracy.TracyFrameCapturer.resize
        MinecraftClient client = MinecraftClient.getInstance();
        swapBuffer = new SimpleFramebuffer(
                EyeOfDreams.id("swap").toString(),
                client.getFramebuffer().textureWidth,
                client.getFramebuffer().textureHeight,
                true // not needed in this example
        );

        colorChangeUniforms = new MappableRingBuffer(
                () -> "ColorChange",
                // Buffer Usage
                // We set 128 as its used for a uniform and 2 since we are writing to it
                // Other bits can be found as constants in `GpuBuffer`
                GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE,
                // The size of the buffer
                // Easiest method is to use Std140SizeCalculator to properly calculate this
                new Std140SizeCalculator()
                        .putVec4()
                        .putVec4()
                        .get()
        );

        samplerInfoUniforms = new MappableRingBuffer(
                () -> "ColorChange",
                // Buffer Usage
                // We set 128 as its used for a uniform and 2 since we are writing to it
                // Other bits can be found as constants in `GpuBuffer`
                GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE,
                // The size of the buffer
                // Easiest method is to use Std140SizeCalculator to properly calculate this
                new Std140SizeCalculator()
                        .putVec2()
                        .putVec2()
                        .get()
        );
    }

    public static void renderSlumber(Framebuffer framebuffer) {
        RenderPipeline pipeline = EyeOfDreamsRenderPipelines.SLUMBER;
        RenderSystem.assertOnRenderThread();

        if (!initialized) {
            initialized = true;
            initialize();
        }

        GpuDevice gpu = RenderSystem.getDevice();

        GpuBuffer quadBuffer = RenderSystem.getQuadVertexBuffer();
        RenderSystem.ShapeIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer indexBuffer = indices.getIndexBuffer(6);

        // !!! Set up your uniforms *before* the pass !!!!
        // As we are using a ring buffer, this simply uses the next available buffer in the list
        colorChangeUniforms.rotate();
        samplerInfoUniforms.rotate();
        // Write the data to the buffer
        try (GpuBuffer.MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(colorChangeUniforms.getBlocking(), false, true)) {
            Std140Builder.intoBuffer(view.data())
                    .putVec4(0.5F, 1, 1.05F, 1)
                    .putVec4(0f, 0f, 0.2f, 0f);
        }

        // I think you can replace the sobel vertex shader with a simple passthrough instead
        try (GpuBuffer.MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(samplerInfoUniforms.getBlocking(), false, true)) {
            Std140Builder.intoBuffer(view.data())
                    .putVec2(framebuffer.textureWidth, framebuffer.textureHeight)
                    .putVec2(swapBuffer.textureWidth, swapBuffer.textureHeight);
        }


        // always use try-with-resources to ensure resource is closed at the end, avoid memory leaks
        try(RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> EyeOfDreams.id("slumber").toString(),

                // drawing into our swap buffer
                gpu.createTextureView(swapBuffer.getColorAttachment()),
                OptionalInt.empty()
                // we aren't drawing anything new so no need to mess with depth
//                gpu.createTextureView(framebuffer.getDepthAttachment()),
//                OptionalDouble.empty()
        )) {
            pass.setPipeline(SLUMBER);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("ColorChange", colorChangeUniforms.getBlocking());
//            pass.setUniform("SamplerInfo", samplerInfoUniforms.getBlocking());
            // might not actually be diffuse but I think theres a way to modify sampler
            // args https://juandiegomontoya.github.io/modern_opengl.html
            pass.bindSampler("DiffuseSampler", framebuffer.getColorAttachmentView());

            // draw the freaking full screen quad
            pass.setVertexBuffer(0, quadBuffer);
            pass.setIndexBuffer(indexBuffer, indices.getIndexType());
            pass.drawIndexed(0, 0, 6, 1);
        }

        try(RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> EyeOfDreams.id("slumber").toString(),
                // drawing back into our main window buffer (or whatever it is you passed in)
                gpu.createTextureView(framebuffer.getColorAttachment()),
                OptionalInt.empty()
        )) {
            // I just use this one to blit since its easy, no uniforms
            pass.setPipeline(RenderPipelines.TRACY_BLIT);
            pass.bindSampler("InSampler", swapBuffer.getColorAttachmentView());

            pass.setVertexBuffer(0, quadBuffer);
            pass.setIndexBuffer(indexBuffer, indices.getIndexType());
            pass.drawIndexed(0, 0, 6, 1);
        }
    }
}
