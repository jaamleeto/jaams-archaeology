package net.jaams.archaeology.configuration.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class TraitsConfig {
	// Global toggles
	public static ForgeConfigSpec.BooleanValue THROWABLE;
	public static ForgeConfigSpec.BooleanValue DEFLECT_CHARGE;
	// Deflect Charge settings
	public static ForgeConfigSpec.DoubleValue DEFLECT_CHARGE_MAX_AMOUNT;
	public static ForgeConfigSpec.DoubleValue DEFLECT_CHARGE_MIN_AMOUNT;
	public static ForgeConfigSpec.DoubleValue DEFLECT_CHARGE_BASE_DAMAGE;
	public static ForgeConfigSpec.DoubleValue DEFLECT_CHARGE_RANGE;
	public static ForgeConfigSpec.DoubleValue DEFLECT_CHARGE_KNOCKBACK;
	public static ForgeConfigSpec.IntValue DEFLECT_CHARGE_COOLDOWN_TICKS;
	public static ForgeConfigSpec.BooleanValue DEFLECT_CHARGE_PASSIVE;
	public static ForgeConfigSpec.DoubleValue DEFLECT_CHARGE_PASSIVE_FACTOR;
	public static ForgeConfigSpec.BooleanValue DEFLECT_CHARGE_MOBS_PASSIVE;
	public static ForgeConfigSpec.BooleanValue DEFLECT_CHARGE_COLOR;
	public static ForgeConfigSpec.BooleanValue DEFLECT_CHARGE_RELEASE_ON_DEATH;
	public static ForgeConfigSpec.BooleanValue DEFLECT_CHARGE_RELEASE_ON_BREAK;

	public static void register(ForgeConfigSpec.Builder builder) {
		builder.push("Weapon Traits Handler");
		builder.push("Traits Global Toggles");
		THROWABLE = builder.comment("Master switch for the mod's Throwable trait").define("Throwable", true);
		DEFLECT_CHARGE = builder.comment("Master switch for the mod's Deflect Charge trait").define("Deflect Charge", true);
		builder.pop();
		builder.push("Item Traits Settings");
		builder.push("Throwable");
		ThrowableConfig.register(builder);
		builder.pop();
		builder.push("Deflect Charge");
		DEFLECT_CHARGE_MAX_AMOUNT = builder.comment("Maximum charge that can be accumulated").defineInRange("Max Charge Amount", 50.0, 1.0, 500.0);
		DEFLECT_CHARGE_MIN_AMOUNT = builder.comment("Minimum incoming damage required to add charge").defineInRange("Min Charge Amount", 0.5, 0.0, 5.0);
		DEFLECT_CHARGE_BASE_DAMAGE = builder.comment("Base area damage when releasing").defineInRange("Base Damage", 1.0, 0.0, 20.0);
		DEFLECT_CHARGE_RANGE = builder.comment("Radius of the area damage and knockback effect when releasing").defineInRange("Base Range", 1.8, 0.5, 8.0);
		DEFLECT_CHARGE_KNOCKBACK = builder.comment("Knockback force applied to targets when releasing").defineInRange("Base Knockback", 0.4, 0.0, 3.0);
		DEFLECT_CHARGE_COOLDOWN_TICKS = builder.comment("Cooldown in ticks after releasing the charge").defineInRange("Cooldown Ticks", 20, 0, 200);
		DEFLECT_CHARGE_PASSIVE = builder.comment("Whether entities can accumulate deflect charge passively when taking damage").define("Passive Charge", true);
		DEFLECT_CHARGE_PASSIVE_FACTOR = builder.comment("Multiplier for passive charge gain from incoming damage").defineInRange("Passive Charge Factor", 0.3, 0.0, 1.0);
		DEFLECT_CHARGE_MOBS_PASSIVE = builder.comment("Whether mobs can accumulate charge passively when taking damage").define("Mobs Passive Charge", true);
		DEFLECT_CHARGE_COLOR = builder.comment("Whether the item color changes based on accumulated deflect charge").define("Color Change", true);
		DEFLECT_CHARGE_RELEASE_ON_DEATH = builder.comment("Whether an entity releases area damage on death if holding a charged deflect item").define("Release Charge On Death", false);
		DEFLECT_CHARGE_RELEASE_ON_BREAK = builder.comment("Whether a deflect item releases area damage when it breaks from durability loss").define("Release Charge On Break", true);
		builder.pop();
		builder.pop();
		builder.pop();
	}
}
