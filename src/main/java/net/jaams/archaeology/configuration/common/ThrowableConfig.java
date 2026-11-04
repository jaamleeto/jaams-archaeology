package net.jaams.archaeology.configuration.common;

import net.minecraftforge.common.ForgeConfigSpec;

import net.minecraft.world.item.UseAnim;

import net.jaams.archaeology.util.ModEnums;

public class ThrowableConfig {
	// Primitive Spear
	public static ForgeConfigSpec.BooleanValue THROWABLE_PRIMITIVE_SPEAR;
	public static ForgeConfigSpec.IntValue THROWABLE_PRIMITIVE_SPEAR_MIN_CHARGE;
	public static ForgeConfigSpec.IntValue THROWABLE_PRIMITIVE_SPEAR_MAX_CHARGE;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_MIN_SPEED;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_MAX_SPEED;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_INACCURACY;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_CRITICAL_POWER_THRESHOLD;
	public static ForgeConfigSpec.BooleanValue THROWABLE_PRIMITIVE_SPEAR_REMEMBER_SLOT;
	public static ForgeConfigSpec.IntValue THROWABLE_PRIMITIVE_SPEAR_DAMAGE_AMOUNT_ON_THROW;
	public static ForgeConfigSpec.BooleanValue THROWABLE_PRIMITIVE_SPEAR_ALLOW_MULTISHOT;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_MULTISHOT_SPREAD_ANGLE;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_RECOIL;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_RECOIL_VERTICAL_MULTIPLIER;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_RECOIL_CROUCH_REDUCTION;
	public static ForgeConfigSpec.DoubleValue THROWABLE_PRIMITIVE_SPEAR_RECOIL_PITCH_KICK;
	public static ForgeConfigSpec.BooleanValue THROWABLE_PRIMITIVE_SPEAR_RECOIL_ONLY_FULLY_CHARGED;
	public static ForgeConfigSpec.EnumValue<ModEnums.ThrowMode> THROWABLE_PRIMITIVE_SPEAR_THROW_MODE;
	public static ForgeConfigSpec.IntValue THROWABLE_PRIMITIVE_SPEAR_COOLDOWN_TICKS_INSTANT;
	public static ForgeConfigSpec.IntValue THROWABLE_PRIMITIVE_SPEAR_USE_DURATION_TICKS;
	public static ForgeConfigSpec.IntValue THROWABLE_PRIMITIVE_SPEAR_MIN_REMAINING_DURABILITY;
	public static ForgeConfigSpec.EnumValue<UseAnim> THROWABLE_PRIMITIVE_SPEAR_USE_ANIMATION;
	// Spear Fragment
	public static ForgeConfigSpec.BooleanValue THROWABLE_SPEAR_FRAGMENT;
	public static ForgeConfigSpec.IntValue THROWABLE_SPEAR_FRAGMENT_MIN_CHARGE;
	public static ForgeConfigSpec.IntValue THROWABLE_SPEAR_FRAGMENT_MAX_CHARGE;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_MIN_SPEED;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_MAX_SPEED;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_INACCURACY;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_CRITICAL_POWER_THRESHOLD;
	public static ForgeConfigSpec.BooleanValue THROWABLE_SPEAR_FRAGMENT_REMEMBER_SLOT;
	public static ForgeConfigSpec.IntValue THROWABLE_SPEAR_FRAGMENT_DAMAGE_AMOUNT_ON_THROW;
	public static ForgeConfigSpec.BooleanValue THROWABLE_SPEAR_FRAGMENT_ALLOW_MULTISHOT;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_MULTISHOT_SPREAD_ANGLE;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_RECOIL;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_RECOIL_VERTICAL_MULTIPLIER;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_RECOIL_CROUCH_REDUCTION;
	public static ForgeConfigSpec.DoubleValue THROWABLE_SPEAR_FRAGMENT_RECOIL_PITCH_KICK;
	public static ForgeConfigSpec.BooleanValue THROWABLE_SPEAR_FRAGMENT_RECOIL_ONLY_FULLY_CHARGED;
	public static ForgeConfigSpec.EnumValue<ModEnums.ThrowMode> THROWABLE_SPEAR_FRAGMENT_THROW_MODE;
	public static ForgeConfigSpec.IntValue THROWABLE_SPEAR_FRAGMENT_COOLDOWN_TICKS_INSTANT;
	public static ForgeConfigSpec.IntValue THROWABLE_SPEAR_FRAGMENT_USE_DURATION_TICKS;
	public static ForgeConfigSpec.IntValue THROWABLE_SPEAR_FRAGMENT_MIN_REMAINING_DURABILITY;
	public static ForgeConfigSpec.EnumValue<UseAnim> THROWABLE_SPEAR_FRAGMENT_USE_ANIMATION;

	public static void register(ForgeConfigSpec.Builder builder) {
		builder.push("Primitive Spear");
		THROWABLE_PRIMITIVE_SPEAR = builder.comment("Whether the Primitive Spear can be thrown").define("Allow Throw", true);
		THROWABLE_PRIMITIVE_SPEAR_MIN_CHARGE = builder.comment("Minimum ticks required to start charging the Primitive Spear throw").defineInRange("Min Charge Time", 5, 1, 40);
		THROWABLE_PRIMITIVE_SPEAR_MAX_CHARGE = builder.comment("Maximum charge time for full power throw of the Primitive Spear").defineInRange("Max Charge Time", 20, 5, 60);
		THROWABLE_PRIMITIVE_SPEAR_MIN_SPEED = builder.comment("Projectile speed of the Primitive Spear at minimum charge").defineInRange("Min Speed", 0.5, 0.1, 2.0);
		THROWABLE_PRIMITIVE_SPEAR_MAX_SPEED = builder.comment("Projectile speed of the Primitive Spear at full charge").defineInRange("Max Speed", 1.8, 0.5, 4.0);
		THROWABLE_PRIMITIVE_SPEAR_INACCURACY = builder.comment("Inaccuracy/spread in degrees of the Primitive Spear").defineInRange("Inaccuracy", 0.0, 0.0, 50.0);
		THROWABLE_PRIMITIVE_SPEAR_CRITICAL_POWER_THRESHOLD = builder.comment("Charge percentage required for critical effects on the Primitive Spear throw").defineInRange("Critical Power Threshold", 0.85, 0.0, 1.0);
		THROWABLE_PRIMITIVE_SPEAR_REMEMBER_SLOT = builder.comment("Whether the Primitive Spear returns to the original hotbar slot after being thrown").define("Remember Original Slot", true);
		THROWABLE_PRIMITIVE_SPEAR_DAMAGE_AMOUNT_ON_THROW = builder.comment("Durability damage the Primitive Spear takes each time it is thrown").defineInRange("Item Damage Amount", 1, 0, 10);
		THROWABLE_PRIMITIVE_SPEAR_ALLOW_MULTISHOT = builder.comment("Whether the Multishot enchantment can be applied to the Primitive Spear").define("Multishot Enchantment", true);
		THROWABLE_PRIMITIVE_SPEAR_MULTISHOT_SPREAD_ANGLE = builder.comment("Spread angle in degrees for each extra projectile when using Multishot on the Primitive Spear").defineInRange("Multishot Spread Angle", 10.0, 0.0, 45.0);
		THROWABLE_PRIMITIVE_SPEAR_RECOIL = builder.comment("Horizontal recoil strength when throwing the Primitive Spear").defineInRange("Recoil", 0.0, 0.0, 10.0);
		THROWABLE_PRIMITIVE_SPEAR_RECOIL_VERTICAL_MULTIPLIER = builder.comment("Vertical multiplier applied to the recoil when throwing the Primitive Spear").defineInRange("Recoil Vertical Multiplier", 0.0, 0.0, 10.0);
		THROWABLE_PRIMITIVE_SPEAR_RECOIL_CROUCH_REDUCTION = builder.comment("Recoil reduction multiplier when crouching while throwing the Primitive Spear").defineInRange("Recoil Crouch Reduction", 0.0, 0.0, 1.0);
		THROWABLE_PRIMITIVE_SPEAR_RECOIL_PITCH_KICK = builder.comment("Additional upward pitch kick applied from recoil when throwing the Primitive Spear").defineInRange("Recoil Pitch Kick", 0.0, 0.0, 10.0);
		THROWABLE_PRIMITIVE_SPEAR_RECOIL_ONLY_FULLY_CHARGED = builder.comment("Whether recoil is only applied on fully charged throws of the Primitive Spear").define("Recoil Fully Charged", false);
		THROWABLE_PRIMITIVE_SPEAR_THROW_MODE = builder.comment("Throw mechanic style used for the Primitive Spear").defineEnum("Throw Mode", ModEnums.ThrowMode.CHARGE_AND_RELEASE);
		THROWABLE_PRIMITIVE_SPEAR_COOLDOWN_TICKS_INSTANT = builder.comment("Cooldown in ticks after an instant throw of the Primitive Spear").defineInRange("Instant Throw Cooldown Ticks", 20, 0, 200);
		THROWABLE_PRIMITIVE_SPEAR_USE_DURATION_TICKS = builder.comment("Maximum use duration in ticks for charging the Primitive Spear throw").defineInRange("Use Duration Ticks", 72000, 20, 72000);
		THROWABLE_PRIMITIVE_SPEAR_MIN_REMAINING_DURABILITY = builder.comment("Minimum remaining durability required to throw the Primitive Spear").defineInRange("Min Durability Throw", -1, -1, 100);
		THROWABLE_PRIMITIVE_SPEAR_USE_ANIMATION = builder.comment("Item use animation played when charging or throwing the Primitive Spear").defineEnum("Use Animation", UseAnim.SPEAR);
		builder.pop();
		builder.push("Spear Fragment");
		THROWABLE_SPEAR_FRAGMENT = builder.comment("Whether the Spear Fragment can be thrown").define("Allow Throw", true);
		THROWABLE_SPEAR_FRAGMENT_MIN_CHARGE = builder.comment("Minimum ticks required to start charging the Spear Fragment throw").defineInRange("Min Charge Time", 5, 1, 40);
		THROWABLE_SPEAR_FRAGMENT_MAX_CHARGE = builder.comment("Maximum charge time for full power throw of the Spear Fragment").defineInRange("Max Charge Time", 20, 5, 60);
		THROWABLE_SPEAR_FRAGMENT_MIN_SPEED = builder.comment("Projectile speed of the Spear Fragment at minimum charge").defineInRange("Min Speed", 0.5, 0.1, 2.0);
		THROWABLE_SPEAR_FRAGMENT_MAX_SPEED = builder.comment("Projectile speed of the Spear Fragment at full charge").defineInRange("Max Speed", 1.8, 0.5, 4.0);
		THROWABLE_SPEAR_FRAGMENT_INACCURACY = builder.comment("Inaccuracy/spread in degrees of the Spear Fragment").defineInRange("Inaccuracy", 0.0, 0.0, 50.0);
		THROWABLE_SPEAR_FRAGMENT_CRITICAL_POWER_THRESHOLD = builder.comment("Charge percentage required for critical effects on the Spear Fragment throw").defineInRange("Critical Power Threshold", 0.85, 0.0, 1.0);
		THROWABLE_SPEAR_FRAGMENT_REMEMBER_SLOT = builder.comment("Whether the Spear Fragment returns to the original hotbar slot after being thrown").define("Remember Original Slot", true);
		THROWABLE_SPEAR_FRAGMENT_DAMAGE_AMOUNT_ON_THROW = builder.comment("Durability damage the Spear Fragment takes each time it is thrown").defineInRange("Item Damage Amount", 1, 0, 10);
		THROWABLE_SPEAR_FRAGMENT_ALLOW_MULTISHOT = builder.comment("Whether the Multishot enchantment can be applied to the Spear Fragment").define("Multishot Enchantment", true);
		THROWABLE_SPEAR_FRAGMENT_MULTISHOT_SPREAD_ANGLE = builder.comment("Spread angle in degrees for each extra projectile when using Multishot on the Spear Fragment").defineInRange("Multishot Spread Angle", 10.0, 0.0, 45.0);
		THROWABLE_SPEAR_FRAGMENT_RECOIL = builder.comment("Horizontal recoil strength when throwing the Spear Fragment").defineInRange("Recoil", 0.0, 0.0, 10.0);
		THROWABLE_SPEAR_FRAGMENT_RECOIL_VERTICAL_MULTIPLIER = builder.comment("Vertical multiplier applied to the recoil when throwing the Spear Fragment").defineInRange("Recoil Vertical Multiplier", 0.0, 0.0, 10.0);
		THROWABLE_SPEAR_FRAGMENT_RECOIL_CROUCH_REDUCTION = builder.comment("Recoil reduction multiplier when crouching while throwing the Spear Fragment").defineInRange("Recoil Crouch Reduction", 0.0, 0.0, 1.0);
		THROWABLE_SPEAR_FRAGMENT_RECOIL_PITCH_KICK = builder.comment("Additional upward pitch kick applied from recoil when throwing the Spear Fragment").defineInRange("Recoil Pitch Kick", 0.0, 0.0, 10.0);
		THROWABLE_SPEAR_FRAGMENT_RECOIL_ONLY_FULLY_CHARGED = builder.comment("Whether recoil is only applied on fully charged throws of the Spear Fragment").define("Recoil Fully Charged", false);
		THROWABLE_SPEAR_FRAGMENT_THROW_MODE = builder.comment("Throw mechanic style used for the Spear Fragment").defineEnum("Throw Mode", ModEnums.ThrowMode.CHARGE_AND_RELEASE);
		THROWABLE_SPEAR_FRAGMENT_COOLDOWN_TICKS_INSTANT = builder.comment("Cooldown in ticks after an instant throw of the Spear Fragment").defineInRange("Instant Throw Cooldown Ticks", 20, 0, 200);
		THROWABLE_SPEAR_FRAGMENT_USE_DURATION_TICKS = builder.comment("Maximum use duration in ticks for charging the Spear Fragment throw").defineInRange("Use Duration Ticks", 72000, 20, 72000);
		THROWABLE_SPEAR_FRAGMENT_MIN_REMAINING_DURABILITY = builder.comment("Minimum remaining durability required to throw the Spear Fragment").defineInRange("Min Durability Throw", -1, -1, 100);
		THROWABLE_SPEAR_FRAGMENT_USE_ANIMATION = builder.comment("Item use animation played when charging or throwing the Spear Fragment").defineEnum("Use Animation", UseAnim.SPEAR);
		builder.pop();
	}
}
