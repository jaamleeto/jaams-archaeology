package net.jaams.archaeology.capability;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

import net.jaams.archaeology.capability.amount.IAmount;
import net.jaams.archaeology.capability.aberration.IAberration;

@Mod.EventBusSubscriber(modid = "jaams_archaeology", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
	@SubscribeEvent
	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.register(IAmount.class);
		event.register(IAberration.class);
	}
}
