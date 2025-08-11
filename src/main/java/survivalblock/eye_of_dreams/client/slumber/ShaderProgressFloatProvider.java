package survivalblock.eye_of_dreams.client.slumber;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.VisibleForTesting;
import survivalblock.eye_of_dreams.client.EyeOfDreamsClient;

import java.util.function.Supplier;

public class ShaderProgressFloatProvider extends FloatValueProvider {

    public static final ShaderProgressFloatProvider INSTANCE = new ShaderProgressFloatProvider(EyeOfDreamsClient::getRealShaderProgress);
    public static final Codec<ShaderProgressFloatProvider> CODEC = Codec.unit(INSTANCE);

    @VisibleForTesting
    public static Type TYPE;

    protected ShaderProgressFloatProvider(Supplier<Float> supplier) {
        super(supplier);
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
