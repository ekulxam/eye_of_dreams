package survivalblock.eye_of_dreams.mixin.eye.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.eye_of_dreams.client.EyeOfDreamsClient;

import static survivalblock.eye_of_dreams.common.EyeOfDreams.EYE_ITEM;
import static survivalblock.eye_of_dreams.common.EyeOfDreams.SLUMBERING;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow private ItemStack currentStack;

    @SuppressWarnings("UnstableApiUsage")
    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;CROSSHAIR_TEXTURE:Lnet/minecraft/util/Identifier;"))
    private Identifier useAltCrosshair(Identifier original) {
        Entity focused = this.client.getCameraEntity();
        if (focused == null) {
            if (this.client.player == null || !this.client.player.getAttachedOrCreate(SLUMBERING)) {
                return original;
            }
        } else {
            if (!focused.getAttachedOrElse(SLUMBERING, false)) {
                return original;
            }
        }
        return EyeOfDreamsClient.CROSSHAIR;
    }

    @WrapOperation(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;equals(Ljava/lang/Object;)Z"))
    private boolean letMeScrollInPeace(Text instance, Object o, Operation<Boolean> original, @Local ItemStack stack) {
        if (!(o instanceof Text)) {
            return original.call(instance, o);
        }
        if (stack == null || stack.isEmpty() || this.currentStack == null || this.currentStack.isEmpty()) {
            return original.call(instance, o);
        }
        if (!stack.isOf(EYE_ITEM) || !this.currentStack.isOf(EYE_ITEM)) {
            return original.call(instance, o);
        }
        return ItemStack.areItemsAndComponentsEqual(stack, this.currentStack);
    }
}
