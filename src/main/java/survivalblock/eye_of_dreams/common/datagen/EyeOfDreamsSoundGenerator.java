package survivalblock.eye_of_dreams.common.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryWrapper;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.concurrent.CompletableFuture;

import static survivalblock.eye_of_dreams.common.EyeOfDreams.REVERSE_TOTEM;

public class EyeOfDreamsSoundGenerator extends FabricSoundsProvider {

    public EyeOfDreamsSoundGenerator(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registryLookup, SoundExporter exporter) {
        exporter.add(REVERSE_TOTEM.id(), SoundTypeBuilder.of(REVERSE_TOTEM).sound(SoundTypeBuilder.EntryBuilder.ofFile(REVERSE_TOTEM.id())));
    }

    @Override
    public String getName() {
        return "Sounds";
    }
}
