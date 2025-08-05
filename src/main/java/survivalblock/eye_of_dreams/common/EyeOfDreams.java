package survivalblock.eye_of_dreams.common;

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
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static final TagKey<Item> ALLOW_USE_WHILE_SLUMBERING = TagKey.of(RegistryKeys.ITEM, id("allow_use_while_slumbering"));

	public static final EyeItem EYE_ITEM = registerItem("eye", EyeItem::new,
			new Item.Settings()
					.rarity(Rarity.EPIC)
					.maxCount(1)
					.fireproof()
					.equippable(EquipmentSlot.HEAD)
	);

	public static final SoundEvent REVERSE_TOTEM = registerSound("eye_triggers");

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
			entries.addAfter(Items.ENDER_EYE, EYE_ITEM);
		});
		AttackBlockCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
		AttackEntityCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
		UseBlockCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
		UseEntityCallback.EVENT.register(EyeOfDreams::cancelActionIfSlumbering);
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getStackInHand(hand);
			if (stack.isIn(ALLOW_USE_WHILE_SLUMBERING)) {
				return ActionResult.PASS;
			}
			return EyeOfDreams.cancelActionIfSlumbering(player);
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

	public static SoundEvent registerSound(String name) {
		Identifier id = id(name);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
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
}