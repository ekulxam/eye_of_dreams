package survivalblock.eye_of_dreams.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.concurrent.CompletableFuture;

public class EyeOfDreamsEnUsLangGenerator extends FabricLanguageProvider {

    public EyeOfDreamsEnUsLangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(EyeOfDreams.EYE_ITEM, "Dreamer's Eye");
        translationBuilder.add("item.eye_of_dreams.eye.hidden", "[Press shift to see lore]");
        translationBuilder.add("item.eye_of_dreams.eye.0", "Dreamers' blue, dreamers' gold");
        translationBuilder.add("item.eye_of_dreams.eye.1", "Something that cannot be sold");
        translationBuilder.add("item.eye_of_dreams.eye.2", "Stories swim within the haze");
        translationBuilder.add("item.eye_of_dreams.eye.3", "Their fragments found by the mind's gaze");
        translationBuilder.add("item.eye_of_dreams.eye.4", "Wonders blur the path of real");
        translationBuilder.add("item.eye_of_dreams.eye.5", "Half-formed hopes pursued with zeal");
        translationBuilder.add("item.eye_of_dreams.eye.6", "Dreamers' blue, dreamers' gold");
        translationBuilder.add("item.eye_of_dreams.eye.7", "Countless tales are left untold");

        translationBuilder.add(EyeOfDreams.REVERSE_TOTEM, "Eye Triggers");
    }
}
