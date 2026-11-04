package net.jaams.archaeology.handler.vigor;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

import java.util.List;

@Mod.EventBusSubscriber
public class GuardianVigorHandler {
	@SubscribeEvent
	public static void preventGuardianTargeting(LivingChangeTargetEvent event) {
		Entity entity = event.getEntity();
		Entity targetEntity = event.getNewTarget();
		if (entity == null || targetEntity == null) {
			return;
		}
		if (entity instanceof Guardian || entity instanceof ElderGuardian) {
			if (targetEntity instanceof LivingEntity livingTarget && livingTarget.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void updateGuardianBehavior(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Mob mob && (entity instanceof Guardian || entity instanceof ElderGuardian)) {
			LivingEntity target = mob.getTarget();
			if (target != null && target.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get())) {
				mob.setTarget(null);
			}
		}
		if (entity.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get())) {
			if (entity.hasEffect(MobEffects.DIG_SLOWDOWN)) {
				entity.removeEffect(MobEffects.DIG_SLOWDOWN);
			}
		}
	}

	@SubscribeEvent
	public static void protectFromGuardianDamage(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get())) {
			Entity source = event.getSource().getEntity();
			if (source instanceof Guardian || source instanceof ElderGuardian) {
				event.setCanceled(true);
			} else if (source instanceof LivingEntity livingSource) {
				List<Guardian> nearbyGuardians = entity.level().getEntitiesOfClass(Guardian.class, entity.getBoundingBox().inflate(10));
				List<ElderGuardian> nearbyElderGuardians = entity.level().getEntitiesOfClass(ElderGuardian.class, entity.getBoundingBox().inflate(10));
				for (Guardian guardian : nearbyGuardians) {
					if (guardian.getTarget() == null || guardian.getTarget() != entity) {
						guardian.setTarget(livingSource);
					}
				}
				for (ElderGuardian elderGuardian : nearbyElderGuardians) {
					if (elderGuardian.getTarget() == null || elderGuardian.getTarget() != entity) {
						elderGuardian.setTarget(livingSource);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void redirectGuardiansOnAttack(LivingAttackEvent event) {
		LivingEntity defender = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (defender == null || attacker == null || !(attacker instanceof LivingEntity)) {
			return;
		}
		LivingEntity livingAttacker = (LivingEntity) attacker;
		LevelAccessor world = defender.level();
		Vec3 position = defender.position();
		if (defender.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get())) {
			List<Mob> nearbyGuardians = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(15), e -> (e instanceof Guardian || e instanceof ElderGuardian) && e != defender);
			for (Mob guardian : nearbyGuardians) {
				guardian.setTarget(livingAttacker);
			}
		}
		if (livingAttacker.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get())) {
			List<Mob> nearbyGuardians = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(7.5), e -> (e instanceof Guardian || e instanceof ElderGuardian) && e != defender);
			for (Mob guardian : nearbyGuardians) {
				guardian.setTarget(defender);
			}
		}
	}

	@SubscribeEvent
	public static void preventAirDepletion(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get()) && entity.isUnderWater()) {
			entity.setAirSupply(entity.getMaxAirSupply());
		}
	}

	@SubscribeEvent
	public static void spikesDefense(LivingAttackEvent event) {
		LivingEntity defender = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (defender.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get()) && event.getSource().is(DamageTypes.MOB_ATTACK) && attacker instanceof LivingEntity livingAttacker) {
			MobEffectInstance effect = defender.getEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get());
			int effectLevel = effect != null ? effect.getAmplifier() + 1 : 1;
			float damage = 2.0F + (effectLevel * 0.5F);
			livingAttacker.hurt(defender.level().damageSources().thorns(defender), damage);
			if (!defender.level().isClientSide() && defender.level() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.CRIT, defender.getX(), defender.getY() + 1.0, defender.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
			}
		}
	}

	@SubscribeEvent
	public static void boostBreakSpeedUnderWater(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		if (player.hasEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get()) && player.isInWater()) {
			MobEffectInstance effect = player.getEffect(JaamsArchaeologyModMobEffects.GUARDIAN_VIGOR.get());
			int effectLevel = effect != null ? effect.getAmplifier() + 1 : 1;
			float speedMultiplier = 1.30F + (effectLevel - 1) * 0.10F;
			event.setNewSpeed(event.getOriginalSpeed() * speedMultiplier);
		}
	}
}
