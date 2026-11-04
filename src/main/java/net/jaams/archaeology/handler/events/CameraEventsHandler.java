
package net.jaams.archaeology.handler.events;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Minecraft;

import net.jaams.archaeology.util.ModEnums;
import net.jaams.archaeology.item.SpearFragmentItem;
import net.jaams.archaeology.item.PrimitiveSpearItem;
import net.jaams.archaeology.configuration.client.CameraEffectsConfig;
import net.jaams.archaeology.capability.aberration.AberrationProvider;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CameraEventsHandler {
	@SubscribeEvent
	public static void onCameraEffect(ViewportEvent.ComputeCameraAngles event) {
		LocalPlayer player = Minecraft.getInstance().player;
		Minecraft minecraft = Minecraft.getInstance();
		if (player != null && CameraEffectsConfig.CAMERA_SHAKE.get() && !minecraft.isPaused() && minecraft.screen == null) {
			player.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
				ModEnums.AberrationType effectType = aberration.getEffectType();
				double intensity = aberration.getIntensity();
				int duration = aberration.getDuration();
				if (intensity > 0.0 && duration > 0 && effectType != ModEnums.AberrationType.NONE) {
					switch (effectType) {
						case SHAKE :
							double randomShake = (-1.0 + 2.0 * Math.random()) * intensity;
							double randomUpward = Math.random() * (intensity * 0.75);
							event.setPitch((float) (event.getPitch() + randomShake));
							event.setRoll((float) (event.getRoll() + randomShake));
							event.setYaw((float) (event.getYaw() + randomShake));
							break;
						case BLUR :
							float fovModifier = (float) (intensity * 0.1);
							break;
						case DISTORT :
							double distortShake = (-0.5 + Math.random()) * intensity * 0.5;
							event.setYaw((float) (event.getYaw() + distortShake));
							event.setPitch((float) (event.getPitch() + distortShake * 0.5));
							break;
						default :
							break;
					}
				}
			});
		}
	}

	@SubscribeEvent
	public static void onFOVModifier(ComputeFovModifierEvent event) {
		Player player = event.getPlayer();
		if (player != null && player.isUsingItem()) {
			if (CameraEffectsConfig.FOV_MODIFIER.get()) {
				ItemStack useStack = player.getUseItem();
				Item useItem = useStack.getItem();
				if (useItem instanceof PrimitiveSpearItem) {
					float f1 = player.getTicksUsingItem() / 20.0f;
					f1 = f1 > 1.0F ? 1.0F : (float) Math.pow(f1, 1.6D);
					event.setNewFovModifier(event.getFovModifier() * (1.0F - f1 * 0.2F));
				}
				if (useItem instanceof SpearFragmentItem) {
					float f1 = player.getTicksUsingItem() / 20.0f;
					f1 = f1 > 1.0F ? 1.0F : (float) Math.pow(f1, 1.6D);
					event.setNewFovModifier(event.getFovModifier() * (1.0F - f1 * 0.2F));
				}
			}
		}
	}
}
