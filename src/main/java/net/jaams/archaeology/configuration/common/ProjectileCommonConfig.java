package net.jaams.archaeology.configuration.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ProjectileCommonConfig {
	// Primitive Spear projectile
	public static ForgeConfigSpec.DoubleValue PRIMITIVE_SPEAR_PROJECTILE_BASE_DAMAGE;
	public static ForgeConfigSpec.DoubleValue PRIMITIVE_SPEAR_PROJECTILE_BASE_KNOCKBACK;
	public static ForgeConfigSpec.IntValue PRIMITIVE_SPEAR_PROJECTILE_PIERCING_LEVEL;
	public static ForgeConfigSpec.IntValue PRIMITIVE_SPEAR_PROJECTILE_IGNORE_TICKS;
	public static ForgeConfigSpec.BooleanValue PRIMITIVE_SPEAR_PROJECTILE_ALLOW_CRITICALS;
	public static ForgeConfigSpec.DoubleValue PRIMITIVE_SPEAR_PROJECTILE_WATER_INERTIA;
	public static ForgeConfigSpec.DoubleValue PRIMITIVE_SPEAR_PROJECTILE_HITBOX_WIDTH;
	public static ForgeConfigSpec.DoubleValue PRIMITIVE_SPEAR_PROJECTILE_HITBOX_HEIGHT;
	public static ForgeConfigSpec.BooleanValue PRIMITIVE_SPEAR_PROJECTILE_DESPAWN_AS_ITEM;
	public static ForgeConfigSpec.IntValue PRIMITIVE_SPEAR_PROJECTILE_DESPAWN_TIME;
	// Spear Fragment projectile
	public static ForgeConfigSpec.DoubleValue SPEAR_FRAGMENT_PROJECTILE_BASE_DAMAGE;
	public static ForgeConfigSpec.DoubleValue SPEAR_FRAGMENT_PROJECTILE_BASE_KNOCKBACK;
	public static ForgeConfigSpec.IntValue SPEAR_FRAGMENT_PROJECTILE_PIERCING_LEVEL;
	public static ForgeConfigSpec.IntValue SPEAR_FRAGMENT_PROJECTILE_IGNORE_TICKS;
	public static ForgeConfigSpec.BooleanValue SPEAR_FRAGMENT_PROJECTILE_ALLOW_CRITICALS;
	public static ForgeConfigSpec.DoubleValue SPEAR_FRAGMENT_PROJECTILE_WATER_INERTIA;
	public static ForgeConfigSpec.DoubleValue SPEAR_FRAGMENT_PROJECTILE_HITBOX_WIDTH;
	public static ForgeConfigSpec.DoubleValue SPEAR_FRAGMENT_PROJECTILE_HITBOX_HEIGHT;
	public static ForgeConfigSpec.BooleanValue SPEAR_FRAGMENT_PROJECTILE_DESPAWN_AS_ITEM;
	public static ForgeConfigSpec.IntValue SPEAR_FRAGMENT_PROJECTILE_DESPAWN_TIME;

	public static void register(ForgeConfigSpec.Builder builder) {
		builder.push("Projectile Common Handler");
		// Primitive Spear Projectile
		builder.push("Primitive Spear Projectile");
		PRIMITIVE_SPEAR_PROJECTILE_BASE_DAMAGE = builder.comment("Base damage dealt by the Primitive Spear projectile").defineInRange("Base Damage", 2.0, 0.5, 64.0);
		PRIMITIVE_SPEAR_PROJECTILE_BASE_KNOCKBACK = builder.comment("Base knockback strength applied by the Primitive Spear projectile to hit entities").defineInRange("Base Knockback", 0.4, 0.0, 4.0);
		PRIMITIVE_SPEAR_PROJECTILE_PIERCING_LEVEL = builder.comment("Number of additional entities the Primitive Spear projectile can pierce through").defineInRange("Piercing Level", 0, 0, 72000);
		PRIMITIVE_SPEAR_PROJECTILE_IGNORE_TICKS = builder.comment("Number of initial ticks during which the Primitive Spear projectile ignores collisions and damage").defineInRange("Ignore Ticks", 10, 0, 72000);
		PRIMITIVE_SPEAR_PROJECTILE_ALLOW_CRITICALS = builder.comment("Whether the Primitive Spear projectile can deal critical hits").define("Critical Hits", true);
		PRIMITIVE_SPEAR_PROJECTILE_WATER_INERTIA = builder.comment("Water inertia factor for the Primitive Spear projectile").defineInRange("Water Inertia", 0.6, 0.0, 1.0);
		PRIMITIVE_SPEAR_PROJECTILE_HITBOX_WIDTH = builder.comment("Hitbox width of the Primitive Spear projectile").defineInRange("Hitbox Width", 0.6, 0.1, 2.0);
		PRIMITIVE_SPEAR_PROJECTILE_HITBOX_HEIGHT = builder.comment("Hitbox height of the Primitive Spear projectile").defineInRange("Hitbox Height", 0.6, 0.1, 2.0);
		PRIMITIVE_SPEAR_PROJECTILE_DESPAWN_AS_ITEM = builder.comment("Whether the Primitive Spear projectile drops as an item when it despawns").define("Despawn As Item", false);
		PRIMITIVE_SPEAR_PROJECTILE_DESPAWN_TIME = builder.comment("Despawn time in ticks for the Primitive Spear projectile").defineInRange("Despawn Time", 1200, 100, 72000);
		builder.pop();
		// Spear Fragment Projectile
		builder.push("Spear Fragment Projectile");
		SPEAR_FRAGMENT_PROJECTILE_BASE_DAMAGE = builder.comment("Base damage dealt by the Spear Fragment projectile").defineInRange("Base Damage", 1.5, 0.5, 32.0);
		SPEAR_FRAGMENT_PROJECTILE_BASE_KNOCKBACK = builder.comment("Base knockback strength applied by the Spear Fragment projectile to hit entities").defineInRange("Base Knockback", 0.3, 0.0, 3.0);
		SPEAR_FRAGMENT_PROJECTILE_PIERCING_LEVEL = builder.comment("Number of additional entities the Spear Fragment projectile can pierce through").defineInRange("Piercing Level", 0, 0, 72000);
		SPEAR_FRAGMENT_PROJECTILE_IGNORE_TICKS = builder.comment("Number of initial ticks during which the Spear Fragment projectile ignores collisions and damage").defineInRange("Ignore Ticks", 10, 0, 72000);
		SPEAR_FRAGMENT_PROJECTILE_ALLOW_CRITICALS = builder.comment("Whether the Spear Fragment projectile can deal critical hits").define("Critical Hits", true);
		SPEAR_FRAGMENT_PROJECTILE_WATER_INERTIA = builder.comment("Water inertia factor for the Spear Fragment projectile").defineInRange("Water Inertia", 0.4, 0.0, 1.0);
		SPEAR_FRAGMENT_PROJECTILE_HITBOX_WIDTH = builder.comment("Hitbox width of the Spear Fragment projectile").defineInRange("Hitbox Width", 0.25, 0.1, 1.0);
		SPEAR_FRAGMENT_PROJECTILE_HITBOX_HEIGHT = builder.comment("Hitbox height of the Spear Fragment projectile").defineInRange("Hitbox Height", 0.25, 0.1, 1.0);
		SPEAR_FRAGMENT_PROJECTILE_DESPAWN_AS_ITEM = builder.comment("Whether the Spear Fragment projectile drops as an item when it despawns").define("Despawn As Item", false);
		SPEAR_FRAGMENT_PROJECTILE_DESPAWN_TIME = builder.comment("Despawn time in ticks for the Spear Fragment projectile").defineInRange("Despawn Time", 800, 100, 72000);
		builder.pop();
		builder.pop();
	}
}
