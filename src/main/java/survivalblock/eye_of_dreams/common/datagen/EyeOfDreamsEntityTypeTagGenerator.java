package survivalblock.eye_of_dreams.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.concurrent.CompletableFuture;

public class EyeOfDreamsEntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {

    public EyeOfDreamsEntityTypeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.valueLookupBuilder(EyeOfDreams.ALWAYS_VISIBLE)
                .add(EntityType.BLOCK_DISPLAY)
                .add(EntityType.ITEM_DISPLAY)
                .add(EntityType.TEXT_DISPLAY)
                .add(EntityType.PAINTING);
    }
}
