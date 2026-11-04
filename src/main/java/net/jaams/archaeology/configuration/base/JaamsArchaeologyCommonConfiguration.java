package net.jaams.archaeology.configuration.base;

import net.minecraftforge.common.ForgeConfigSpec;

import net.jaams.archaeology.configuration.common.TraitsConfig;
import net.jaams.archaeology.configuration.common.ProjectileCommonConfig;
import net.jaams.archaeology.configuration.common.LootConfig;
import net.jaams.archaeology.configuration.common.InteractionsConfig;
import net.jaams.archaeology.configuration.common.AssortedCommonConfig;

public class JaamsArchaeologyCommonConfiguration {
	public static final ForgeConfigSpec SPEC;
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		AssortedCommonConfig.register(builder);
		InteractionsConfig.register(builder);
		LootConfig.register(builder);
		ProjectileCommonConfig.register(builder);
		TraitsConfig.register(builder);
		SPEC = builder.build();
	}
}
