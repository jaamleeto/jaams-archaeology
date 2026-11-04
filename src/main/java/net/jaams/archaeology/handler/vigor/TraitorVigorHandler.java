package net.jaams.archaeology.handler.vigor;

import org.joml.Vector3f;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.DustParticleOptions;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

import java.util.List;

@Mod.EventBusSubscriber
public class TraitorVigorHandler {
	@SubscribeEvent
	public static void preventIllagerTargeting(LivingChangeTargetEvent event) {
		Entity entity = event.getEntity();
		Entity targetEntity = event.getNewTarget();
		if (entity == null || targetEntity == null) {
			return;
		}
		if (entity instanceof LivingEntity livingEntity && (livingEntity.getMobType() == MobType.ILLAGER || entity instanceof Vex || entity instanceof Ravager)) {
			if (targetEntity instanceof LivingEntity livingTarget && livingTarget.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void updateIllagerBehavior(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Mob mob && (entity.getMobType() == MobType.ILLAGER || entity instanceof Vex || entity instanceof Ravager)) {
			LivingEntity target = mob.getTarget();
			if (target != null && target.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())) {
				mob.setTarget(null);
			}
		}
		if (entity.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())) {
			if (entity.hasEffect(MobEffects.BAD_OMEN)) {
				entity.removeEffect(MobEffects.BAD_OMEN);
			}
		}
	}

	@SubscribeEvent
	public static void redirectIllagersOnAttack(LivingAttackEvent event) {
		LivingEntity defender = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (defender == null || attacker == null || !(attacker instanceof LivingEntity)) {
			return;
		}
		LivingEntity livingAttacker = (LivingEntity) attacker;
		LevelAccessor world = defender.level();
		Vec3 position = defender.position();
		if (defender.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())) {
			List<Mob> nearbyIllagers = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(15), e -> (e.getMobType() == MobType.ILLAGER || e instanceof Vex || e instanceof Ravager) && e != defender);
			for (Mob illager : nearbyIllagers) {
				illager.setTarget(livingAttacker);
			}
		}
		if (livingAttacker.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())) {
			List<Mob> nearbyIllagers = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(10), e -> (e.getMobType() == MobType.ILLAGER || e instanceof Vex || e instanceof Ravager) && e != defender);
			for (Mob illager : nearbyIllagers) {
				illager.setTarget(defender);
			}
		}
	}

	@SubscribeEvent
	public static void convertAllayToVex(PlayerInteractEvent.EntityInteract event) {
		Player player = event.getEntity();
		Entity target = event.getTarget();
		if (!(target instanceof Allay allay) || event.getLevel().isClientSide()) {
			return;
		}
		if (!player.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())) {
			return;
		}
		Level level = allay.level();
		Vex vex = new Vex(EntityType.VEX, level);
		vex.setPos(allay.getX(), allay.getY(), allay.getZ());
		vex.setYRot(allay.getYRot());
		vex.setXRot(allay.getXRot());
		vex.setDeltaMovement(allay.getDeltaMovement());
		vex.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0);
		vex.setNoAi(false);
		vex.invulnerableTime = 60;
		level.addFreshEntity(vex);
		allay.remove(Entity.RemovalReason.DISCARDED);
		event.setCanceled(true);
		player.swing(event.getHand(), true);
		level.playSound(null, vex.getX(), vex.getY(), vex.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
		if (level instanceof ServerLevel serverLevel) {
			DustParticleOptions dust = new DustParticleOptions(new Vector3f(0.6F, 0.1F, 0.1F), 1.0F);
			int particleCount = 32;
			double radius = 0.5;
			for (int i = 0; i < particleCount; i++) {
				double theta = level.random.nextDouble() * 2 * Math.PI;
				double phi = level.random.nextDouble() * Math.PI;
				double r = radius * Math.cbrt(level.random.nextDouble());
				double x = r * Math.sin(phi) * Math.cos(theta);
				double y = r * Math.sin(phi) * Math.sin(theta);
				double z = r * Math.cos(phi);
				serverLevel.sendParticles(dust, vex.getX() + x, vex.getY() + 0.5 + y, vex.getZ() + z, 1, 0.0, 0.0, 0.0, 0.03);
			}
		}
	}

	@SubscribeEvent
	public static void spawnEvokerFangsOnDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (!entity.level().isClientSide() && entity.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())) {
			Level level = entity.level();
			MobEffectInstance effect = entity.getEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get());
			int amplifier = effect.getAmplifier();
			double radius = 8.0 + 2.0 * amplifier;
			Vec3 position = entity.position();
			AABB searchArea = new AABB(position, position).inflate(radius);
			List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchArea,
					e -> e != entity && e.isAlive() && !(e instanceof Vex) && !(e instanceof Player player && player.isCreative())
							&& (entity instanceof Player
									? (e instanceof Player ? !ModUtils.isAlliedEntity((Player) entity, e) : !e.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get()))
									: !e.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())));
			for (LivingEntity target : nearbyEntities) {
				EvokerFangs fangs = new EvokerFangs(level, target.getX(), target.getY() + 0.1, target.getZ(), target.getYRot(), 0, entity);
				fangs.addTag("TraitorVigorFangs");
				level.addFreshEntity(fangs);
				if (level instanceof ServerLevel serverLevel) {
					serverLevel.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 0.5, target.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
				}
			}
			if (!nearbyEntities.isEmpty()) {
				level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, SoundSource.HOSTILE, 1.0F, 1.0F);
			}
		}
	}

	@SubscribeEvent
	public static void modifyEvokerFangsDamage(LivingDamageEvent event) {
		DamageSource source = event.getSource();
		if (source.getDirectEntity() instanceof EvokerFangs fangs && fangs.getTags().contains("TraitorVigorFangs")) {
			LivingEntity owner = (LivingEntity) source.getEntity();
			MobEffectInstance effect = owner.getEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get());
			if (effect != null) {
				int amplifier = effect.getAmplifier();
				event.setAmount(event.getAmount() * (2.0F + (amplifier / 2)));
			}
		}
	}
}
