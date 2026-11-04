package net.jaams.archaeology.tooltip;

import org.checkerframework.checker.units.qual.min;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.util.ModTraits;
import net.jaams.archaeology.init.JaamsArchaeologyModEnchantments;
import net.jaams.archaeology.handler.traits.DeflectChargeHandler;
import net.jaams.archaeology.configuration.common.TraitsConfig;
import net.jaams.archaeology.configuration.client.TooltipsConfig;

import java.util.List;

public class DeflectChargeItemTooltip {
	public static void add(ItemStack stack, List<Component> tooltip) {
		if (!TraitsConfig.DEFLECT_CHARGE.get()) {
			return;
		}
		if (!ModTraits.isDeflectChargeItem(stack)) {
			return;
		}
		ModUtils.addTrait(stack, tooltip, "tooltip.jaams_archaeology.trait.deflect_charge", "tooltip.jaams_archaeology.trait.deflect_charge.desc");
		ModUtils.addExtraInfo(stack, tooltip, "tooltip.jaams_archaeology.properties.deflect_charge", ChatFormatting.GOLD);
		addCurrentChargeLine(stack, tooltip);
		addDamageSourceLine(stack, tooltip);
		addBaseDamageLine(stack, tooltip);
		addBaseKnockbackLine(stack, tooltip);
		addBaseRangeLineIfApplicable(stack, tooltip);
		addCooldownLine(stack, tooltip);
		addMaxChargeLine(stack, tooltip);
		addMinChargeLine(stack, tooltip);
	}

	private static void addCurrentChargeLine(ItemStack stack, List<Component> tooltip) {
		double current = DeflectChargeHandler.getDeflectAmount(stack);
		if (current <= 0) {
			return;
		}
		double rounded = ModUtils.roundToTwoDecimals(current);
		ModUtils.addStat(stack, tooltip, "current_charge", rounded);
	}

	private static void addDamageSourceLine(ItemStack stack, List<Component> tooltip) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("AttackerName")) {
			return;
		}
		if (!ModUtils.canAddTooltip(stack, true, false, TooltipsConfig.EXCLUDED_CONTROL_TOOLTIPS_ITEMS.get())) {
			return;
		}
		String source = tag.getString("AttackerName");
		tooltip.add(Component.translatable("tooltip.jaams_archaeology.properties.damage_source", source).withStyle(ChatFormatting.GRAY));
	}

	private static void addBaseDamageLine(ItemStack stack, List<Component> tooltip) {
		boolean hasStrikehold = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), stack) > 0;
		if (hasStrikehold) {
			return;
		}
		float baseDmg = DeflectChargeHandler.getBaseAreaDamage(stack);
		if (baseDmg <= 0.01f) {
			return;
		}
		double rounded = ModUtils.roundToTwoDecimals(baseDmg);
		ModUtils.addStat(stack, tooltip, "base_damage", rounded);
	}

	private static void addBaseKnockbackLine(ItemStack stack, List<Component> tooltip) {
		boolean hasStrikehold = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), stack) > 0;
		if (hasStrikehold) {
			return;
		}
		double knockback = DeflectChargeHandler.getKnockbackForce(stack);
		if (knockback <= 0.01) {
			return;
		}
		double rounded = ModUtils.roundToTwoDecimals(knockback);
		ModUtils.addStat(stack, tooltip, "base_knockback", rounded);
	}

	private static void addBaseRangeLineIfApplicable(ItemStack stack, List<Component> tooltip) {
		boolean hasStrikehold = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), stack) > 0;
		if (hasStrikehold) {
			return;
		}
		double range = ModUtils.getConfigOrNbtDouble(stack, "DeflectRange", TraitsConfig.DEFLECT_CHARGE_RANGE::get);
		double rounded = ModUtils.roundToTwoDecimals(range);
		ModUtils.addStat(stack, tooltip, "area_range", rounded);
	}

	private static void addCooldownLine(ItemStack stack, List<Component> tooltip) {
		boolean hasStrikehold = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), stack) > 0;
		int cooldownTicks;
		if (hasStrikehold) {
			cooldownTicks = 0;
		} else {
			cooldownTicks = DeflectChargeHandler.getReleaseCooldownTicks(stack);
			if (cooldownTicks < 0) {
				cooldownTicks = 0;
			}
		}
		double seconds = cooldownTicks / 20.0;
		double rounded = ModUtils.roundToTwoDecimals(seconds);
		ModUtils.addStat(stack, tooltip, "cooldown", rounded);
	}

	private static void addMaxChargeLine(ItemStack stack, List<Component> tooltip) {
		double max = ModUtils.getConfigOrNbtDouble(stack, "DeflectMaxAmount", TraitsConfig.DEFLECT_CHARGE_MAX_AMOUNT::get);
		double rounded = ModUtils.roundToTwoDecimals(max);
		ModUtils.addStat(stack, tooltip, "max_charge", rounded);
	}

	private static void addMinChargeLine(ItemStack stack, List<Component> tooltip) {
		double min = ModUtils.getConfigOrNbtDouble(stack, "DeflectMinAmount", TraitsConfig.DEFLECT_CHARGE_MIN_AMOUNT::get);
		double rounded = ModUtils.roundToTwoDecimals(min);
		ModUtils.addStat(stack, tooltip, "min_charge", rounded);
	}
}
