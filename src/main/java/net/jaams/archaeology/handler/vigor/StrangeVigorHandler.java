package net.jaams.archaeology.handler.vigor;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

import java.util.Random;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class StrangeVigorHandler {
	private static final double BASE_AURA_RADIUS = 4.0;
	private static final double AURA_RADIUS_PER_LEVEL = 1.0;
	private static final double PARTICLE_AURA_RADIUS = 0.5;
	private static final int PARTICLE_COUNT = 8;
	private static final float BASE_SWAP_CHANCE = 0.5F;
	private static final float SWAP_CHANCE_PER_LEVEL = 0.05F;
	private static final float CRITICAL_SWAP_BONUS = 0.2F;
	private static final double SWAP_RANGE = 8.0;
	private static final int MAX_TELEPORT_ATTEMPTS = 10;

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event) {
		LivingEntity defender = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		Level level = defender.level();
		if (level.isClientSide() || !(attacker instanceof LivingEntity livingAttacker)) {
			return;
		}
		if (livingAttacker.hasEffect(JaamsArchaeologyModMobEffects.STRANGE_VIGOR.get())) {
			trySwapPositionsAttack(livingAttacker, defender, event.getSource(), level);
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		LivingEntity deadEntity = event.getEntity();
		Level level = deadEntity.level();
		if (level.isClientSide() || !deadEntity.hasEffect(JaamsArchaeologyModMobEffects.STRANGE_VIGOR.get())) {
			return;
		}
		if (level instanceof ServerLevel serverLevel) {
			swapNearbyEntitiesDeath(deadEntity, serverLevel);
		}
	}

	private static boolean trySwapPositionsAttack(LivingEntity attacker, LivingEntity defender, DamageSource source, Level level) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return false;
		}
		MobEffectInstance effect = attacker.getEffect(JaamsArchaeologyModMobEffects.STRANGE_VIGOR.get());
		if (effect == null) {
			return false;
		}
		float swapChance = calculateSwapChance(attacker, defender, effect);
		if (attacker.getRandom().nextFloat() >= swapChance) {
			return false;
		}
		return performPositionSwapAttack(attacker, defender, serverLevel);
	}

	private static float calculateSwapChance(LivingEntity attacker, LivingEntity defender, MobEffectInstance effect) {
		int effectLevel = effect.getAmplifier() + 1;
		float swapChance = BASE_SWAP_CHANCE + (effectLevel * SWAP_CHANCE_PER_LEVEL);
		if (attacker instanceof Player player) {
			boolean isCritical = ModUtils.isCritical(player, defender, player.getAttackStrengthScale(0.5F));
			if (isCritical) {
				swapChance += CRITICAL_SWAP_BONUS;
			}
		}
		return Math.min(swapChance, 1.0F);
	}

	private static boolean performPositionSwapAttack(LivingEntity attacker, LivingEntity defender, ServerLevel serverLevel) {
		Vec3 attackerPos = attacker.position();
		Vec3 defenderPos = defender.position();
		attacker.teleportTo(defenderPos.x(), defenderPos.y(), defenderPos.z());
		defender.teleportTo(attackerPos.x(), attackerPos.y(), attackerPos.z());
		spawnAuraParticles(attacker, serverLevel);
		spawnAuraParticles(defender, serverLevel);
		serverLevel.playSound(null, attackerPos.x(), attackerPos.y(), attackerPos.z(), SoundEvents.ENDERMAN_TELEPORT, attacker.getSoundSource(), 1.0F, 0.8F + serverLevel.random.nextFloat() * 0.4F);
		serverLevel.playSound(null, defenderPos.x(), defenderPos.y(), defenderPos.z(), SoundEvents.ENDERMAN_TELEPORT, defender.getSoundSource(), 1.0F, 0.8F + serverLevel.random.nextFloat() * 0.4F);
		return true;
	}

	private static void swapNearbyEntitiesDeath(LivingEntity deadEntity, ServerLevel serverLevel) {
		MobEffectInstance effect = deadEntity.getEffect(JaamsArchaeologyModMobEffects.STRANGE_VIGOR.get());
		if (effect == null) {
			return;
		}
		int effectLevel = effect.getAmplifier() + 1;
		double auraRadius = BASE_AURA_RADIUS + (effectLevel * AURA_RADIUS_PER_LEVEL);
		AABB area = new AABB(deadEntity.getX() - auraRadius, deadEntity.getY() - auraRadius, deadEntity.getZ() - auraRadius, deadEntity.getX() + auraRadius, deadEntity.getY() + auraRadius, deadEntity.getZ() + auraRadius);
		List<Entity> nearbyEntities = serverLevel.getEntities(deadEntity, area, entity -> (entity instanceof LivingEntity || entity instanceof ItemEntity || entity instanceof Projectile) && entity != deadEntity);
		for (Entity entity : nearbyEntities) {
			tryRandomTeleportDeath(entity, serverLevel, auraRadius);
		}
		List<Entity> entitiesToSwap = new ArrayList<>(nearbyEntities);
		Collections.shuffle(entitiesToSwap, new Random(serverLevel.random.nextLong()));
		for (int i = 0; i < entitiesToSwap.size() - 1; i += 2) {
			Entity entity1 = entitiesToSwap.get(i);
			Entity entity2 = entitiesToSwap.get(i + 1);
			performEntitySwapDeath(entity1, entity2, serverLevel);
		}
	}

	private static boolean tryRandomTeleportDeath(Entity entity, ServerLevel serverLevel, double auraRadius) {
		RandomSource random = serverLevel.random;
		for (int i = 0; i < MAX_TELEPORT_ATTEMPTS; i++) {
			double offsetX = (random.nextDouble() - 0.5) * 2 * auraRadius;
			double offsetY = (random.nextDouble() - 0.5) * auraRadius;
			double offsetZ = (random.nextDouble() - 0.5) * 2 * auraRadius;
			double newX = entity.getX() + offsetX;
			double newY = entity.getY() + offsetY;
			double newZ = entity.getZ() + offsetZ;
			BlockPos pos = new BlockPos((int) Math.floor(newX), (int) Math.floor(newY), (int) Math.floor(newZ));
			newY = findSafeY(serverLevel, pos, newY);
			if (newY == Double.NEGATIVE_INFINITY) {
				continue;
			}
			if (isSafeTeleportLocation(entity, newX, newY, newZ, serverLevel)) {
				entity.teleportTo(newX, newY, newZ);
				spawnAuraParticles(entity, serverLevel);
				serverLevel.playSound(null, newX, newY, newZ, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 0.8F + random.nextFloat() * 0.4F);
				return true;
			}
		}
		return false;
	}

	private static double findSafeY(ServerLevel serverLevel, BlockPos pos, double startY) {
		for (double y = startY - 2; y <= startY + 4; y++) {
			BlockPos testPos = new BlockPos(pos.getX(), (int) Math.floor(y), pos.getZ());
			BlockState below = serverLevel.getBlockState(testPos.below());
			BlockState atPos = serverLevel.getBlockState(testPos);
			BlockState above = serverLevel.getBlockState(testPos.above());
			if (atPos.isAir() && above.isAir() && below.isSolid() && !atPos.liquid() && !above.liquid() && !below.liquid()) {
				return y;
			}
		}
		return Double.NEGATIVE_INFINITY;
	}

	private static void performEntitySwapDeath(Entity entity1, Entity entity2, ServerLevel serverLevel) {
		Vec3 pos1 = entity1.position();
		Vec3 pos2 = entity2.position();
		if (pos1.distanceTo(pos2) > SWAP_RANGE * 2) {
			return;
		}
		entity1.teleportTo(pos2.x(), pos2.y(), pos2.z());
		entity2.teleportTo(pos1.x(), pos1.y(), pos1.z());
		spawnAuraParticles(entity1, serverLevel);
		spawnAuraParticles(entity2, serverLevel);
		serverLevel.playSound(null, pos1.x(), pos1.y(), pos1.z(), SoundEvents.ENDERMAN_TELEPORT, entity1.getSoundSource(), 1.0F, 0.8F + serverLevel.random.nextFloat() * 0.4F);
		serverLevel.playSound(null, pos2.x(), pos2.y(), pos2.z(), SoundEvents.ENDERMAN_TELEPORT, entity2.getSoundSource(), 1.0F, 0.8F + serverLevel.random.nextFloat() * 0.4F);
	}

	private static boolean isSafeTeleportLocation(Entity entity, double x, double y, double z, ServerLevel serverLevel) {
		BlockPos pos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
		BlockState below = serverLevel.getBlockState(pos.below());
		BlockState atPos = serverLevel.getBlockState(pos);
		BlockState above = serverLevel.getBlockState(pos.above());
		AABB boundingBox = entity.getBoundingBox().move(x - entity.getX(), y - entity.getY(), z - entity.getZ());
		return atPos.isAir() && above.isAir() && below.isSolid() && !atPos.liquid() && !above.liquid() && !below.liquid() && serverLevel.noCollision(entity, boundingBox);
	}

	private static void spawnAuraParticles(Entity entity, ServerLevel serverLevel) {
		RandomSource random = serverLevel.random;
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			double offsetX = (random.nextDouble() - 0.5) * PARTICLE_AURA_RADIUS;
			double offsetY = random.nextDouble() * entity.getBbHeight();
			double offsetZ = (random.nextDouble() - 0.5) * PARTICLE_AURA_RADIUS;
			serverLevel.sendParticles(ParticleTypes.PORTAL, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, 1, 0.1, 0.1, 0.1, 0.02);
		}
	}
}
