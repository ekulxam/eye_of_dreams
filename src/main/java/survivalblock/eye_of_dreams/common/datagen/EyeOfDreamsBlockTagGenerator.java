package survivalblock.eye_of_dreams.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.concurrent.CompletableFuture;

public class EyeOfDreamsBlockTagGenerator extends FabricTagProvider.BlockTagProvider {

    public static final String GLOWCASE = "glowcase";

    public EyeOfDreamsBlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.getTagBuilder(EyeOfDreams.ALLOW_USE_WHILE_SLUMBERING_BLOCK)
                .addOptional(Identifier.of(GLOWCASE, "item_provider_block"))
                .addOptional(Identifier.of(GLOWCASE, "popup_block"));
    }
}
