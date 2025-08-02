package survivalblock.eye_of_dreams.common;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADD_VALUE;

@SuppressWarnings("UnstableApiUsage")
public class EyeOfDreams implements ModInitializer {

	public static final String MOD_ID = "eye_of_dreams";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier MOVEMENT_SPEED = EyeOfDreams.id("movement_speed");
	public static final Identifier MOVEMENT_EFFICIENCY = EyeOfDreams.id("movement_efficiency");
	public static final Identifier KNOCKBACK_RESISTANCE = EyeOfDreams.id("knockback_resistance");
	public static final Identifier JUMP_STRENGTH = EyeOfDreams.id("jump_strength");
	public static final Identifier SAFE_FALL_DISTANCE = EyeOfDreams.id("safe_fall_distance");
	public static final Identifier STEP_HEIGHT = EyeOfDreams.id("step_height");

	public static final Map<RegistryEntry<EntityAttribute>, EntityAttributeModifier> EYE_MODIFIERS_MAP = Util.make(ImmutableMap.<RegistryEntry<EntityAttribute>, EntityAttributeModifier>builder(), builder -> {
		builder.put(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(MOVEMENT_SPEED, 0.2, ADD_VALUE));
		builder.put(EntityAttributes.MOVEMENT_EFFICIENCY, new EntityAttributeModifier(MOVEMENT_EFFICIENCY, 0.2, ADD_VALUE));
		builder.put(EntityAttributes.KNOCKBACK_RESISTANCE, new EntityAttributeModifier(KNOCKBACK_RESISTANCE, 0.5, ADD_VALUE));
		builder.put(EntityAttributes.JUMP_STRENGTH, new EntityAttributeModifier(JUMP_STRENGTH, 0.5, ADD_VALUE));
		builder.put(EntityAttributes.SAFE_FALL_DISTANCE, new EntityAttributeModifier(SAFE_FALL_DISTANCE, 5, ADD_VALUE));
		builder.put(EntityAttributes.STEP_HEIGHT, new EntityAttributeModifier(STEP_HEIGHT, 0.5, ADD_VALUE));
	}).build();

	public static final AttachmentType<Boolean> SLUMBERING = AttachmentRegistry.create(
			id("slumbering"),
			builder -> builder.initializer(() -> false)
                .persistent(Codec.BOOL)
                .syncWith(PacketCodecs.BOOLEAN, AttachmentSyncPredicate.all())
	);

	public static final Eye EYE = registerItem("eye", Eye::new,
			new Item.Settings()
					.rarity(Rarity.EPIC)
					.maxCount(1)
					.fireproof()
					.equippable(EquipmentSlot.HEAD)
	);

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
			entries.addAfter(Items.ENDER_EYE, EYE);
		});
		AttackBlockCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
		AttackEntityCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
		UseBlockCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
		UseEntityCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
		UseItemCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
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

	public static boolean canSee(boolean original, Entity me, Entity other) {
		if (me instanceof PhantomEntity) {
			return original;
		}
		if (me == null || other == null) {
			return original;
		}
		if (!me.isAlive() || !other.isAlive()) {
			return original;
		}
		return original && me.getAttachedOrElse(SLUMBERING, false) == other.getAttachedOrElse(SLUMBERING, false);
	}

	public static ActionResult cancelActionIfSlumbering(PlayerEntity player, Object... otherArgs) {
		if (player.isSpectator()) {
			return ActionResult.PASS;
		}
		if (player.getAttachedOrCreate(SLUMBERING)) {
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
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
	}
}