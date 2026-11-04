package net.jaams.archaeology.configuration.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ProjectileClientConfig {
	// Primitive Spear
	public static ForgeConfigSpec.DoubleValue PRIMITIVE_SPEAR_PROJECTILE_SCALE;
	// Spear Fragment
	public static ForgeConfigSpec.DoubleValue SPEAR_FRAGMENT_PROJECTILE_SCALE;
	// Ammonite
	public static ForgeConfigSpec.DoubleValue AMMONITE_PROJECTILE_SCALE;

	public static void register(ForgeConfigSpec.Builder builder) {
		builder.push("Projectile Client Handler");
		builder.push("Primitive Spear Projectile");
		PRIMITIVE_SPEAR_PROJECTILE_SCALE = builder.comment("Visual size scale of the Primitive Spear projectile").defineInRange("Projectile Visual Size", 1.0, 0.1, 10.0);
		builder.pop();
		builder.push("Spear Fragment Projectile");
		SPEAR_FRAGMENT_PROJECTILE_SCALE = builder.comment("Visual size scale of the Spear Fragment projectile").defineInRange("Projectile Visual Size", 1.5, 0.1, 10.0);
		builder.pop();
		builder.push("Ammonite Projectile");
		AMMONITE_PROJECTILE_SCALE = builder.comment("Visual size scale of the Ammonite projectile").defineInRange("Projectile Visual Size", 1.2, 0.1, 10.0);
		builder.pop();
		builder.pop();
	}
}
