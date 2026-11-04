package net.jaams.archaeology.tooltip;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.util.ModTags;
import net.jaams.archaeology.util.ModEnums;
import net.jaams.archaeology.configuration.common.TraitsConfig;
import net.jaams.archaeology.configuration.common.ThrowableConfig;
import net.jaams.archaeology.configuration.common.ProjectileCommonConfig;
import net.jaams.archaeology.configuration.client.TooltipsConfig;

import java.util.List;

public class SpearFragmentItemTooltip {
	public static void add(ItemStack stack, List<Component> tooltip) {
		if (!isSpearFragment(stack)) {
			return;
		}
		if (!isThrowableEnabled(stack)) {
			return;
		}
		ModEnums.ThrowMode throwMode = ModUtils.getConfigOrNbtEnum(stack, "ThrowMode", () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_THROW_MODE.get());
		boolean isInstant = throwMode == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK;
		addMainTraits(stack, tooltip);
		addThrowingPropertiesIfEnabled(stack, tooltip, throwMode, isInstant);
		addThrowingModeIfEnabled(stack, tooltip, throwMode);
	}

	private static boolean isSpearFragment(ItemStack stack) {
		return stack.is(ModTags.SPEAR_FRAGMENTS) || (stack.hasTag() && stack.getTag().getBoolean("ForceSpearFragmentThrowable"));
	}

	private static boolean isThrowableEnabled(ItemStack stack) {
		if (!TraitsConfig.THROWABLE.get())
			return false;
		if (!isSpearFragment(stack))
			return false;
		if (!ThrowableConfig.THROWABLE_SPEAR_FRAGMENT.get())
			return false;
		if (stack.hasTag() && stack.getTag().contains("ThrowableTrait")) {
			return stack.getTag().getBoolean("ThrowableTrait");
		}
		return true;
	}

	private static void addMainTraits(ItemStack stack, List<Component> tooltip) {
		ModUtils.addTrait(stack, tooltip, "tooltip.jaams_archaeology.trait.throwable", "tooltip.jaams_archaeology.trait.throwable.desc");
	}

	private static void addThrowingPropertiesIfEnabled(ItemStack stack, List<Component> tooltip, ModEnums.ThrowMode throwMode, boolean isInstant) {
		if (!TooltipsConfig.TOOLTIP_THROWING_PROPERTIES.get()) {
			return;
		}
		ModUtils.addExtraInfo(stack, tooltip, "tooltip.jaams_archaeology.properties.throwing", ChatFormatting.GOLD);
		addBaseDamage(stack, tooltip);
		addBaseKnockback(stack, tooltip);
		addChargeTimesIfApplicable(stack, tooltip, isInstant);
		addSpeedStats(stack, tooltip, isInstant);
		addInaccuracy(stack, tooltip);
		addPiercingLevel(stack, tooltip);
		addCooldownIfInstant(stack, tooltip, isInstant);
		addRecoil(stack, tooltip);
		addWaterInertia(stack, tooltip);
	}

	private static void addBaseDamage(ItemStack stack, List<Component> tooltip) {
		double baseDamage = ModUtils.calculateDamage(stack);
		if (stack.hasTag() && stack.getTag().contains("BaseDamage", 99)) {
			baseDamage += stack.getTag().getDouble("BaseDamage");
		} else {
			baseDamage += ProjectileCommonConfig.SPEAR_FRAGMENT_PROJECTILE_BASE_DAMAGE.get();
		}
		double rounded = ModUtils.roundToTwoDecimals(baseDamage);
		ModUtils.addStat(stack, tooltip, "base_damage", rounded);
	}

	private static void addBaseKnockback(ItemStack stack, List<Component> tooltip) {
		double knockback = 0.0;
		if (stack.hasTag() && stack.getTag().contains("BaseKnockback", 99)) {
			knockback = stack.getTag().getDouble("BaseKnockback");
		} else {
			knockback = ProjectileCommonConfig.SPEAR_FRAGMENT_PROJECTILE_BASE_KNOCKBACK.get();
		}
		if (knockback > 0.0) {
			double rounded = ModUtils.roundToTwoDecimals(knockback);
			ModUtils.addStat(stack, tooltip, "base_knockback", rounded);
		}
	}

	private static void addChargeTimesIfApplicable(ItemStack stack, List<Component> tooltip, boolean isInstant) {
		if (isInstant)
			return;
		int minCharge = ModUtils.getConfigOrNbtInt(stack, "MinChargeTicks", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MIN_CHARGE::get);
		int maxCharge = ModUtils.getConfigOrNbtInt(stack, "MaxChargeTicks", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MAX_CHARGE::get);
		if (minCharge > 0) {
			double seconds = minCharge / 20.0;
			double rounded = ModUtils.roundToTwoDecimals(seconds);
			ModUtils.addStat(stack, tooltip, "min_charge", rounded);
		}
		if (maxCharge > 0) {
			double seconds = maxCharge / 20.0;
			double rounded = ModUtils.roundToTwoDecimals(seconds);
			ModUtils.addStat(stack, tooltip, "max_charge", rounded);
		}
	}

	private static void addSpeedStats(ItemStack stack, List<Component> tooltip, boolean isInstant) {
		double minSpeed = ModUtils.getConfigOrNbtDouble(stack, "MinSpeed", () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MIN_SPEED.get().doubleValue());
		double maxSpeed = ModUtils.getConfigOrNbtDouble(stack, "MaxSpeed", () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_MAX_SPEED.get().doubleValue());
		if (isInstant) {
			if (maxSpeed > 0.0) {
				double rounded = ModUtils.roundToTwoDecimals(maxSpeed);
				ModUtils.addStat(stack, tooltip, "throw_speed", rounded);
			}
		} else {
			if (minSpeed > 0.0) {
				double rounded = ModUtils.roundToTwoDecimals(minSpeed);
				ModUtils.addStat(stack, tooltip, "min_speed", rounded);
			}
			if (maxSpeed > 0.0) {
				double rounded = ModUtils.roundToTwoDecimals(maxSpeed);
				ModUtils.addStat(stack, tooltip, "max_speed", rounded);
			}
		}
	}

	private static void addInaccuracy(ItemStack stack, List<Component> tooltip) {
		double inaccuracy = ModUtils.getConfigOrNbtDouble(stack, "InaccuracyDegrees", () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_INACCURACY.get().doubleValue());
		if (inaccuracy > 0.1) {
			double rounded = ModUtils.roundToTwoDecimals(inaccuracy);
			ModUtils.addStat(stack, tooltip, "inaccuracy", rounded);
		}
	}

	private static void addPiercingLevel(ItemStack stack, List<Component> tooltip) {
		int piercingLevel = ProjectileCommonConfig.SPEAR_FRAGMENT_PROJECTILE_PIERCING_LEVEL.get();
		if (stack.hasTag() && stack.getTag().contains("PiercingLevel", 3)) {
			piercingLevel = stack.getTag().getInt("PiercingLevel");
		}
		piercingLevel += net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, stack);
		if (piercingLevel > 0) {
			ModUtils.addStat(stack, tooltip, "piercing_level", piercingLevel);
		}
	}

	private static void addCooldownIfInstant(ItemStack stack, List<Component> tooltip, boolean isInstant) {
		if (!isInstant)
			return;
		int cooldownTicks = ModUtils.getConfigOrNbtInt(stack, "InstantCooldownTicks", ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_COOLDOWN_TICKS_INSTANT::get);
		double seconds = cooldownTicks / 20.0;
		double rounded = ModUtils.roundToTwoDecimals(seconds);
		ModUtils.addStat(stack, tooltip, "cooldown", rounded);
	}

	private static void addRecoil(ItemStack stack, List<Component> tooltip) {
		double recoil = ModUtils.getConfigOrNbtDouble(stack, "RecoilDistance", () -> ThrowableConfig.THROWABLE_SPEAR_FRAGMENT_RECOIL.get().doubleValue());
		if (recoil > 0.0) {
			double rounded = ModUtils.roundToTwoDecimals(recoil);
			ModUtils.addStat(stack, tooltip, "recoil", rounded);
		}
	}

	private static void addWaterInertia(ItemStack stack, List<Component> tooltip) {
		double waterInertia = ModUtils.getConfigOrNbtDouble(stack, "WaterInertia", () -> ProjectileCommonConfig.SPEAR_FRAGMENT_PROJECTILE_WATER_INERTIA.get().doubleValue());
		double rounded = ModUtils.roundToTwoDecimals(waterInertia);
		ModUtils.addStat(stack, tooltip, "water_inertia", rounded);
	}

	private static void addThrowingModeIfEnabled(ItemStack stack, List<Component> tooltip, ModEnums.ThrowMode throwMode) {
		if (!TooltipsConfig.TOOLTIP_THROWING_MODE.get())
			return;
		ModUtils.addExtraInfo(stack, tooltip, "tooltip.jaams_archaeology.properties.throwing_mode", ChatFormatting.GOLD);
		String modeKey = switch (throwMode) {
			case INSTANT_ON_RIGHT_CLICK -> "tooltip.jaams_archaeology.properties.throw_mode.instant";
			case CHARGE_AND_RELEASE -> "tooltip.jaams_archaeology.properties.throw_mode.charge_release";
			case CHARGE_AND_FINISH_USING -> "tooltip.jaams_archaeology.properties.throw_mode.charge_finishing";
			case CHARGE_RELEASE_AND_FINISH -> "tooltip.jaams_archaeology.properties.throw_mode.charge_hybrid";
		};
		tooltip.add(Component.translatable(modeKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}
}
