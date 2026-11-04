package net.jaams.archaeology.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;

import net.jaams.archaeology.util.ModTraits;
import net.jaams.archaeology.handler.traits.DeflectChargeHandler;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE))
	private void onHurtAndBreakBeforeBreak(int amount, LivingEntity entity, Consumer<LivingEntity> onBroken, CallbackInfo ci) {
		ItemStack stack = (ItemStack) (Object) this;
		if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
			if (ModTraits.isDeflectChargeItem(stack)) {
				float charge = DeflectChargeHandler.getDeflectAmount(stack);
				if (charge > 0 && DeflectChargeHandler.shouldReleaseOnBreak()) {
					DeflectChargeHandler.releaseOnBreak(stack.copy(), entity);
				}
			}
		}
	}
}
