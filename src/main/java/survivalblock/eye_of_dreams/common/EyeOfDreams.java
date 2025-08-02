package survivalblock.eye_of_dreams.common;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class EyeOfDreams implements ModInitializer {

	public static final String MOD_ID = "eye_of_dreams";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final AttachmentType<Boolean> SLUMBERING = AttachmentRegistry.create(
			id("slumbering"),
			builder -> builder.initializer(() -> false)
                .persistent(Codec.BOOL)
                .syncWith(PacketCodecs.BOOLEAN, AttachmentSyncPredicate.all())
	);

	public static final Eye EYE = registerItem("eye", Eye::new, new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().useCooldown(4));

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
			entries.addAfter(Items.ENDER_EYE, EYE);
		});
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static <T extends Item> T registerItem(String name, Function<Item.Settings, T> itemFromSettings, Item.Settings settings) {
		Identifier id = id(name);
		RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, id);
		return Registry.register(Registries.ITEM, id, itemFromSettings.apply(settings.registryKey(registryKey)));
	}

	public static MutableText scrollingGradient(Text original, int wrap, float reciprocalWrap, int startColor, int endColor, boolean forward) {
		String blessed = original.getString();
		int length = blessed.length();
		long time = Util.getMeasuringTimeMs();
		MutableText text = Text.literal(blessed.substring(0, 1)).withColor(ColorHelper.lerp(((time) % wrap) * reciprocalWrap, startColor, endColor));
		float incr = (float) wrap / length;
		for (int i = 1; i < length; i++) {
			float deltaHalfCalculated;
			if (forward) {
				deltaHalfCalculated = time - (int) (incr * i);
			} else {
				deltaHalfCalculated = time + (int) (incr * i);
			}
			text.append(Text.literal(blessed.substring(i, i + 1)).withColor(ColorHelper.lerp((deltaHalfCalculated % wrap) * reciprocalWrap, startColor, endColor)));
		}
		return text;
	}

	public static final class Eye extends Item {

		public static final int BLUE = Color.CYAN.getRGB();
		public static final int GOLD = Color.ORANGE.getRGB();

		public Eye(Settings settings) {
			super(settings);
		}

		@Override
		public Text getName(ItemStack stack) {
			Text text = super.getName(stack);
			if (!Text.translatable(this.getTranslationKey()).equals(text)) {
				return text;
			}
			return scrollingGradient(text, 1000, 0.001F, BLUE, GOLD, true);
		}

		@Override
		public ActionResult use(World world, PlayerEntity user, Hand hand) {
			if (!world.isClient) {
				user.setAttached(SLUMBERING, !user.getAttachedOrCreate(SLUMBERING));
				return ActionResult.SUCCESS_SERVER;
			}
			return ActionResult.SUCCESS;
		}
	}
}