package net.jaams.archaeology.handler.vigor;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;

import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

import java.util.List;

@Mod.EventBusSubscriber
public class ExplosiveVigorHandler {
	@SubscribeEvent
	public static void preventCreeperTargeting(LivingChangeTargetEvent event) {
		Entity entity = event.getEntity();
		Entity targetEntity = event.getNewTarget();
		if (entity == null || targetEntity == null) {
			return;
		}
		if (entity instanceof Creeper) {
			if (targetEntity instanceof LivingEntity livingTarget && livingTarget.hasEffect(JaamsArchaeologyModMobEffects.EXPLOSIVE_VIGOR.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void updateCreeperBehavior(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Mob mob && entity instanceof Creeper) {
			LivingEntity target = mob.getTarget();
			if (target != null && target.hasEffect(JaamsArchaeologyModMobEffects.EXPLOSIVE_VIGOR.get())) {
				mob.setTarget(null);
			}
		}
	}

	@SubscribeEvent
	public static void redirectCreepersOnAttack(LivingAttackEvent event) {
		LivingEntity defender = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (defender == null || attacker == null || !(attacker instanceof LivingEntity)) {
			return;
		}
		LivingEntity livingAttacker = (LivingEntity) attacker;
		LevelAccessor world = defender.level();
		Vec3 position = defender.position();
		if (defender.hasEffect(JaamsArchaeologyModMobEffects.EXPLOSIVE_VIGOR.get())) {
			List<Mob> nearbyCreepers = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(15), e -> e instanceof Creeper && e != defender);
			for (Mob creeper : nearbyCreepers) {
				creeper.setTarget(livingAttacker);
			}
		}
		if (livingAttacker.hasEffect(JaamsArchaeologyModMobEffects.EXPLOSIVE_VIGOR.get())) {
			List<Mob> nearbyCreepers = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(7.5), e -> e instanceof Creeper && e != defender);
			for (Mob creeper : nearbyCreepers) {
				creeper.setTarget(defender);
			}
		}
	}

	@SubscribeEvent
	public static void preventExplosionDamage(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();
		if ((source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)) && entity.hasEffect(JaamsArchaeologyModMobEffects.EXPLOSIVE_VIGOR.get())) {
			MobEffectInstance effect = entity.getEffect(JaamsArchaeologyModMobEffects.EXPLOSIVE_VIGOR.get());
			int effectLevel = effect.getAmplifier() + 1;
			float reduction = 0.75F + (effectLevel * 0.05F);
			float newDamage = event.getAmount() * (1.0F - reduction);
			event.setAmount(Math.max(0.0F, newDamage));
		}
	}

	@SubscribeEvent
	public static void triggerExplosionOnDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.hasEffect(JaamsArchaeologyModMobEffects.EXPLOSIVE_VIGOR.get())) {
			MobEffectInstance effect = entity.getEffect(JaamsArchaeologyModMobEffects.EXPLOSIVE_VIGOR.get());
			if (effect != null) {
				int effectLevel = effect.getAmplifier() + 1;
				float explosionPower = 2.5F + (effectLevel * 0.5F);
				Level world = entity.level();
				world.explode(null, entity.getX(), entity.getY(), entity.getZ(), explosionPower, false, Level.ExplosionInteraction.MOB);
			}
		}
	}
}
