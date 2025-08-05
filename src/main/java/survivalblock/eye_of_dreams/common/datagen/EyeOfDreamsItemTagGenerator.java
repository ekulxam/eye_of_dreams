package survivalblock.eye_of_dreams.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.concurrent.CompletableFuture;

public class EyeOfDreamsItemTagGenerator extends FabricTagProvider.ItemTagProvider {

    public EyeOfDreamsItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.valueLookupBuilder(EyeOfDreams.ALLOW_USE_WHILE_SLUMBERING)
                .add(Items.SPYGLASS);
    }
}
