package survivalblock.eye_of_dreams.mixin.eye;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.eye_of_dreams.common.EyeOfDreams;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;update(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void movingIsExhausting(HungerManager instance, ServerPlayerEntity player, Operation<Void> original) {
        original.call(instance, player);
        if (player.getAttachedOrCreate(EyeOfDreams.SLUMBERING)) {
            // don't mind me
            original.call(instance, player);
            original.call(instance, player);
            original.call(instance, player);
        }
    }
}
