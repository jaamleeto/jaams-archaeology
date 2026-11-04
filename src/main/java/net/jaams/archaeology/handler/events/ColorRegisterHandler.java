package net.jaams.archaeology.handler.events;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.jaams.archaeology.init.JaamsArchaeologyModItems;
import net.jaams.archaeology.dyeable.IDyeableItem;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ColorRegisterHandler {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onItemColorRegister(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((IDyeableItem) stack.getItem()).getColor(stack), JaamsArchaeologyModItems.BROKEN_SWORD_FRAGMENT.get());
	}
}
