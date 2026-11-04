package net.jaams.archaeology.packet;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;

import net.jaams.archaeology.util.ModEnums;
import net.jaams.archaeology.capability.amount.AmountProvider;
import net.jaams.archaeology.capability.aberration.AberrationProvider;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
	public static void handleAmountPacket(int entityId, float damage) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level != null) {
			Entity entity = minecraft.level.getEntity(entityId);
			if (entity instanceof LivingEntity livingEntity) {
				livingEntity.getCapability(AmountProvider.AMOUNT).ifPresent(amount -> {
					amount.setDamage(damage);
				});
			}
		}
	}

	public static void handleAberrationPacket(int playerId, ModEnums.AberrationType effectType, double intensity, int duration) {
		Level level = Minecraft.getInstance().level;
		if (level != null) {
			Entity entity = level.getEntity(playerId);
			if (entity instanceof Player player) {
				player.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
					aberration.setEffectType(effectType);
					aberration.setIntensity(intensity);
					aberration.setDuration(duration);
				});
			}
		}
	}
}
