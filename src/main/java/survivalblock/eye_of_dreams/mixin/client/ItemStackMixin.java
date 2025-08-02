package survivalblock.eye_of_dreams.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@Mixin(value = ItemStack.class, priority = 2000)
public abstract class ItemStackMixin {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow public abstract boolean isOf(Item item);

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void lore(Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        if (!this.isOf(EyeOfDreams.EYE)) {
            return;
        }
        if (!Screen.hasShiftDown()) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            textConsumer.accept(Text.translatable("item.eye_of_dreams.eye." + i).formatted(Formatting.LIGHT_PURPLE));
        }
        ci.cancel();
    }

    @SuppressWarnings("DiscouragedShift")
    @Inject(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/tooltip/TooltipType;isAdvanced()Z", shift = At.Shift.BEFORE))
    private void shiftToOpen(Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        if (!this.isOf(EyeOfDreams.EYE)) {
            return;
        }
        if (Screen.hasShiftDown()) {
            return;
        }
        textConsumer.accept(Text.translatable("item.eye_of_dreams.eye.hidden").formatted(Formatting.DARK_GRAY));
    }

}
