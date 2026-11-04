package net.jaams.archaeology.handler.vigor;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;

import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

import java.util.List;

@Mod.EventBusSubscriber
public class RottenVigorHandler {
	@SubscribeEvent
	public static void preventZombieTargeting(LivingChangeTargetEvent event) {
		Entity entity = event.getEntity();
		Entity targetEntity = event.getNewTarget();
		if (entity == null || targetEntity == null) {
			return;
		}
		if (entity instanceof Zombie || entity instanceof Husk || entity instanceof Drowned) {
			if (targetEntity instanceof LivingEntity livingTarget && livingTarget.hasEffect(JaamsArchaeologyModMobEffects.ROTTEN_VIGOR.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void updateZombieBehavior(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Mob mob && (entity instanceof Zombie || entity instanceof Husk || entity instanceof Drowned)) {
			LivingEntity target = mob.getTarget();
			if (target != null && target.hasEffect(JaamsArchaeologyModMobEffects.ROTTEN_VIGOR.get())) {
				mob.setTarget(null);
			}
		}
	}

	@SubscribeEvent
	public static void redirectZombiesOnAttack(LivingAttackEvent event) {
		LivingEntity defender = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (defender == null || attacker == null || !(attacker instanceof LivingEntity)) {
			return;
		}
		LivingEntity livingAttacker = (LivingEntity) attacker;
		LevelAccessor world = defender.level();
		Vec3 position = defender.position();
		if (defender.hasEffect(JaamsArchaeologyModMobEffects.ROTTEN_VIGOR.get())) {
			List<Mob> nearbyZombies = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(15), e -> (e instanceof Zombie || e instanceof Husk || e instanceof Drowned) && e != defender);
			for (Mob zombie : nearbyZombies) {
				zombie.setTarget(livingAttacker);
			}
		}
		if (livingAttacker.hasEffect(JaamsArchaeologyModMobEffects.ROTTEN_VIGOR.get())) {
			List<Mob> nearbyZombies = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(7.5), // Usando 15/2 del código original
					e -> (e instanceof Zombie || e instanceof Husk || e instanceof Drowned) && e != defender);
			for (Mob zombie : nearbyZombies) {
				zombie.setTarget(defender);
			}
		}
	}

	@SubscribeEvent
	public static void ironGolemActiveTargeting(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof IronGolem golem && !entity.level().isClientSide && entity.tickCount % 20 == 0) {
			AABB area = new AABB(entity.position(), entity.position()).inflate(16);
			List<LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(LivingEntity.class, area, e -> e.hasEffect(JaamsArchaeologyModMobEffects.ROTTEN_VIGOR.get()) && e != entity && golem.canAttack(e));
			if (!nearbyEntities.isEmpty()) {
				LivingEntity closestTarget = nearbyEntities.stream().min((e1, e2) -> Double.compare(entity.distanceToSqr(e1), entity.distanceToSqr(e2))).orElse(null);
				if (closestTarget != null) {
					golem.setTarget(closestTarget);
				}
			}
		}
	}

	@SubscribeEvent
	public static void blockRegenerationAndPoison(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.hasEffect(JaamsArchaeologyModMobEffects.ROTTEN_VIGOR.get()) && !entity.level().isClientSide) {
			if (entity.hasEffect(MobEffects.REGENERATION)) {
				entity.removeEffect(MobEffects.REGENERATION);
			}
			if (entity.hasEffect(MobEffects.POISON)) {
				entity.removeEffect(MobEffects.POISON);
			}
		}
	}
}
