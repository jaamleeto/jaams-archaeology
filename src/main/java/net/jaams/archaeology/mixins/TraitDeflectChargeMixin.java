package net.jaams.archaeology.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.util.ModTraits;
import net.jaams.archaeology.particle.EnhancedHitParticleData;
import net.jaams.archaeology.init.JaamsArchaeologyModEnchantments;
import net.jaams.archaeology.handler.traits.DeflectChargeHandler;

@Mixin(ItemStack.class)
public abstract class TraitDeflectChargeMixin {
	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void onDeflectChargeGetUseDuration(CallbackInfoReturnable<Integer> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (ModTraits.isDeflectChargeItem(stack)) {
			cir.setReturnValue(72000);
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void onDeflectChargeGetUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (ModTraits.isDeflectChargeItem(stack)) {
			cir.setReturnValue(UseAnim.BLOCK);
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void onDeflectChargeUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (!ModTraits.isDeflectChargeItem(stack))
			return;
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains("DeflectOriginalColor")) {
			int current = ModUtils.getCurrentColor(stack);
			if (current != -1) {
				tag.putInt("DeflectOriginalColor", current);
			}
		}
		if (!ModUtils.isEntityInBattleMode(player)) {
			player.startUsingItem(hand);
			cir.setReturnValue(InteractionResultHolder.consume(stack));
		} else {
			cir.setReturnValue(InteractionResultHolder.consume(stack));
		}
	}

	@Inject(method = "inventoryTick", at = @At("TAIL"))
	private void cleanupDeflectCharge(Level level, Entity entity, int slot, boolean selected, CallbackInfo ci) {
		if (!(entity instanceof Player player))
			return;
		ItemStack stack = (ItemStack) (Object) this;
		if (!ModTraits.isDeflectChargeItem(stack))
			return;
		CompoundTag tag = stack.getTag();
		if (tag == null)
			return;
		boolean inHand = selected || player.getOffhandItem() == stack;
		boolean hasStrikehold = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), stack) > 0;
		if (!inHand && !hasStrikehold) {
			if (tag.contains("DeflectAmount") || tag.contains("LastDeflectColorLevel")) {
				tag.putDouble("DeflectAmount", 0);
				DeflectChargeHandler.restoreOriginalColor(stack, tag);
				DeflectChargeHandler.clearAllDeflectData(stack);
				tag.remove("LastDeflectColorLevel");
			}
		}
	}

	@Inject(method = "hurtEnemy", at = @At("HEAD"))
	private void onDeflectChargeHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
		ItemStack stack = (ItemStack) (Object) this;
		if (!ModTraits.isDeflectChargeItem(stack) || target == null || attacker == null)
			return;
		CompoundTag tag = stack.getOrCreateTag();
		double deflectAmount = tag.getDouble("DeflectAmount");
		if (deflectAmount <= 0)
			return;
		int vengeanceLevel = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.VENGEANCE.get(), stack);
		float vengeanceMultiplier = vengeanceLevel > 0 ? 1.0f + 0.1f * vengeanceLevel : 1.0f;
		float adjustedDeflect = (float) (deflectAmount * vengeanceMultiplier);
		ModUtils.applyBonusDamage(attacker, target, stack, adjustedDeflect);
		Level level = target.level();
		if (level instanceof ServerLevel serverLevel) {
			double x = target.getX();
			double y = target.getY() + target.getBbHeight() / 2.0;
			double z = target.getZ();
			EnhancedHitParticleData particle = DeflectChargeHandler.getDeflectParticleData(deflectAmount);
			int count = 10 + vengeanceLevel * 4;
			for (int i = 0; i < count; i++) {
				double ox = serverLevel.random.nextGaussian() * 0.4;
				double oy = serverLevel.random.nextGaussian() * 0.35;
				double oz = serverLevel.random.nextGaussian() * 0.4;
				serverLevel.sendParticles(particle, x + ox, y + oy, z + oz, 1, 0.35, 0.35, 0.35, 0.08 + (deflectAmount / 120.0));
			}
			SoundEvent sound = vengeanceLevel > 0 ? SoundEvents.PLAYER_ATTACK_CRIT : SoundEvents.PLAYER_ATTACK_STRONG;
			level.playSound(null, x, y, z, sound, SoundSource.PLAYERS, 1.0f, 0.9f + level.random.nextFloat() * 0.2f);
		}
		DeflectChargeHandler.clearDeflectTags(stack);
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"))
	private void onReleaseDeflectCharge(Level level, LivingEntity livingEntity, int timeCharged, CallbackInfo ci) {
		ItemStack stack = (ItemStack) (Object) this;
		if (!ModTraits.isDeflectChargeItem(stack))
			return;
		if (!(livingEntity instanceof Player player))
			return;
		if (player.isCrouching()) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
			return;
		}
		boolean hasStrikehold = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), stack) > 0;
		int cooldownTicks = DeflectChargeHandler.getReleaseCooldownTicks(stack);
		player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
		float deflectAmount = DeflectChargeHandler.getDeflectAmount(stack);
		SoundEvent sound = (deflectAmount > 0.0F && !hasStrikehold) ? SoundEvents.PLAYER_ATTACK_SWEEP : SoundEvents.PLAYER_ATTACK_WEAK;
		level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), 1.0F, 1.0F);
		if (level instanceof ServerLevel serverLevel) {
			double range = DeflectChargeHandler.getAreaRange(stack);
			if (player.isUnderWater()) {
				DeflectChargeHandler.spawnBubbleSweep(serverLevel, player, (float) range, DeflectChargeHandler.PARTICLE_SIZE_DEFAULT);
			}
			if (!hasStrikehold) {
				DeflectChargeHandler.spawnParticleInFront(serverLevel, player, stack);
			}
		}
		if (!hasStrikehold) {
			float damageToApply = deflectAmount > 0.0F ? deflectAmount : DeflectChargeHandler.getBaseAreaDamage(stack);
			int hitCount = DeflectChargeHandler.applyAreaDamage(player, stack);
			if (hitCount > 0) {
				stack.hurtAndBreak(hitCount, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
			}
			DeflectChargeHandler.clearAllDeflectData(stack);
		}
	}
}
