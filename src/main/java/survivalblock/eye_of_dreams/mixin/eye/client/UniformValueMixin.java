package survivalblock.eye_of_dreams.mixin.eye.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.gl.UniformValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import survivalblock.eye_of_dreams.client.slumber.ShaderProgressFloatProvider;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(UniformValue.class)
public interface UniformValueMixin {

    @Mixin(value = UniformValue.Type.class, priority = 364000)
    class TypeMixin {

        @Shadow @Final private static UniformValue.Type[] field_60145;

        @Invoker("<init>")
        static UniformValue.Type eye_of_dreams$invokeInit(String enumName, int id, final String name, final Codec<? extends UniformValue> codec) {
            throw new UnsupportedOperationException();
        }

        static {
            ArrayList<UniformValue.Type> list = new ArrayList<>(Arrays.asList(field_60145));
            int size = list.size();
            ShaderProgressFloatProvider.TYPE = eye_of_dreams$invokeInit("eye_of_dreams$SHADER_PROGRESS", size, "eye_of_dreams$shaderProgress", ShaderProgressFloatProvider.CODEC);
            list.add(ShaderProgressFloatProvider.TYPE);
            field_60145 = list.toArray(new UniformValue.Type[size + 1]);
        }
    }
}
