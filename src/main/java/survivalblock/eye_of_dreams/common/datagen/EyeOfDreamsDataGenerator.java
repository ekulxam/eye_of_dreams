package survivalblock.eye_of_dreams.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EyeOfDreamsDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(EyeOfDreamsEnUsLangGenerator::new);
		pack.addProvider(EyeOfDreamsModelGenerator::new);
		pack.addProvider(EyeOfDreamsSoundGenerator::new);
		pack.addProvider(EyeOfDreamsBlockTagGenerator::new);
		pack.addProvider(EyeOfDreamsEntityTypeTagGenerator::new);
		pack.addProvider(EyeOfDreamsItemTagGenerator::new);
	}
}
