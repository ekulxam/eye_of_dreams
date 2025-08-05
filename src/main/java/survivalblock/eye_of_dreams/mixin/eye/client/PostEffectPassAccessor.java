package survivalblock.eye_of_dreams.mixin.eye.client;

import net.minecraft.client.gl.PostEffectPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PostEffectPass.class)
public interface PostEffectPassAccessor {

    @Accessor("SIZE")
    static int eye_of_dreams$getSIZE() {
        throw new UnsupportedOperationException("Mixin accessor");
    }
}
