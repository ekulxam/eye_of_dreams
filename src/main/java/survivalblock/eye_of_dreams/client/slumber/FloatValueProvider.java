package survivalblock.eye_of_dreams.client.slumber;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.gl.UniformValue;

import java.util.function.Supplier;

public abstract class FloatValueProvider implements UniformValue {

    protected final Supplier<Float> supplier;

    public FloatValueProvider(Supplier<Float> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void write(Std140Builder builder) {
        builder.putFloat(this.supplier.get());
    }

    @Override
    public void addSize(Std140SizeCalculator calculator) {
        calculator.putFloat();
    }
}
