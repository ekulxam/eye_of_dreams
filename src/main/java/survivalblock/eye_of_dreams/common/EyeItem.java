package survivalblock.eye_of_dreams.common;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

import java.awt.Color;
import java.util.Map;

import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADD_VALUE;

public class EyeItem extends Item {

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

    public static final int BLUE = Color.CYAN.getRGB();
    public static final int GOLD = Color.ORANGE.getRGB();

    public EyeItem(Settings settings) {
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
}