package survivalblock.eye_of_dreams.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

public class EyeOfDreamsClient implements ClientModInitializer {

    public static final Identifier DREAMING_SHADER = EyeOfDreams.id("slumber");
    public static final Identifier CROSSHAIR = EyeOfDreams.id("hud/crosshair");

    @Override
    public void onInitializeClient() {

    }
}
