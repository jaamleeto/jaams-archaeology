package net.jaams.archaeology.handler.vigor;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;

import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

import java.util.UUID;
import java.util.List;

@Mod.EventBusSubscriber
public class SkeletalVigorHandler {
	private static final UUID SKELETAL_VIGOR_ITEM_USE_SPEED_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

	@SubscribeEvent
	public static void preventSkeletonTargeting(LivingChangeTargetEvent event) {
		Entity entity = event.getEntity();
		Entity targetEntity = event.getNewTarget();
		if (entity == null || targetEntity == null) {
			return;
		}
		if (entity instanceof AbstractSkeleton) {
			if (targetEntity instanceof LivingEntity livingTarget && livingTarget.hasEffect(JaamsArchaeologyModMobEffects.SKELETAL_VIGOR.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void updateSkeletonBehavior(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Mob mob && entity instanceof AbstractSkeleton) {
			LivingEntity target = mob.getTarget();
			if (target != null && target.hasEffect(JaamsArchaeologyModMobEffects.SKELETAL_VIGOR.get())) {
				mob.setTarget(null);
			}
		}
	}

	@SubscribeEvent
	public static void redirectSkeletonsOnAttack(LivingAttackEvent event) {
		LivingEntity defender = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (defender == null || attacker == null || !(attacker instanceof LivingEntity)) {
			return;
		}
		LivingEntity livingAttacker = (LivingEntity) attacker;
		LevelAccessor world = defender.level();
		Vec3 position = defender.position();
		if (defender.hasEffect(JaamsArchaeologyModMobEffects.SKELETAL_VIGOR.get())) {
			List<Mob> nearbySkeletons = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(15), e -> (e instanceof AbstractSkeleton) && e != defender);
			for (Mob skeleton : nearbySkeletons) {
				skeleton.setTarget(livingAttacker);
			}
		}
		if (livingAttacker.hasEffect(JaamsArchaeologyModMobEffects.SKELETAL_VIGOR.get())) {
			List<Mob> nearbySkeletons = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(12.5), e -> (e instanceof AbstractSkeleton) && e != defender);
			for (Mob skeleton : nearbySkeletons) {
				skeleton.setTarget(defender);
			}
		}
	}

	@SubscribeEvent
	public static void ironGolemActiveTargeting(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof IronGolem golem && !entity.level().isClientSide && entity.tickCount % 20 == 0) {
			AABB area = new AABB(entity.position(), entity.position()).inflate(16);
			List<LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(LivingEntity.class, area, e -> e.hasEffect(JaamsArchaeologyModMobEffects.SKELETAL_VIGOR.get()) && e != entity && golem.canAttack(e));
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
		if (entity.hasEffect(JaamsArchaeologyModMobEffects.SKELETAL_VIGOR.get()) && !entity.level().isClientSide) {
			if (entity.hasEffect(MobEffects.REGENERATION)) {
				entity.removeEffect(MobEffects.REGENERATION);
			}
			if (entity.hasEffect(MobEffects.POISON)) {
				entity.removeEffect(MobEffects.POISON);
			}
		}
	}

	@SubscribeEvent
	public static void preventWitherTargeting(LivingChangeTargetEvent event) {
		Entity entity = event.getEntity();
		Entity target = event.getNewTarget();
		if (entity instanceof WitherBoss && target instanceof LivingEntity livingTarget) {
			if (livingTarget.hasEffect(JaamsArchaeologyModMobEffects.SKELETAL_VIGOR.get())) {
				event.setCanceled(true);
			}
		}
	}
}
