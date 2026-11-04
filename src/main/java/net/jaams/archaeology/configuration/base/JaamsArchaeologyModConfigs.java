package net.jaams.archaeology.configuration.base;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.jaams.archaeology.JaamsArchaeologyMod;

@Mod.EventBusSubscriber(modid = JaamsArchaeologyMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JaamsArchaeologyModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, JaamsArchaeologyClientConfiguration.SPEC, "jaams/archaeology/jaams_archaeology_client.toml");
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, JaamsArchaeologyCommonConfiguration.SPEC, "jaams/archaeology/jaams_archaeology_common.toml");
		});
	}
}
