package net.jaams.archaeology.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import org.joml.Vector3f;
import org.joml.Quaternionf;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.Tag;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.util.ModTags;
import net.jaams.archaeology.util.ModEnums;
import net.jaams.archaeology.init.JaamsArchaeologyModSounds;
import net.jaams.archaeology.entity.SpearFragmentProjectileEntity;
import net.jaams.archaeology.entity.PrimitiveSpearProjectileEntity;
import net.jaams.archaeology.configuration.common.TraitsConfig;
import net.jaams.archaeology.configuration.common.ThrowableConfig;
import net.jaams.archaeology.configuration.common.ProjectileCommonConfig;
import net.jaams.archaeology.component.BaseWeaponProjectileEntity;

@Mixin(ItemStack.class)
public class TraitThrowableMixin {
	@Unique
	private boolean isPrimitiveSpear(ItemStack stack) {
		return stack.is(ModTags.PRIMITIVE_SPEARS) || (stack.hasTag() && stack.getTag().getBoolean("ForcePrimitiveSpearThrowable"));
	}

	@Unique
	private boolean isSpearFragment(ItemStack stack) {
		return stack.is(ModTags.SPEAR_FRAGMENTS) || (stack.hasTag() && stack.getTag().getBoolean("ForceSpearFragmentThrowable"));
	}

	@Unique
	private boolean isThrowableType(ItemStack stack) {
		return isPrimitiveSpear(stack) || isSpearFragment(stack);
	}

	@Unique
	private boolean isThrowableEnabled(ItemStack stack) {
		if (!TraitsConfig.THROWABLE.get()) {
			return false;
		}
		if (!isPrimitiveSpear(stack) && !isSpearFragment(stack)) {
			return false;
		}
		if (isPrimitiveSpear(stack) && !ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR.get()) {
			return false;
		}
		if (isSpearFragment(stack) && !ThrowableConfig.THROWABLE_SPEAR_FRAGMENT.get()) {
			return false;
		}
		if (stack.hasTag() && stack.getTag().contains("ThrowableTrait")) {
			return stack.getTag().getBoolean("ThrowableTrait");
		}
		return true;
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void onThrowableUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		ItemStack stack = player.getItemInHand(hand);
		if (hand == InteractionHand.MAIN_HAND && player.isCrouching() && ModUtils.isBlockableItem(player.getItemInHand(InteractionHand.OFF_HAND))) {
			return;
		}
		if (!isThrowableType(stack) || !isThrowableEnabled(stack)) {
			return;
		}
		if (!canThrowWithDurabilityCheck(stack)) {
			cir.setReturnValue(InteractionResultHolder.fail(stack));
			return;
		}
		ModEnums.ThrowMode mode = getThrowMode(stack);
		if (mode == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK) {
			throwInstant(level, player, hand, stack);
			if (!level.isClientSide) {
				player.swing(hand, true);
			}
			cir.setReturnValue(InteractionResultHolder.consume(stack));
			return;
		}
		player.startUsingItem(hand);
		cir.setReturnValue(InteractionResultHolder.consume(stack));
	}

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void modifyThrowableUseDuration(CallbackInfoReturnable<Integer> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (!isThrowableType(stack) || !isThrowableEnabled(stack)) {
			return;
		}
		int duration = isPrimitiveSpear(stack)
				? ModUtils.getConfigOrNbtInt(stack, "UseDurationTicks", ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_USE_DURATION_TICKS::get)
				: ModUtils.getConfigOrNbtInt(stack, "UseDurationTicks", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_USE_DURATION_TICKS::get);
		cir.setReturnValue(duration);
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void changeThrowableUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (!isThrowableType(stack) || !isThrowableEnabled(stack)) {
			return;
		}
		UseAnim anim = isPrimitiveSpear(stack)
				? ModUtils.getConfigOrNbtEnum(stack, "UseAnimation", ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_USE_ANIMATION::get)
				: ModUtils.getConfigOrNbtEnum(stack, "UseAnimation", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_USE_ANIMATION::get);
		cir.setReturnValue(anim);
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"))
	private void onReleaseThrowable(Level level, LivingEntity entity, int durationUsed, CallbackInfo ci) {
		ItemStack stack = (ItemStack) (Object) this;
		if (!(entity instanceof Player player))
			return;
		if (!isThrowableType(stack) || !isThrowableEnabled(stack))
			return;
		ModEnums.ThrowMode mode = getThrowMode(stack);
		if (mode != ModEnums.ThrowMode.CHARGE_AND_RELEASE && mode != ModEnums.ThrowMode.CHARGE_RELEASE_AND_FINISH) {
			return;
		}
		int remaining = stack.getUseDuration() - durationUsed;
		int minCharge = getMinChargeTicks(stack);
		int maxCharge = getMaxChargeTicks(stack);
		if (remaining < minCharge)
			return;
		float power = ModUtils.getPowerForTime(remaining, minCharge, maxCharge);
		throwCharged(level, player, player.getUsedItemHand(), stack, power);
	}

	@Inject(method = "finishUsingItem", at = @At("HEAD"))
	private void onFinishUsing(Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
		if (!(entity instanceof Player player))
			return;
		ItemStack stack = player.getItemInHand(player.getUsedItemHand());
		if (!isThrowableType(stack) || !isThrowableEnabled(stack))
			return;
		ModEnums.ThrowMode mode = getThrowMode(stack);
		if (mode != ModEnums.ThrowMode.CHARGE_AND_FINISH_USING && mode != ModEnums.ThrowMode.CHARGE_RELEASE_AND_FINISH) {
			return;
		}
		throwCharged(level, player, player.getUsedItemHand(), stack, 1.0f);
	}

	@Unique
	private ModEnums.ThrowMode getThrowMode(ItemStack stack) {
		if (isPrimitiveSpear(stack)) {
			return ModUtils.getConfigOrNbtEnum(stack, "ThrowMode", () -> ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_THROW_MODE.get());
		}
		return ModUtils.getConfigOrNbtEnum(stack, "ThrowMode", () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_THROW_MODE.get());
	}

	@Unique
	private int getMinChargeTicks(ItemStack stack) {
		if (isPrimitiveSpear(stack)) {
			return ModUtils.getConfigOrNbtInt(stack, "MinChargeTicks", ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_MIN_CHARGE::get);
		}
		return ModUtils.getConfigOrNbtInt(stack, "MinChargeTicks", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MIN_CHARGE::get);
	}

	@Unique
	private int getMaxChargeTicks(ItemStack stack) {
		if (isPrimitiveSpear(stack)) {
			return ModUtils.getConfigOrNbtInt(stack, "MaxChargeTicks", ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_MAX_CHARGE::get);
		}
		return ModUtils.getConfigOrNbtInt(stack, "MaxChargeTicks", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MAX_CHARGE::get);
	}

	@Unique
	private void throwInstant(Level level, Player player, InteractionHand hand, ItemStack stack) {
		boolean isCreative = player.getAbilities().instabuild;
		int cooldown = isPrimitiveSpear(stack)
				? ModUtils.getConfigOrNbtInt(stack, "InstantCooldownTicks", ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_COOLDOWN_TICKS_INSTANT::get)
				: ModUtils.getConfigOrNbtInt(stack, "InstantCooldownTicks", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_COOLDOWN_TICKS_INSTANT::get);
		if (!level.isClientSide) {
			player.getCooldowns().addCooldown(stack.getItem(), cooldown);
		}
		boolean success = performThrowWithDamageCheck(level, player, hand, stack, 1.0f);
		if (success && !level.isClientSide && !isCreative) {
			stack.shrink(1);
			if (stack.isEmpty())
				player.setItemInHand(hand, ItemStack.EMPTY);
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
		}
	}

	@Unique
	private void throwCharged(Level level, Player player, InteractionHand hand, ItemStack stack, float power) {
		boolean isCreative = player.getAbilities().instabuild;
		boolean success = performThrowWithDamageCheck(level, player, hand, stack, power);
		if (success && !level.isClientSide && !isCreative) {
			stack.shrink(1);
			if (stack.isEmpty())
				player.setItemInHand(hand, ItemStack.EMPTY);
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
		}
	}

	@Unique
	private boolean performThrowWithDamageCheck(Level level, Player player, InteractionHand hand, ItemStack stack, float power) {
		boolean isCreative = player.getAbilities().instabuild;
		if (!isCreative && !level.isClientSide && ModUtils.hasDurability(stack)) {
			int damageAmount = isPrimitiveSpear(stack)
					? ModUtils.getConfigOrNbtInt(stack, "DamageOnThrow", ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_DAMAGE_AMOUNT_ON_THROW::get)
					: ModUtils.getConfigOrNbtInt(stack, "DamageOnThrow", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_DAMAGE_AMOUNT_ON_THROW::get);
			if (stack.getDamageValue() + damageAmount >= stack.getMaxDamage()) {
				stack.hurtAndBreak(damageAmount, player, p -> p.broadcastBreakEvent(hand));
				return false;
			}
			stack.hurtAndBreak(damageAmount, player, p -> p.broadcastBreakEvent(hand));
		}
		performThrow(level, player, hand, stack, power);
		return true;
	}

	@Unique
	private boolean canThrowWithDurabilityCheck(ItemStack stack) {
		if (!ModUtils.hasDurability(stack))
			return true;
		int minRemaining = isPrimitiveSpear(stack)
				? ModUtils.getConfigOrNbtInt(stack, "MinRemainingDurability", ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_MIN_REMAINING_DURABILITY::get)
				: ModUtils.getConfigOrNbtInt(stack, "MinRemainingDurability", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MIN_REMAINING_DURABILITY::get);
		int currentRemaining = stack.getMaxDamage() - stack.getDamageValue();
		if (minRemaining == 0)
			return true;
		if (minRemaining == -1)
			return currentRemaining >= 2;
		return currentRemaining >= minRemaining;
	}

	@Unique
	private void performThrow(Level level, Player player, InteractionHand hand, ItemStack stack, float power) {
		boolean isPrimitiveSpear = isPrimitiveSpear(stack);
		float minSpeed = ModUtils.getConfigOrNbtFloat(stack, "MinSpeed", isPrimitiveSpear ? () -> ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_MIN_SPEED.get().floatValue() : () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MIN_SPEED.get().floatValue());
		float maxSpeed = ModUtils.getConfigOrNbtFloat(stack, "MaxSpeed", isPrimitiveSpear ? () -> ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_MAX_SPEED.get().floatValue() : () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MAX_SPEED.get().floatValue());
		float speed = minSpeed + (maxSpeed - minSpeed) * power * power;
		boolean isCreative = player.getAbilities().instabuild;
		boolean allowMultishot = ModUtils.getConfigOrNbtBoolean(stack, "MultishotAllowed", isPrimitiveSpear ? ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_ALLOW_MULTISHOT::get : ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_ALLOW_MULTISHOT::get);
		int multishotLevel = allowMultishot ? EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack) : 0;
		int projectileCount = multishotLevel > 0 ? 3 : 1;
		float spreadAngle = ModUtils.getConfigOrNbtFloat(stack, "MultishotSpreadAngle",
				isPrimitiveSpear ? () -> ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_MULTISHOT_SPREAD_ANGLE.get().floatValue() : () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MULTISHOT_SPREAD_ANGLE.get().floatValue());
		float[] shotPitches = ModUtils.generateShotPitches(level.getRandom(), projectileCount);
		float inaccuracy = ModUtils.getConfigOrNbtFloat(stack, "InaccuracyDegrees", isPrimitiveSpear ? ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_INACCURACY::get : ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_INACCURACY::get);
		if (!level.isClientSide) {
			int flameLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack);
			boolean isFlaming = flameLevel > 0;
			for (int i = 0; i < projectileCount; i++) {
				ItemStack projectileStack = stack.copy();
				BaseWeaponProjectileEntity projectile;
				if (isPrimitiveSpear) {
					projectile = new PrimitiveSpearProjectileEntity(level, player, projectileStack);
				} else {
					projectile = new SpearFragmentProjectileEntity(level, player, projectileStack);
				}
				if (isFlaming) {
					projectile.setSecondsOnFire(100);
				}
				if ((isPrimitiveSpear ? ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_REMEMBER_SLOT.get() : ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_REMEMBER_SLOT.get())) {
					int slotIndex = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
					projectile.setOriginalSlotIndex(slotIndex);
				}
				boolean shouldBeCritical;
				ModEnums.ThrowMode mode = getThrowMode(stack);
				boolean isInstant = mode == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK;
				if (isInstant) {
					shouldBeCritical = ModUtils.isProjectileCritical(player, power);
				} else {
					float threshold = ModUtils.getConfigOrNbtFloat(stack, "CriticalPowerThreshold",
							isPrimitiveSpear ? () -> ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_CRITICAL_POWER_THRESHOLD.get().floatValue() : () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_CRITICAL_POWER_THRESHOLD.get().floatValue());
					shouldBeCritical = power >= threshold && ModUtils.isProjectileCritical(player, power);
				}
				if (shouldBeCritical) {
					projectile.setCritical(true);
				}
				int pierceLevel = stack.getTag() != null && stack.getTag().contains("PiercingLevel", Tag.TAG_INT)
						? stack.getTag().getInt("PiercingLevel")
						: (isPrimitiveSpear ? ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_PIERCING_LEVEL.get() : ProjectileCommonConfig.SPEAR_FRAGMENT_PROJECTILE_PIERCING_LEVEL.get());
				pierceLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, stack);
				projectile.setPierceLevel(Math.max(0, pierceLevel));
				if (multishotLevel > 0) {
					projectile.pickup = isCreative ? AbstractArrow.Pickup.CREATIVE_ONLY : (i == 0 ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED);
					if (i != 0) {
						projectile.getPersistentData().putBoolean("IsMultishotClone", true);
					}
					float rotation = (i == 0) ? 0.0F : (i == 1) ? -spreadAngle : spreadAngle;
					Vec3 upVector = player.getUpVector(1.0F);
					Quaternionf rotationQuaternion = new Quaternionf().setAngleAxis(rotation * (float) Math.PI / 180F, (float) upVector.x, (float) upVector.y, (float) upVector.z);
					Vec3 viewVector = player.getViewVector(1.0F);
					Vector3f rotatedVector = new Vector3f((float) viewVector.x, (float) viewVector.y, (float) viewVector.z).rotate(rotationQuaternion);
					if (i == 0) {
						float forwardOffset = 1.0F;
						Vec3 offset = viewVector.scale(forwardOffset);
						projectile.setPos(player.getX() + offset.x, player.getEyeY() + offset.y, player.getZ() + offset.z);
					}
					projectile.shoot(rotatedVector.x(), rotatedVector.y(), rotatedVector.z(), speed, inaccuracy);
				} else {
					projectile.pickup = isCreative ? AbstractArrow.Pickup.CREATIVE_ONLY : AbstractArrow.Pickup.ALLOWED;
					projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, inaccuracy);
				}
				projectile.setWeaponItem(stack.copy());
				level.addFreshEntity(projectile);
				SoundEvent shootSound;
				if (stack.hasTag() && stack.getTag().getBoolean("GenericSoundSet")) {
					shootSound = JaamsArchaeologyModSounds.PROJECTILE_THROW.get();
				} else {
					shootSound = isPrimitiveSpear ? JaamsArchaeologyModSounds.PRIMITIVE_SPEAR_FIRED.get() : JaamsArchaeologyModSounds.SPEAR_FRAGMENT_FIRED.get();
				}
				level.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(), shootSound, SoundSource.PLAYERS, 1.0F, shotPitches[i]);
			}
			boolean fullChargeOnlyRecoil = ModUtils.getConfigOrNbtBoolean(stack, "RecoilOnlyFullCharge",
					isPrimitiveSpear ? ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_RECOIL_ONLY_FULLY_CHARGED::get : ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_RECOIL_ONLY_FULLY_CHARGED::get);
			boolean shouldApplyRecoil = !fullChargeOnlyRecoil || power >= 0.99f;
			if (shouldApplyRecoil) {
				float recoilBase = ModUtils.getConfigOrNbtFloat(stack, "RecoilDistance", isPrimitiveSpear ? ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_RECOIL::get : ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_RECOIL::get);
				float recoilDistance = recoilBase * power;
				float crouchReduction = ModUtils.getConfigOrNbtFloat(stack, "RecoilCrouchReduction",
						isPrimitiveSpear ? ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_RECOIL_CROUCH_REDUCTION::get : ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_RECOIL_CROUCH_REDUCTION::get);
				float verticalMult = ModUtils.getConfigOrNbtFloat(stack, "RecoilVerticalMultiplier",
						isPrimitiveSpear ? ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_RECOIL_VERTICAL_MULTIPLIER::get : ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_RECOIL_VERTICAL_MULTIPLIER::get);
				ModUtils.applyRecoil(player, recoilDistance, crouchReduction, verticalMult);
			}
		} else {
			float recoilPitchKick = ModUtils.getConfigOrNbtFloat(stack, "RecoilPitchKick", isPrimitiveSpear ? ThrowableConfig.THROWABLE_PRIMITIVE_SPEAR_RECOIL_PITCH_KICK::get : ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_RECOIL_PITCH_KICK::get);
			float pitchChange = recoilPitchKick * power;
			player.setXRot(player.getXRot() - pitchChange);
		}
	}
}
