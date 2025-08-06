package survivalblock.eye_of_dreams.client.slumber;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.SimpleFramebuffer;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

@SuppressWarnings({"JavadocReference"})
public class EyeOfDreamsShaderRenderer {

    // ref https://github.com/neoforged/.github/blob/main/primers/1.21.6/index.md#writing-custom-uniforms
    // thanks, hama
    private static final MappableRingBuffer COLOR_CHANGE = new MappableRingBuffer(
            () -> "ColorChange",
            // Buffer Usage
            // We set 128 as it's used for a uniform and 2 since we are writing to it
            // Other bits can be found as constants in `GpuBuffer`
            GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE,
            // The size of the buffer
            // Easiest method is to use Std140SizeCalculator to properly calculate this
            new Std140SizeCalculator()
                    .putVec4()
                    .putVec4()
                    .get()
    );

    private static final MappableRingBuffer SAMPLER_INFO = new MappableRingBuffer(
            () -> "SamplerInfo",
            // Buffer Usage
            // We set 128 as it's used for a uniform and 2 since we are writing to it
            // Other bits can be found as constants in `GpuBuffer`
            GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE,
            // The size of the buffer
            // Easiest method is to use Std140SizeCalculator to properly calculate this
            new Std140SizeCalculator()
                    .putVec2()
                    .putVec2()
                    .get()
    );

    private static final MappableRingBuffer BLIT_CONFIG = new MappableRingBuffer(
            () -> "BlitConfig",
            GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE,
            new Std140SizeCalculator()
                    .putVec4()
                    .get()
    );

    private static GpuTexture swap;
    private static GpuTextureView swapView;
    private static SimpleFramebuffer swapBuffer;

    public static void tryResize(Framebuffer framebuffer) {
        if (swap == null ||
                swapView == null ||
                framebuffer.textureWidth != swap.getWidth(1) ||
                framebuffer.textureHeight != swap.getHeight(1)) {
            resize(framebuffer.textureWidth, framebuffer.textureHeight);
        }
    }

    /**
     * @see net.minecraft.client.util.tracy.TracyFrameCapturer#resize(int, int)
     * @param width the width of the framebuffer
     * @param height the height of the framebuffer
     */
    public static void resize(int width, int height) {
        if (swap != null) {
            swap.close();
        }
        if (swapView != null) {
            swapView.close();
        }
        GpuDevice device = RenderSystem.getDevice();
        swap = device.createTexture("eod swap", 10, TextureFormat.RGBA8, width, height, 1, 1);
        swapView = device.createTextureView(swap);

        swapBuffer = new SimpleFramebuffer(
                EyeOfDreams.id("swap").toString(),
                width,
                height,
                true // not needed in this example
        );
    }

    public static void renderSlumber(Framebuffer framebuffer, double progress) {
        RenderSystem.assertOnRenderThread();

        RenderSystem.backupProjectionMatrix();

        tryResize(framebuffer);

        Supplier<String> labelGetter = EyeOfDreamsRenderPipelines.SLUMBER_ID::toString;

        GpuDevice device = RenderSystem.getDevice();
        CommandEncoder commandEncoder = device.createCommandEncoder();

        GpuBuffer quadBuffer = RenderSystem.getQuadVertexBuffer();
        RenderSystem.ShapeIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer indexBuffer = indices.getIndexBuffer(6);

        // !!! Set up your uniforms *before* the pass !!!!
        // As we are using a ring buffer, this simply uses the next available buffer in the list
        COLOR_CHANGE.rotate();
        SAMPLER_INFO.rotate();
        // Write the data to the buffer
        try (GpuBuffer.MappedView view = commandEncoder.mapBuffer(COLOR_CHANGE.getBlocking(), false, true)) {
            Std140Builder.intoBuffer(view.data())
                    .putVec4(0.5F, 1, 1.05F, 1) // MulColor
                    .putVec4(0, 0, 0.2f, 0); // AddColor
        }

        swapBuffer.setFilter(FilterMode.LINEAR);

        try (GpuBuffer.MappedView view = commandEncoder.mapBuffer(SAMPLER_INFO.getBlocking(), false, true)) {
            Std140Builder.intoBuffer(view.data())
                    .putVec2(framebuffer.textureWidth, framebuffer.textureHeight)
                    .putVec2(swapBuffer.textureWidth, swapBuffer.textureHeight);
        }

        // always use try-with-resources to ensure resource is closed at the end, avoid memory leaks
        try(RenderPass pass = commandEncoder.createRenderPass(
                labelGetter,
                // drawing into our swap buffer
                device.createTextureView(swapBuffer.getColorAttachment()),
                OptionalInt.empty()
        )) {
            pass.setPipeline(EyeOfDreamsRenderPipelines.SLUMBER);
            RenderSystem.bindDefaultUniforms(pass);
            // might not actually be diffuse but I think theres a way to modify sampler
            // args https://juandiegomontoya.github.io/modern_opengl.html
            pass.bindSampler("DiffuseSampler", framebuffer.getColorAttachmentView());
            pass.setUniform("ColorChange", COLOR_CHANGE.getBlocking());
            pass.setUniform("SamplerInfo", SAMPLER_INFO.getBlocking());

            // draw the freaking full screen quad
            pass.setVertexBuffer(0, quadBuffer);
            pass.setIndexBuffer(indexBuffer, indices.getIndexType());
            pass.drawIndexed(0, 0, 6, 1);
        }

        swapBuffer.setFilter(FilterMode.NEAREST);

        SAMPLER_INFO.rotate();
        BLIT_CONFIG.rotate();

        try (GpuBuffer.MappedView view = commandEncoder.mapBuffer(BLIT_CONFIG.getBlocking(), false, true)) {
            Std140Builder.intoBuffer(view.data())
                    .putVec4(1, 1, 1, 1);
        }

        try (GpuBuffer.MappedView view = commandEncoder.mapBuffer(SAMPLER_INFO.getBlocking(), false, true)) {
            Std140Builder.intoBuffer(view.data())
                    .putVec2(framebuffer.textureWidth, framebuffer.textureHeight)
                    .putVec2(swapBuffer.textureWidth, swapBuffer.textureHeight);
        }

        try(RenderPass pass = commandEncoder.createRenderPass(
                labelGetter,
                // drawing back into our main window buffer (or whatever it is you passed in)
                device.createTextureView(framebuffer.getColorAttachment()),
                OptionalInt.empty(),
                framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null,
                OptionalDouble.empty()
        )) {
            // I just use this one to blit since its easy, no uniforms
            pass.setPipeline(EyeOfDreamsRenderPipelines.BLIT);
            pass.bindSampler("InSampler", swapBuffer.getColorAttachmentView());
            pass.setUniform("BlitConfig", BLIT_CONFIG.getBlocking());
            pass.setUniform("SamplerInfo", SAMPLER_INFO.getBlocking());

            pass.setVertexBuffer(0, quadBuffer);
            pass.setIndexBuffer(indexBuffer, indices.getIndexType());
            pass.drawIndexed(0, 0, 6, 1);
        }

        RenderSystem.restoreProjectionMatrix();
    }
}
