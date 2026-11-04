package net.jaams.archaeology.capability;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

import net.jaams.archaeology.capability.amount.AmountProvider;
import net.jaams.archaeology.capability.aberration.AberrationProvider;

@Mod.EventBusSubscriber(modid = "jaams_archaeology", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityAttacher {
	public static final ResourceLocation AMOUNT_CAP = new ResourceLocation("jaams_archaeology", "amount");
	public static final ResourceLocation ABERRATION_CAP = new ResourceLocation("jaams_archaeology", "aberration");

	@SubscribeEvent
	public static void onCapabilityAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			event.addCapability(ABERRATION_CAP, new AberrationProvider());
		}
		if (event.getObject() instanceof LivingEntity) {
			event.addCapability(AMOUNT_CAP, new AmountProvider());
		}
	}
}
