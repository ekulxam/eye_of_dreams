package survivalblock.eye_of_dreams.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import survivalblock.eye_of_dreams.client.slumber.EyeOfDreamsRenderPipelines;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import static survivalblock.eye_of_dreams.common.EyeOfDreams.SLUMBERING;

public class EyeOfDreamsClient implements ClientModInitializer {

    public static final Identifier CROSSHAIR = EyeOfDreams.id("hud/crosshair");

    public static final int MAX_PROGRESS = 20;

    public static int shaderProgress = 0;
    public static int targetProgress = 0;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitializeClient() {
        EyeOfDreamsRenderPipelines.init();
        ClientTickEvents.START_WORLD_TICK.register(clientWorld -> {
            MinecraftClient client = MinecraftClient.getInstance();
            Entity focused = client.getCameraEntity();
            if (focused == null) {
                if (client.player == null) {
                    targetProgress = 0;
                } else {
                    if (!client.player.getAttachedOrCreate(SLUMBERING)) {
                        targetProgress = 0; // if not slumbering, target progress is 0
                    } else {
                        targetProgress = MAX_PROGRESS;
                    }
                }
            } else {
                if (!focused.getAttachedOrElse(SLUMBERING, false)) {
                    targetProgress = 0;
                } else {
                    targetProgress = MAX_PROGRESS;
                }
            }
            shaderProgress = stepTowards(shaderProgress, targetProgress, 1);
        });
    }

    // step should always be positive
    public static int stepTowards(int from, int to, int step) {
        if (from == to) {
            return from;
        }
        return from < to ? MathHelper.clamp(from + step, from, to) : MathHelper.clamp(from - step, to, from);
    }
}
