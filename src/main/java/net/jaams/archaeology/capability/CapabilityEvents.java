package net.jaams.archaeology.capability;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

import net.jaams.archaeology.util.ModEnums;
import net.jaams.archaeology.packet.AmountPacket;
import net.jaams.archaeology.packet.AberrationPacket;
import net.jaams.archaeology.capability.amount.AmountProvider;
import net.jaams.archaeology.capability.aberration.AberrationProvider;
import net.jaams.archaeology.JaamsArchaeologyMod;

@Mod.EventBusSubscriber(modid = "jaams_archaeology", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEvents {
	@SubscribeEvent
	public static void onCapabilityPlayerTick(LivingEvent.LivingTickEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
			player.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
				int duration = aberration.getDuration();
				if (duration > 0) {
					aberration.setDuration(duration - 1);
					if (duration - 1 <= 0) {
						aberration.setEffectType(ModEnums.AberrationType.NONE);
						aberration.setIntensity(0.0);
					}
					JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AberrationPacket(player.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration()));
				}
			});
		}
	}

	@SubscribeEvent
	public static void onCapabilityEntityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
			player.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AberrationPacket(player.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration()));
			});
			player.getCapability(AmountProvider.AMOUNT).ifPresent(amount -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AmountPacket(player.getId(), amount.getDamage()));
			});
		}
	}

	@SubscribeEvent
	public static void onCapabilityStartTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if (!target.level().isClientSide && target instanceof Player && event.getEntity() instanceof ServerPlayer player) {
			target.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AberrationPacket(target.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration()));
			});
			target.getCapability(AmountProvider.AMOUNT).ifPresent(amount -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AmountPacket(target.getId(), amount.getDamage()));
			});
		}
	}

	@SubscribeEvent
	public static void onCapabilityPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
			player.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AberrationPacket(player.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration()));
			});
			player.getCapability(AmountProvider.AMOUNT).ifPresent(amount -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AmountPacket(player.getId(), amount.getDamage()));
			});
		}
	}

	@SubscribeEvent
	public static void onCapabilityPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
			player.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AberrationPacket(player.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration()));
			});
			player.getCapability(AmountProvider.AMOUNT).ifPresent(amount -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AmountPacket(player.getId(), amount.getDamage()));
			});
		}
	}

	@SubscribeEvent
	public static void onCapabilityPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
			player.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AberrationPacket(player.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration()));
			});
			player.getCapability(AmountProvider.AMOUNT).ifPresent(amount -> {
				JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AmountPacket(player.getId(), amount.getDamage()));
			});
		}
	}

	@SubscribeEvent
	public static void onCapabilityPlayerClone(PlayerEvent.Clone event) {
		if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer newPlayer && event.getOriginal() instanceof ServerPlayer original) {
			original.getCapability(AberrationProvider.ABERRATION).ifPresent(originalAberration -> {
				newPlayer.getCapability(AberrationProvider.ABERRATION).ifPresent(newAberration -> {
					newAberration.deserializeNBT(originalAberration.serializeNBT());
					JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> newPlayer), new AberrationPacket(newPlayer.getId(), newAberration.getEffectType(), newAberration.getIntensity(), newAberration.getDuration()));
				});
			});
			original.getCapability(AmountProvider.AMOUNT).ifPresent(originalAmount -> {
				newPlayer.getCapability(AmountProvider.AMOUNT).ifPresent(newAmount -> {
					newAmount.deserializeNBT(originalAmount.serializeNBT());
					JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> newPlayer), new AmountPacket(newPlayer.getId(), newAmount.getDamage()));
				});
			});
		}
	}
}
