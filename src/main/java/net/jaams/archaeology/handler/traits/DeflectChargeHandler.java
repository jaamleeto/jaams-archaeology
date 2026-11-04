package net.jaams.archaeology.handler.traits;

import org.checkerframework.checker.units.qual.g;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.util.ModTraits;
import net.jaams.archaeology.particle.MiniSweepParticleData;
import net.jaams.archaeology.particle.EnhancedHitParticleData;
import net.jaams.archaeology.init.JaamsArchaeologyModEnchantments;
import net.jaams.archaeology.configuration.common.TraitsConfig;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class DeflectChargeHandler {
	public static final float PARTICLE_SIZE_DEFAULT = 0.65F;
	public static final float PARTICLE_OFFSET_Y = 0.6F;
	public static final float DEFLECT_PARTICLE_DISTANCE = 1.8F;

	public static float getMaxDeflectAmount(ItemStack stack) {
		return (float) ModUtils.getConfigOrNbtDouble(stack, "DeflectMaxAmount", () -> TraitsConfig.DEFLECT_CHARGE_MAX_AMOUNT.get());
	}

	public static float getMinDamageToAdd(ItemStack stack) {
		return (float) ModUtils.getConfigOrNbtDouble(stack, "DeflectMinAmount", () -> TraitsConfig.DEFLECT_CHARGE_MIN_AMOUNT.get());
	}

	public static float getBaseAreaDamage(ItemStack stack) {
		return (float) ModUtils.getConfigOrNbtDouble(stack, "DeflectBaseDamage", () -> TraitsConfig.DEFLECT_CHARGE_BASE_DAMAGE.get());
	}

	public static double getAreaRange(ItemStack stack) {
		return ModUtils.getConfigOrNbtDouble(stack, "DeflectRange", () -> TraitsConfig.DEFLECT_CHARGE_RANGE.get());
	}

	public static double getKnockbackForce(ItemStack stack) {
		return ModUtils.getConfigOrNbtDouble(stack, "DeflectKnockback", () -> TraitsConfig.DEFLECT_CHARGE_KNOCKBACK.get());
	}

	public static int getReleaseCooldownTicks(ItemStack stack) {
		return ModUtils.getConfigOrNbtInt(stack, "DeflectCooldownTicks", () -> TraitsConfig.DEFLECT_CHARGE_COOLDOWN_TICKS.get());
	}

	public static boolean isPassiveEnabled(ItemStack stack) {
		return ModUtils.getConfigOrNbtBoolean(stack, "DeflectPassive", () -> TraitsConfig.DEFLECT_CHARGE_PASSIVE.get());
	}

	public static float getPassiveFactor(ItemStack stack) {
		return (float) ModUtils.getConfigOrNbtDouble(stack, "DeflectPassiveFactor", () -> TraitsConfig.DEFLECT_CHARGE_PASSIVE_FACTOR.get());
	}

	public static boolean canMobsGainPassiveCharge() {
		return TraitsConfig.DEFLECT_CHARGE_MOBS_PASSIVE.get();
	}

	public static boolean isColorChangeEnabled(ItemStack stack) {
		return ModUtils.getConfigOrNbtBoolean(stack, "DeflectColor", () -> TraitsConfig.DEFLECT_CHARGE_COLOR.get());
	}

	public static boolean shouldReleaseOnDeath() {
		return TraitsConfig.DEFLECT_CHARGE_RELEASE_ON_DEATH.get();
	}

	public static boolean shouldReleaseOnBreak() {
		return TraitsConfig.DEFLECT_CHARGE_RELEASE_ON_BREAK.get();
	}

	@SubscribeEvent
	public static void onDeflectChargeLivingTick(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		processStack(entity, entity.getMainHandItem(), true);
		processStack(entity, entity.getOffhandItem(), false);
	}

	private static void processStack(LivingEntity entity, ItemStack stack, boolean inHand) {
		if (stack.isEmpty())
			return;
		if (!ModTraits.isDeflectChargeItem(stack))
			return;
		CompoundTag tag = stack.getOrCreateTag();
		double deflect = tag.getDouble("DeflectAmount");
		if (Double.isNaN(deflect) || deflect < 0) {
			deflect = 0.0;
			tag.putDouble("DeflectAmount", 0.0);
		}
		boolean hasStrikehold = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), stack) > 0;
		boolean cooldown = entity instanceof Player player && player.getCooldowns().isOnCooldown(stack.getItem());
		if (!inHand && !hasStrikehold) {
			if (deflect > 0 || tag.contains("LastDeflectColorLevel")) {
				tag.putDouble("DeflectAmount", 0);
				restoreOriginalColor(stack, tag);
				tag.remove("LastDeflectColorLevel");
			}
			return;
		}
		boolean shouldResetCharge = cooldown && !hasStrikehold;
		if (shouldResetCharge && deflect > 0) {
			deflect = 0;
			tag.putDouble("DeflectAmount", 0);
		}
		double lastDeflect = tag.getDouble("LastDeflectAmountProcessed");
		boolean lastResetState = tag.getBoolean("LastShouldResetState");
		if (lastDeflect == deflect && lastResetState == shouldResetCharge)
			return;
		tag.putDouble("LastDeflectAmountProcessed", deflect);
		tag.putBoolean("LastShouldResetState", shouldResetCharge);
		int desiredLevel = 0;
		if (deflect > 30)
			desiredLevel = 3;
		else if (deflect > 15)
			desiredLevel = 2;
		else if (deflect > 5)
			desiredLevel = 1;
		int savedLevel = tag.getInt("LastDeflectColorLevel");
		if (desiredLevel != savedLevel) {
			restoreOriginalColor(stack, tag);
			if (desiredLevel > 0 && isColorChangeEnabled(stack)) {
				if (!tag.contains("DeflectOriginalColor")) {
					int currentColor = ModUtils.getCurrentColor(stack);
					if (currentColor != -1)
						tag.putInt("DeflectOriginalColor", currentColor);
				}
				int newColor = getDeflectItemColor(deflect);
				if (newColor != -1)
					ModUtils.applyDyeColor(stack, newColor);
			} else {
				if (tag.contains("DeflectOriginalColor")) {
					int original = tag.getInt("DeflectOriginalColor");
					ModUtils.applyDyeColor(stack, original);
					tag.remove("DeflectOriginalColor");
				} else {
					ModUtils.removeColor(stack);
				}
				tag.remove("LastDeflectColorLevel");
			}
			tag.putInt("LastDeflectColorLevel", desiredLevel);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onDeflectChargeLivingAttack(LivingAttackEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;
		ItemStack itemStack = player.getUseItem();
		if (!ModTraits.isDeflectChargeItem(itemStack) || !isBlocking(player))
			return;
		Entity source = event.getSource().getEntity();
		Level level = player.level();
		if (source instanceof LivingEntity attacker && canDisableBlocking(attacker.getMainHandItem(), itemStack, player, attacker)) {
			disableBlockingWeapon(player, itemStack);
			event.setCanceled(true);
			return;
		}
		if (source != null && isInBlockingArea(player, source)) {
			event.setCanceled(true);
			player.hurtTime = 0;
			player.hurtDuration = 0;
			player.invulnerableTime = 0;
			player.clearFire();
			SoundEvent blockSound = getBlockSound(itemStack);
			if (blockSound != null) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(), blockSound, player.getSoundSource(), 1.0F, 1.0F);
			}
			int damage = Math.max(0, Math.round(event.getAmount()));
			itemStack.hurtAndBreak(damage, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
			applyDeflectAmount(itemStack, event.getAmount(), source);
		}
	}

	@SubscribeEvent
	public static void onPassiveDeflectCharge(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		if (event.isCanceled() || event.getAmount() <= 0)
			return;
		if (entity instanceof Player player && player.isBlocking())
			return;
		if (!(entity instanceof Player) && !canMobsGainPassiveCharge())
			return;
		ItemStack main = entity.getMainHandItem();
		ItemStack off = entity.getOffhandItem();
		ItemStack targetStack = null;
		if (ModTraits.isDeflectChargeItem(main)) {
			targetStack = main;
		} else if (ModTraits.isDeflectChargeItem(off)) {
			targetStack = off;
		}
		if (targetStack == null)
			return;
		if (!isPassiveEnabled(targetStack))
			return;
		float factor = getPassiveFactor(targetStack);
		if (factor <= 0.0001f)
			return;
		float rawPassive = event.getAmount() * factor;
		float capped = Math.min(rawPassive, 15.0f);
		float minToAdd = getMinDamageToAdd(targetStack);
		if (capped >= minToAdd) {
			applyDeflectAmount(targetStack, capped, event.getSource().getEntity());
		}
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (!shouldReleaseOnDeath())
			return;
		LivingEntity entity = event.getEntity();
		ItemStack main = entity.getMainHandItem();
		ItemStack off = entity.getOffhandItem();
		ItemStack chargedStack = null;
		if (ModTraits.isDeflectChargeItem(main) && getDeflectAmount(main) > 0) {
			chargedStack = main;
		} else if (ModTraits.isDeflectChargeItem(off) && getDeflectAmount(off) > 0) {
			chargedStack = off;
		}
		if (chargedStack == null)
			return;
		float charge = getDeflectAmount(chargedStack);
		if (charge <= 0)
			return;
		float areaDamage = charge > 0 ? charge : getBaseAreaDamage(chargedStack);
		releaseAreaDamage(entity.level(), entity.getX(), entity.getY(), entity.getZ(), areaDamage, getAreaRange(chargedStack), getKnockbackForce(chargedStack), entity);
		if (entity.level() instanceof ServerLevel serverLevel) {
			spawnAreaReleaseParticles(serverLevel, entity.getX(), entity.getY() + 1.0, entity.getZ(), charge);
		}
		clearAllDeflectData(chargedStack);
	}

	public static void releaseOnBreak(ItemStack stack, LivingEntity holder) {
		if (!shouldReleaseOnBreak())
			return;
		float charge = getDeflectAmount(stack);
		if (charge <= 0)
			return;
		float areaDamage = charge > 0 ? charge : getBaseAreaDamage(stack);
		releaseAreaDamage(holder.level(), holder.getX(), holder.getY(), holder.getZ(), areaDamage, getAreaRange(stack), getKnockbackForce(stack), holder);
		if (holder.level() instanceof ServerLevel serverLevel) {
			spawnAreaReleaseParticles(serverLevel, holder.getX(), holder.getY() + 1.0, holder.getZ(), charge);
		}
		clearAllDeflectData(stack);
	}

	public static void spawnAreaReleaseParticles(ServerLevel level, double x, double y, double z, float deflectAmount) {
		EnhancedHitParticleData particle = getDeflectParticleData(deflectAmount);
		int count = deflectAmount >= 30 ? 24 : deflectAmount >= 15 ? 18 : deflectAmount >= 5 ? 12 : 8;
		for (int i = 0; i < count; i++) {
			double offsetX = level.random.nextGaussian() * 1.4;
			double offsetY = level.random.nextGaussian() * 1.0 + 0.6;
			double offsetZ = level.random.nextGaussian() * 1.4;
			double velX = level.random.nextGaussian() * 0.06;
			double velY = level.random.nextGaussian() * 0.10 + 0.05;
			double velZ = level.random.nextGaussian() * 0.06;
			level.sendParticles(particle, x + offsetX, y + offsetY, z + offsetZ, 1, velX, velY, velZ, 0.035);
		}
	}

	private static void releaseAreaDamage(Level level, double x, double y, double z, float damage, double range, double knockback, LivingEntity sourceEntity) {
		if (!(level instanceof ServerLevel serverLevel))
			return;
		AABB area = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
		List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, area, e -> e.isAlive() && e != sourceEntity);
		int count = targets.size();
		float finalDamage = (count > 1) ? damage / (float) Math.sqrt(count) : damage;
		for (LivingEntity target : targets) {
			target.hurt(sourceEntity.damageSources().generic(), finalDamage);
			Vec3 dir = new Vec3(target.getX() - x, 0, target.getZ() - z).normalize();
			target.knockback(knockback, dir.x, dir.z);
		}
		serverLevel.playSound(null, x, y, z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.0F, 0.9F);
	}

	public static void applyDeflectAmount(ItemStack itemStack, float rawDamage, Entity source) {
		if (rawDamage < getMinDamageToAdd(itemStack))
			return;
		CompoundTag tag = itemStack.getOrCreateTag();
		float incoming = Math.max(0.0F, rawDamage);
		int vengeanceLevel = EnchantmentHelper.getItemEnchantmentLevel(JaamsArchaeologyModEnchantments.VENGEANCE.get(), itemStack);
		int maxHits = vengeanceLevel == 0 ? 1 : vengeanceLevel * 3;
		List<Float> damages = loadDeflectDamages(tag);
		float currentTotal = calculateTotalDeflect(damages);
		if (currentTotal <= 0.0F) {
			damages.clear();
			tag.remove("DeflectDamages");
			tag.remove("AttackerName");
			currentTotal = 0.0F;
		}
		boolean addedOrReplaced = false;
		float maxAmount = getMaxDeflectAmount(itemStack);
		if (vengeanceLevel == 0) {
			if (incoming > currentTotal) {
				damages.clear();
				damages.add(incoming);
				addedOrReplaced = true;
			}
		} else {
			float spaceLeft = maxAmount - currentTotal;
			if (damages.size() < maxHits) {
				if (spaceLeft >= getMinDamageToAdd(itemStack)) {
					damages.add(incoming);
					addedOrReplaced = true;
				}
			} else {
				if (incoming > currentTotal && incoming <= maxAmount) {
					damages.clear();
					damages.add(incoming);
					addedOrReplaced = true;
				}
			}
		}
		if (addedOrReplaced) {
			float newTotal = calculateTotalDeflect(damages);
			tag.putFloat("DeflectAmount", Math.min(newTotal, maxAmount));
			saveDeflectDamages(tag, damages);
			if (source instanceof LivingEntity living) {
				tag.putString("AttackerName", living.getName().getString());
			} else {
				tag.putString("AttackerName", "Projectile");
			}
		}
	}

	public static float getDeflectAmount(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("DeflectDamages"))
			return 0.0F;
		List<Float> damages = loadDeflectDamages(tag);
		float total = calculateTotalDeflect(damages);
		if (total <= 0.0F && tag.contains("DeflectDamages")) {
			tag.remove("DeflectDamages");
			tag.remove("AttackerName");
		}
		return (float) Math.min(total, getMaxDeflectAmount(stack));
	}

	public static int applyAreaDamage(Player player, ItemStack stack) {
		Level level = player.level();
		if (!(level instanceof ServerLevel serverLevel))
			return 0;
		float charge = getDeflectAmount(stack);
		if (charge <= 0)
			return 0;
		float areaDamage = charge * 1.0f;
		double knockbackForce = getKnockbackForce(stack);
		double areaRange = getAreaRange(stack);
		AABB area = player.getBoundingBox().inflate(areaRange);
		DamageSource damageSource = player.damageSources().playerAttack(player);
		ItemStack mainHand = player.getMainHandItem();
		int smite = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SMITE, mainHand);
		int bane = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, mainHand);
		int fire = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, mainHand);
		int kb = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, mainHand);
		List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, area, e -> !ModUtils.isAlliedEntity(player, e) && e.isAlive() && e != player).stream().filter(t -> isInBlockingArea(player, t)).toList();
		int count = targets.size();
		float finalDamage = (count > 1) ? areaDamage / (float) Math.sqrt(count) : areaDamage;
		int successfulHits = 0;
		for (LivingEntity target : targets) {
			float dmg = finalDamage;
			if (smite > 0 && target.getMobType() == MobType.UNDEAD) {
				dmg += smite * 1.5F;
			}
			if (bane > 0 && target.getMobType() == MobType.ARTHROPOD) {
				dmg += bane * 1.5F;
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
			}
			if (target.hurt(damageSource, dmg)) {
				successfulHits++;
			}
			if (fire > 0) {
				target.setSecondsOnFire(fire * 4);
			}
			Vec3 dir = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ()).normalize().scale(-1);
			double kx = dir.x * (1 + kb * 0.5);
			double kz = dir.z * (1 + kb * 0.5);
			target.knockback(knockbackForce, kx, kz);
			if (!player.isCreative() && target instanceof Mob mob) {
				mob.setTarget(player);
			}
		}
		serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 0.9F);
		return successfulHits;
	}

	public static void spawnParticleInFront(ServerLevel serverLevel, LivingEntity entity, ItemStack weapon) {
		if (entity == null || serverLevel.isClientSide() || PARTICLE_SIZE_DEFAULT <= 0.0F)
			return;
		float deflectAmount = getDeflectAmount(weapon);
		Vec3 look = entity.getLookAngle().normalize();
		double sx = entity.getX();
		double sy = entity.getY() + entity.getEyeHeight() * PARTICLE_OFFSET_Y;
		double sz = entity.getZ();
		Vec3 start = new Vec3(sx, sy, sz);
		Vec3 end = start.add(look.scale(DEFLECT_PARTICLE_DISTANCE));
		ClipContext ctx = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
		BlockHitResult blockHit = serverLevel.clip(ctx);
		EntityHitResult entityHit = getEntityHitResult(serverLevel, entity, start, end);
		double dist = Math.max(DEFLECT_PARTICLE_DISTANCE - 0.1f, 0.1f);
		if (blockHit.getType() != HitResult.Type.MISS) {
			dist = Math.min(dist, start.distanceTo(blockHit.getLocation()));
		}
		if (entityHit != null) {
			dist = Math.min(dist, start.distanceTo(entityHit.getLocation()));
		}
		double px = sx + look.x * dist;
		double py = sy + look.y * dist;
		double pz = sz + look.z * dist;
		RandomSource rand = serverLevel.random;
		float r, g, b;
		if (deflectAmount >= 30.0F) {
			r = 1.0F;
			g = 0.0F;
			b = 0.0F;
		} else if (deflectAmount >= 15.0F) {
			r = 1.0F;
			g = 0.65F;
			b = 0.0F;
		} else if (deflectAmount >= 5.0F) {
			r = 1.0F;
			g = 1.0F;
			b = 0.0F;
		} else {
			float gray = 0.4F + rand.nextFloat() * 0.6F;
			r = g = b = gray;
		}
		serverLevel.sendParticles(new MiniSweepParticleData(r, g, b, PARTICLE_SIZE_DEFAULT), px, py, pz, 1, 0, 0, 0, 0);
	}

	public static void spawnBubbleSweep(ServerLevel serverLevel, Player player, float particleDistance, float particleSize) {
		if (serverLevel.isClientSide())
			return;
		Vec3 lookVec = player.getLookAngle().normalize();
		double startX = player.getX();
		double startY = player.getY() + player.getEyeHeight() * 0.8;
		double startZ = player.getZ();
		Vec3 startPos = new Vec3(startX, startY, startZ);
		int particleCount = 8;
		float arcAngle = (float) Math.toRadians(60);
		float angleStep = arcAngle / (particleCount - 1);
		RandomSource random = serverLevel.random;
		float pitch = player.getXRot();
		float clampedPitch = Math.max(-80.0F, Math.min(80.0F, pitch));
		Vec3 adjustedLookVec = Vec3.directionFromRotation(clampedPitch, player.getYRot()).normalize();
		for (int i = 0; i < particleCount; i++) {
			float angle = -arcAngle / 2 + i * angleStep;
			Vec3 rightVec = adjustedLookVec.cross(new Vec3(0, 1, 0)).normalize();
			Vec3 rotatedVec = adjustedLookVec.scale(Math.cos(angle)).add(rightVec.scale(Math.sin(angle))).normalize();
			Vec3 endPos = startPos.add(rotatedVec.scale(particleDistance));
			ClipContext clipContext = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
			BlockHitResult blockHit = serverLevel.clip(clipContext);
			EntityHitResult entityHit = getEntityHitResult(serverLevel, player, startPos, endPos);
			double adjustedDistance = Math.max(particleDistance - 0.1f, 0.1f);
			if (blockHit.getType() != HitResult.Type.MISS) {
				adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(blockHit.getLocation()));
			}
			if (entityHit != null) {
				adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(entityHit.getLocation()));
			}
			double particleX = startX + rotatedVec.x * adjustedDistance;
			double particleY = startY + rotatedVec.y * adjustedDistance;
			double particleZ = startZ + rotatedVec.z * adjustedDistance;
			serverLevel.sendParticles(ParticleTypes.BUBBLE, particleX + (random.nextDouble() - 0.5) * 0.1, particleY + (random.nextDouble() - 0.5) * 0.1, particleZ + (random.nextDouble() - 0.5) * 0.1, 1, 0.0, 0.0, 0.0, 0.0);
		}
	}

	public static EnhancedHitParticleData getDeflectParticleData(double deflectAmount) {
		float r, g, b, size = 1.0f;
		if (deflectAmount >= 30.0) {
			r = 1.00f;
			g = 0.10f;
			b = 0.05f;
			size = 0.6f;
		} else if (deflectAmount >= 15.0) {
			r = 1.00f;
			g = 0.60f;
			b = 0.00f;
			size = 0.5f;
		} else if (deflectAmount >= 5.0) {
			r = 1.00f;
			g = 0.95f;
			b = 0.20f;
			size = 0.4f;
		} else {
			r = 0.90f;
			g = 0.95f;
			b = 1.00f;
			size = 0.3f;
		}
		return new EnhancedHitParticleData(r, g, b, size);
	}

	public static int getDeflectItemColor(double deflectAmount) {
		if (deflectAmount >= 30.0)
			return 0xFFFF0000;
		if (deflectAmount >= 15.0)
			return 0xFFFF6600;
		if (deflectAmount >= 5.0)
			return 0xFFFFFF00;
		return -1;
	}

	public static void clearDeflectTags(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.remove("DeflectAmount");
		tag.remove("DeflectDamages");
		tag.remove("AttackerName");
		tag.remove("LastDeflectColorLevel");
		if (tag.contains("DeflectOriginalColor")) {
			ModUtils.applyDyeColor(stack, tag.getInt("DeflectOriginalColor"));
			tag.remove("DeflectOriginalColor");
		} else {
			ModUtils.removeColor(stack);
		}
	}

	public static void restoreOriginalColor(ItemStack stack, CompoundTag tag) {
		if (tag.contains("DeflectOriginalColor")) {
			int original = tag.getInt("DeflectOriginalColor");
			ModUtils.applyDyeColor(stack, original);
		} else {
			ModUtils.removeColor(stack);
		}
	}

	public static void clearAllDeflectData(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			tag.remove("DeflectAmount");
			tag.remove("DeflectDamages");
			tag.remove("AttackerName");
		}
	}

	private static List<Float> loadDeflectDamages(CompoundTag tag) {
		List<Float> list = new ArrayList<>();
		if (tag.contains("DeflectDamages")) {
			ListTag damagesTag = tag.getList("DeflectDamages", Tag.TAG_FLOAT);
			for (int i = 0; i < damagesTag.size(); i++) {
				float val = damagesTag.getFloat(i);
				if (val > 0)
					list.add(val);
			}
		}
		return list;
	}

	private static float calculateTotalDeflect(List<Float> damages) {
		float sum = 0;
		for (float d : damages)
			sum += d;
		return sum;
	}

	private static void saveDeflectDamages(CompoundTag tag, List<Float> damages) {
		ListTag listTag = new ListTag();
		for (float d : damages) {
			listTag.add(FloatTag.valueOf(d));
		}
		tag.put("DeflectDamages", listTag);
	}

	private static EntityHitResult getEntityHitResult(ServerLevel level, LivingEntity shooter, Vec3 startPos, Vec3 endPos) {
		AABB aabb = new AABB(startPos, endPos).inflate(1.0);
		for (Entity entity : level.getEntities(shooter, aabb, e -> e instanceof LivingEntity && e.isPickable())) {
			AABB entityBox = entity.getBoundingBox().inflate(0.3);
			Optional<Vec3> hit = entityBox.clip(startPos, endPos);
			if (hit.isPresent()) {
				return new EntityHitResult(entity, hit.get());
			}
		}
		return null;
	}

	private static boolean isInBlockingArea(Player player, Entity source) {
		Direction playerFront = player.getDirection();
		Direction attackerFacing = Direction.getNearest(source.getX() - player.getX(), 0, source.getZ() - player.getZ());
		Direction leftSide = playerFront.getCounterClockWise();
		Direction rightSide = playerFront.getClockWise();
		return attackerFacing == playerFront || attackerFacing == leftSide || attackerFacing == rightSide;
	}

	public static boolean isBlocking(Player player) {
		return player.isUsingItem() && player.getUseItem().getUseAnimation() == UseAnim.BLOCK;
	}

	private static boolean canDisableBlocking(ItemStack attackerStack, ItemStack blockingStack, Player player, LivingEntity attacker) {
		return !attackerStack.isEmpty() && attackerStack.getItem().canDisableShield(attackerStack, blockingStack, player, attacker);
	}

	private static SoundEvent getBlockSound(ItemStack itemStack) {
		return SoundEvents.SHIELD_BLOCK;
	}

	private static void disableBlockingWeapon(Player player, ItemStack itemStack) {
		if (!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
			player.getCooldowns().addCooldown(itemStack.getItem(), 100);
		}
		player.stopUsingItem();
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, player.getSoundSource(), 1.0F, 1.0F);
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BREAK, player.getSoundSource(), 1.0F, 1.0F);
		clearAllDeflectData(itemStack);
	}
}
