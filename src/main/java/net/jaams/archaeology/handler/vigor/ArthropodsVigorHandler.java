package net.jaams.archaeology.handler.vigor;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

import java.util.List;

@Mod.EventBusSubscriber
public class ArthropodsVigorHandler {
	private static final float WEB_CHANCE_NORMAL = 0.2F;
	private static final float WEB_CHANCE_CRITICAL = 0.5F;
	private static final float POISON_CHANCE_NORMAL = 0.2F;
	private static final float POISON_CHANCE_CRITICAL = 0.5F;

	@SubscribeEvent
	public static void preventArthropodTargeting(LivingChangeTargetEvent event) {
		Entity entity = event.getEntity();
		Entity targetEntity = event.getNewTarget();
		if (entity == null || targetEntity == null) {
			return;
		}
		if (entity instanceof LivingEntity livingEntity && livingEntity.getMobType() == MobType.ARTHROPOD) {
			if (targetEntity instanceof LivingEntity livingTarget && livingTarget.hasEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void protectFromArthropodDamage(LivingDamageEvent event) {
		Entity source = event.getSource().getEntity();
		LivingEntity target = event.getEntity();
		if (source instanceof LivingEntity livingSource && livingSource.getMobType() == MobType.ARTHROPOD) {
			if (target.hasEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void redirectArthropodsOnAttack(LivingAttackEvent event) {
		LivingEntity defender = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (defender == null || attacker == null || !(attacker instanceof LivingEntity)) {
			return;
		}
		LivingEntity livingAttacker = (LivingEntity) attacker;
		LevelAccessor world = defender.level();
		Vec3 position = defender.position();
		if (defender.hasEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get())) {
			List<Mob> nearbyArthropods = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(15), e -> e.getMobType() == MobType.ARTHROPOD && e != defender).stream().limit(10).toList();
			for (Mob arthropod : nearbyArthropods) {
				arthropod.setTarget(livingAttacker);
			}
		}
		if (livingAttacker.hasEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get())) {
			List<Mob> nearbyArthropods = world.getEntitiesOfClass(Mob.class, new AABB(position, position).inflate(7.5), e -> e.getMobType() == MobType.ARTHROPOD && e != defender).stream().limit(10).toList();
			for (Mob arthropod : nearbyArthropods) {
				arthropod.setTarget(defender);
			}
		}
	}

	@SubscribeEvent
	public static void updateArthropodBehavior(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Mob mob && mob.getMobType() == MobType.ARTHROPOD) {
			LivingEntity target = mob.getTarget();
			if (target != null && target.hasEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get())) {
				mob.setTarget(null);
			}
		}
	}

	@SubscribeEvent
	public static void onAttackPlaceWeb(LivingAttackEvent event) {
		Entity sourceentity = event.getSource().getEntity();
		LivingEntity entity = event.getEntity();
		if (sourceentity == null || !(sourceentity instanceof LivingEntity livingAttacker) || entity == null) {
			return;
		}
		if (!livingAttacker.hasEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get())) {
			return;
		}
		Level level = entity.level();
		if (level.isClientSide) {
			return;
		}
		boolean isCritical = sourceentity instanceof Player && ModUtils.isCritical((Player) sourceentity, entity, ((Player) sourceentity).getAttackStrengthScale(0.5F));
		int effectLevel = livingAttacker.getEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get()).getAmplifier();
		float webChance = isCritical ? WEB_CHANCE_CRITICAL : WEB_CHANCE_NORMAL;
		webChance = Math.min(1.0F, webChance + (effectLevel * 0.05F));
		float poisonChance = isCritical ? POISON_CHANCE_CRITICAL : POISON_CHANCE_NORMAL;
		poisonChance = Math.min(1.0F, poisonChance + (effectLevel * 0.05F));
		if (level.random.nextFloat() < webChance) {
			BlockPos defenderPos = entity.blockPosition();
			BlockState blockState = level.getBlockState(defenderPos);
			if (blockState.isAir() || blockState.canBeReplaced()) {
				level.setBlock(defenderPos, Blocks.COBWEB.defaultBlockState(), 3);
				spawnWebEffects((ServerLevel) level, defenderPos);
			}
		}
		if (level.random.nextFloat() < poisonChance) {
			entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
		}
	}

	@SubscribeEvent
	public static void onBreakCobweb(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		BlockState blockState = event.getState();
		if (blockState.getBlock() == Blocks.COBWEB && player.hasEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get())) {
			int effectLevel = player.getEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get()).getAmplifier();
			event.setNewSpeed(10.0F * (effectLevel + 1));
		}
	}

	private static void spawnWebEffects(ServerLevel level, BlockPos pos) {
		level.playSound(null, pos, SoundEvents.SPIDER_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
		for (int i = 0; i < 10; i++) {
			level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5 + (level.random.nextDouble() - 0.5), pos.getY() + 0.5 + (level.random.nextDouble() - 0.5), pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5), 1, 0.0, 0.05, 0.0, 0.0);
		}
	}
}
