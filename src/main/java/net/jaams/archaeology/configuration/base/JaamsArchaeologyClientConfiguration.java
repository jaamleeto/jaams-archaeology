package net.jaams.archaeology.configuration.base;

import net.minecraftforge.common.ForgeConfigSpec;

import net.jaams.archaeology.configuration.client.TooltipsConfig;
import net.jaams.archaeology.configuration.client.ProjectileClientConfig;
import net.jaams.archaeology.configuration.client.CreativeTabConfig;
import net.jaams.archaeology.configuration.client.CameraEffectsConfig;

public class JaamsArchaeologyClientConfiguration {
	public static final ForgeConfigSpec SPEC;
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		CameraEffectsConfig.register(builder);
		CreativeTabConfig.register(builder);
		ProjectileClientConfig.register(builder);
		TooltipsConfig.register(builder);
		SPEC = builder.build();
	}
}
