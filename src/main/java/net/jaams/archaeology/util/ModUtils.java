package net.jaams.archaeology.util;

import org.checkerframework.checker.units.qual.m;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.common.ToolActions;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ResourceLocationException;
import net.minecraft.ChatFormatting;

import net.jaams.archaeology.packet.AberrationPacket;
import net.jaams.archaeology.dyeable.IDyeableItem;
import net.jaams.archaeology.configuration.client.TooltipsConfig;
import net.jaams.archaeology.capability.amount.AmountProvider;
import net.jaams.archaeology.capability.aberration.AberrationProvider;
import net.jaams.archaeology.JaamsArchaeologyMod;

import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.function.IntSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.BooleanSupplier;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

public class ModUtils {
	public static final Logger LOGGER = LogManager.getLogger();

	public static double roundToTwoDecimals(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

	public static boolean isBlockableItem(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		if (stack.hasTag() && stack.getTag().contains("Blockable")) {
			return stack.getTag().getBoolean("Blockable");
		}
		if (stack.getUseAnimation() == UseAnim.BLOCK) {
			return true;
		}
		Item item = stack.getItem();
		if (item instanceof ShieldItem || item.canPerformAction(stack, ToolActions.SHIELD_BLOCK)) {
			return true;
		}
		return false;
	}

	public static void applyBonusDamage(LivingEntity attacker, LivingEntity target, ItemStack itemStack, float damageBonus) {
		if (attacker == null || target == null || itemStack == null || !target.isAlive()) {
			return;
		}
		try {
			DamageSource damageSource = attacker.damageSources().generic();
			attacker.getCapability(AmountProvider.AMOUNT).ifPresent(capability -> {
				float baseDamage = (float) capability.getDamage();
				float totalDamage = baseDamage + damageBonus;
				if (totalDamage > 0) {
					target.hurt(damageSource, totalDamage);
				}
			});
		} catch (Exception e) {
			JaamsArchaeologyMod.LOGGER.error("Error applying bonus damage to {}: {}", target, e.getMessage());
		}
	}

	public static void applyDyeColor(ItemStack itemStack, int color) {
		if (itemStack.isEmpty()) {
			return;
		}
		Item item = itemStack.getItem();
		if (item instanceof IDyeableItem dyeableItem) {
			dyeableItem.setColor(itemStack, color);
			return;
		}
		CompoundTag tag = itemStack.getOrCreateTag();
		tag.putInt("color", color);
		if (tag.contains("display", Tag.TAG_COMPOUND)) {
			tag.getCompound("display").putInt("color", color);
		}
	}

	public static void removeColor(ItemStack itemStack) {
		CompoundTag tag = itemStack.getOrCreateTag();
		tag.remove("color");
		if (tag.contains("display", Tag.TAG_COMPOUND)) {
			CompoundTag display = tag.getCompound("display");
			display.remove("color");
			if (display.isEmpty()) {
				tag.remove("display");
			}
		}
		tag.remove("ImbuedColor");
	}

	public static int getCurrentColor(ItemStack stack) {
		if (stack.isEmpty()) {
			return -1;
		}
		Item item = stack.getItem();
		if (item instanceof IDyeableItem dyeableItem) {
			return dyeableItem.getColor(stack);
		}
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return -1;
		}
		if (tag.contains("color", Tag.TAG_INT)) {
			return tag.getInt("color");
		}
		if (tag.contains("display", Tag.TAG_COMPOUND)) {
			CompoundTag display = tag.getCompound("display");
			if (display.contains("color", Tag.TAG_INT)) {
				return display.getInt("color");
			}
		}
		return -1;
	}

	public static String getConfigOrNbtString(ItemStack stack, String nbtKey, Supplier<String> configValue) {
		if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_STRING)) {
			return stack.getTag().getString(nbtKey);
		}
		return configValue.get();
	}

	public static double getConfigOrNbtDouble(ItemStack stack, String nbtKey, DoubleSupplier configValue) {
		if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_DOUBLE)) {
			return stack.getTag().getDouble(nbtKey);
		}
		return configValue.getAsDouble();
	}

	public static float getConfigOrNbtFloat(ItemStack stack, String nbtKey, DoubleSupplier configValue) {
		if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_FLOAT)) {
			return stack.getTag().getFloat(nbtKey);
		}
		return (float) configValue.getAsDouble();
	}

	public static int getConfigOrNbtInt(ItemStack stack, String nbtKey, IntSupplier configValue) {
		if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_INT)) {
			return stack.getTag().getInt(nbtKey);
		}
		return configValue.getAsInt();
	}

	public static boolean getConfigOrNbtBoolean(ItemStack stack, String nbtKey, BooleanSupplier configValue) {
		if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_BYTE)) {
			return stack.getTag().getBoolean(nbtKey);
		}
		return configValue.getAsBoolean();
	}

	public static <T extends Enum<T>> T getConfigOrNbtEnum(ItemStack stack, String nbtKey, Supplier<T> configValue) {
		if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_STRING)) {
			String enumName = stack.getTag().getString(nbtKey);
			try {
				return Enum.valueOf(configValue.get().getDeclaringClass(), enumName);
			} catch (IllegalArgumentException e) {
			}
		}
		return configValue.get();
	}

	public static double calculateDamage(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return 1.0;
		}
		double base = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream().filter(m -> m.getOperation() == AttributeModifier.Operation.ADDITION).mapToDouble(AttributeModifier::getAmount).sum();
		if (base <= 0.0) {
			base = 1.0;
		}
		base += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
		return base + 1.0;
	}

	public static boolean canAddTooltip(ItemStack stack, boolean requireCtrl, boolean requireAlt, List<? extends String> extraExclusions) {
		if (!TooltipsConfig.TOOLTIPS.get())
			return false;
		if (isItemExcluded(stack, TooltipsConfig.EXCLUDED_TOOLTIPS_ITEMS.get()))
			return false;
		if (requireCtrl && !TooltipsConfig.CONTROL_TOOLTIPS.get())
			return false;
		if (requireAlt && !TooltipsConfig.ALT_TOOLTIPS.get())
			return false;
		if (extraExclusions != null && !extraExclusions.isEmpty() && isItemExcluded(stack, extraExclusions)) {
			return false;
		}
		return true;
	}

	public static void addTrait(ItemStack stack, List<Component> tooltip, String traitKey, String descKey) {
		if (!canAddTooltip(stack, false, false, null))
			return;
		tooltip.add(Component.translatable(traitKey).withStyle(ChatFormatting.GOLD));
		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
		}
	}

	public static void addNegativeTrait(ItemStack stack, List<Component> tooltip, String traitKey, String descKey) {
		if (!canAddTooltip(stack, false, false, null))
			return;
		tooltip.add(Component.translatable(traitKey).withStyle(ChatFormatting.RED));
		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
		}
	}

	public static void addProjectileTrait(ItemStack stack, List<Component> tooltip, String traitKey, String descKey) {
		if (!canAddTooltip(stack, false, false, null))
			return;
		tooltip.add(Component.translatable(traitKey).withStyle(ChatFormatting.YELLOW));
		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
		}
	}

	public static void addPropertyTrait(ItemStack stack, List<Component> tooltip, TooltipFlag flag, String propertyKey, String descKey) {
		if (!canAddTooltip(stack, false, false, null))
			return;
		if (!flag.isAdvanced())
			return;
		tooltip.add(Component.translatable(propertyKey).withStyle(ChatFormatting.AQUA));
		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
		}
	}

	public static void addExtraInfo(ItemStack stack, List<Component> tooltip, String key, ChatFormatting... styles) {
		if (!canAddTooltip(stack, true, false, TooltipsConfig.EXCLUDED_CONTROL_TOOLTIPS_ITEMS.get()))
			return;
		tooltip.add(Component.translatable(key).withStyle(styles));
	}

	public static void addStat(ItemStack stack, List<Component> tooltip, String key, double value) {
		if (!canAddTooltip(stack, true, false, TooltipsConfig.EXCLUDED_CONTROL_TOOLTIPS_ITEMS.get()))
			return;
		tooltip.add(Component.translatable("tooltip.jaams_archaeology.properties." + key, value).withStyle(ChatFormatting.GRAY));
	}

	public static void addLongDescription(ItemStack stack, List<Component> tooltip, String key, ChatFormatting... styles) {
		if (!canAddTooltip(stack, false, true, TooltipsConfig.EXCLUDED_ALT_TOOLTIPS_ITEMS.get()))
			return;
		tooltip.add(Component.translatable(key).withStyle(styles));
	}

	public static void addLongDescriptionComponent(ItemStack stack, List<Component> tooltip, Component component) {
		if (!canAddTooltip(stack, false, true, TooltipsConfig.EXCLUDED_ALT_TOOLTIPS_ITEMS.get()))
			return;
		tooltip.add(component);
	}

	public static boolean isItemExcluded(ItemStack stack, List<? extends String> excludedList) {
		if (stack == null || stack.isEmpty() || excludedList == null || excludedList.isEmpty()) {
			return false;
		}
		ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
		if (itemKey == null) {
			return false;
		}
		Set<String> excludedSet = new HashSet<>(excludedList);
		return ModUtils.matchesList(excludedSet, itemKey, stack.getItem(), false);
	}

	public static boolean hasDurability(ItemStack stack) {
		return stack.getMaxDamage() > 1;
	}

	public static boolean isIdolItem(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains("IdolItem", Tag.TAG_BYTE)) {
			return tag.getBoolean("IdolItem");
		}
		return stack.is(ModTags.IDOLS);
	}

	public static boolean isItemWeapon(ItemStack itemStack) {
		return itemStack.getItem().getDefaultAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
	}

	public static boolean hasRestrictedEffect(LivingEntity entity) {
		return entity.hasEffect(BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation("jaams_weaponry", "knocked_up"))) || entity.hasEffect(BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation("jaams_weaponry", "incapable")));
	}

	public static boolean isProjectileCritical(Player player, float power) {
		boolean attackPowerFull = power >= 1.0F;
		boolean inAir = !player.onGround();
		boolean hasFallen = player.fallDistance > 0.0F;
		boolean notClimbing = !player.onClimbable();
		boolean notWet = !player.isInWater();
		boolean notBlind = !player.hasEffect(MobEffects.BLINDNESS);
		boolean notRiding = !player.isPassenger();
		boolean notSprinting = !player.isSprinting();
		return attackPowerFull && inAir && hasFallen && notClimbing && notWet && notBlind && notRiding && notSprinting;
	}

	public static float getPowerForTime(int durationRemaining, int minChargeTicks, int maxChargeTicks) {
		if (durationRemaining <= minChargeTicks) {
			return 0.0f;
		}
		float effectiveDuration = durationRemaining - minChargeTicks;
		float maxEffective = maxChargeTicks - minChargeTicks;
		if (maxEffective <= 0) {
			return 1.0f;
		}
		float f = effectiveDuration / maxEffective;
		f = (f * f + f * 2.0f) / 3.0f;
		return Math.min(f, 1.0f);
	}

	public static float[] generateShotPitches(RandomSource random, int projectileCount) {
		float[] pitches = new float[projectileCount];
		for (int i = 0; i < projectileCount; i++) {
			boolean flag = random.nextBoolean();
			pitches[i] = 1.0F / (random.nextFloat() * 0.5F + 1.8F) + (flag ? 0.63F : 0.43F);
		}
		return pitches;
	}

	public static boolean isProjectileBreakableBlock(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.POINTED_DRIPSTONE || block == Blocks.SCAFFOLDING;
	}

	public static boolean isInFrontArea(LivingEntity entity, Entity source) {
		Direction entityFront = entity.getDirection();
		Direction attackerFacing = Direction.getNearest(source.getX() - entity.getX(), 0, source.getZ() - entity.getZ());
		Direction leftSide = entityFront.getCounterClockWise();
		Direction rightSide = entityFront.getClockWise();
		return attackerFacing == entityFront || attackerFacing == leftSide || attackerFacing == rightSide;
	}

	public static void playSound(Entity entity, String soundEvent) {
		if (!(entity.level() instanceof ServerLevel level))
			return;
		BlockPos pos = entity.blockPosition();
		SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
		if (sound != null) {
			level.playSound(null, pos, sound, SoundSource.PLAYERS, 1.0f, 1.0f);
		}
	}

	public static void playSound(Entity entity, String soundEvent, SoundSource source) {
		if (!(entity.level() instanceof ServerLevel level))
			return;
		BlockPos pos = entity.blockPosition();
		SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
		if (sound != null) {
			level.playSound(null, pos, sound, source, 1.0f, 1.0f);
		}
	}

	public static void playSound(Entity entity, String soundEvent, SoundSource source, float volume, float pitch) {
		if (!(entity.level() instanceof ServerLevel level))
			return;
		BlockPos pos = entity.blockPosition();
		SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
		if (sound != null) {
			level.playSound(null, pos, sound, source, volume, pitch);
		}
	}

	public static void playClientSound(Entity entity, String soundEvent) {
		if (!(entity.level() instanceof ClientLevel level))
			return;
		BlockPos pos = entity.blockPosition();
		SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
		if (sound != null) {
			level.playLocalSound(pos, sound, SoundSource.PLAYERS, 1.0f, 1.0f, false);
		}
	}

	public static ItemStack getItemInMainHand(LivingEntity entity, Item item) {
		ItemStack mainHand = entity.getMainHandItem();
		return mainHand.is(item) ? mainHand : ItemStack.EMPTY;
	}

	public static ItemStack getItemInEitherHand(LivingEntity entity, Item item) {
		ItemStack mainHand = entity.getMainHandItem();
		ItemStack offHand = entity.getOffhandItem();
		return mainHand.is(item) ? mainHand : offHand.is(item) ? offHand : ItemStack.EMPTY;
	}

	public static ItemStack getItemInEitherHand(LivingEntity entity, Predicate<ItemStack> predicate) {
		ItemStack mainHand = entity.getMainHandItem();
		if (!mainHand.isEmpty() && predicate.test(mainHand)) {
			return mainHand;
		}
		ItemStack offHand = entity.getOffhandItem();
		if (!offHand.isEmpty() && predicate.test(offHand)) {
			return offHand;
		}
		return ItemStack.EMPTY;
	}

	public static Vec3 getDirectionToTarget(LivingEntity entity, LivingEntity target) {
		return new Vec3(target.getX() - entity.getX(), target.getEyeY() - entity.getEyeY(), target.getZ() - entity.getZ()).normalize();
	}

	public static boolean canThrowAtTarget(LivingEntity entity, LivingEntity target, double minDistance, double maxDistance) {
		double distance = target.distanceTo(entity);
		Vec3 direction = getDirectionToTarget(entity, target);
		Vec3 entityLook = entity.getLookAngle().normalize();
		double angle = direction.dot(entityLook);
		return distance <= maxDistance && distance >= minDistance && angle > 0.5;
	}

	public static boolean hasEpicFightAttribute(LivingEntity entity) {
		if (entity == null) {
			return false;
		}
		if (entity.getType() == EntityType.PLAYER) {
			return false;
		}
		ResourceLocation staminaAttributeId = new ResourceLocation("epicfight", "weight");
		Attribute staminaAttribute = ForgeRegistries.ATTRIBUTES.getValue(staminaAttributeId);
		if (staminaAttribute == null) {
			return false;
		}
		return entity.getAttributes().hasAttribute(staminaAttribute);
	}

	public static boolean isEntityInBattleMode(Entity entity) {
		if (entity == null) {
			return false;
		}
		CompoundTag entityData = new CompoundTag();
		entity.saveWithoutId(entityData);
		CompoundTag forgeCaps = entityData.getCompound("ForgeCaps");
		if (forgeCaps.contains("epicfight:skill_cap")) {
			CompoundTag skillCap = forgeCaps.getCompound("epicfight:skill_cap");
			String playerMode = skillCap.getString("playerMode");
			return "EPICFIGHT".equals(playerMode) || "BATTLE".equals(playerMode);
		}
		return false;
	}

	public static boolean isAlliedEntity(Player player, Entity entity) {
		if (!(entity instanceof LivingEntity)) {
			return false;
		}
		if (entity instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == player) {
			return true;
		}
		if (entity instanceof Player otherPlayer && !player.canHarmPlayer(otherPlayer)) {
			return true;
		}
		if (entity == player.getVehicle()) {
			return true;
		}
		return false;
	}

	public static void applyShakeEffect(Entity entity, double shakeAmount, int shakeResetDelay) {
		if (entity == null || !(entity instanceof ServerPlayer player) || player.level().isClientSide) {
			return;
		}
		player.getCapability(AberrationProvider.ABERRATION).ifPresent(aberration -> {
			aberration.setEffectType(ModEnums.AberrationType.SHAKE);
			aberration.setIntensity(shakeAmount);
			aberration.setDuration(shakeResetDelay);
			JaamsArchaeologyMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AberrationPacket(player.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration()));
		});
	}

	public static boolean isBlocked(LivingEntity defender, Entity attacker) {
		if (defender.isBlocking() && attacker != null) {
			Vec3 attackerPos = attacker.position();
			Vec3 defenderLook = defender.getLookAngle();
			Vec3 directionToAttacker = attackerPos.subtract(defender.position()).normalize();
			Vec3 horizontalDirection = new Vec3(directionToAttacker.x, 0.0D, directionToAttacker.z).normalize();
			return horizontalDirection.dot(defenderLook) < 0.0D;
		}
		return false;
	}

	public static boolean isCritical(Player player, Entity target, float chargeStrength) {
		boolean attackPowerFull = chargeStrength > 0.9f;
		boolean hasFallen = player.fallDistance > 0.0F;
		boolean inAir = !player.onGround();
		boolean notClimbing = !player.onClimbable();
		boolean notWet = !player.isInWater();
		boolean notBlind = !player.hasEffect(MobEffects.BLINDNESS);
		boolean notRiding = !player.isPassenger();
		boolean targetValid = target instanceof LivingEntity;
		boolean walking = !player.isSprinting();
		return attackPowerFull && hasFallen && inAir && notClimbing && notWet && notBlind && notRiding && targetValid && walking;
	}

	public static boolean isLookingAtEntity(LivingEntity viewer, Entity target, double maxAngle) {
		Vec3 viewerLook = viewer.getLookAngle();
		Vec3 directionToTarget = target.position().subtract(viewer.position()).normalize();
		return viewerLook.dot(directionToTarget) > Math.cos(Math.toRadians(maxAngle));
	}

	public static boolean isAttackFromBehind(LivingEntity defender, Entity attacker) {
		Vec3 defenderLook = defender.getLookAngle();
		Vec3 directionToAttacker = attacker.position().subtract(defender.position()).normalize();
		return defenderLook.dot(directionToAttacker) < -0.5;
	}

	public static boolean isHeadshot(Entity attacker, Entity target) {
		double headHeight = target.getBoundingBox().maxY - target.getBoundingBox().getYsize() * 0.25;
		return attacker.getEyePosition().y > headHeight;
	}

	public static boolean isBodyshot(Entity attacker, Entity target) {
		double minBodyHeight = target.getBoundingBox().minY + target.getBoundingBox().getYsize() * 0.25;
		double maxBodyHeight = target.getBoundingBox().maxY - target.getBoundingBox().getYsize() * 0.25;
		double attackerEyeHeight = attacker.getEyePosition().y;
		return attackerEyeHeight >= minBodyHeight && attackerEyeHeight <= maxBodyHeight;
	}

	public static void applyRecoil(LivingEntity entity, float recoilDistance, float crouchRecoilReduction, float verticalRecoilMultiplier) {
		if (entity == null || recoilDistance <= 0.0F) {
			return;
		}
		double recoilMultiplier = recoilDistance;
		if (entity instanceof Player player && player.isCrouching() && !player.isCreative()) {
			recoilMultiplier *= crouchRecoilReduction;
			player.getFoodData().addExhaustion(0.2F);
		} else if (entity instanceof Player player) {
			player.getFoodData().addExhaustion(0.5F);
		}
		double knockbackResistance = Math.max(0.0, Math.min(1.0, entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
		double knockbackReduction = 1.0 - Math.pow(1.0 - knockbackResistance, 4.0);
		recoilMultiplier *= (1.0 - knockbackReduction);
		double movementSpeed = entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
		double baseMovementSpeed = 0.1;
		double movementSpeedMultiplier = Math.max(0.1, movementSpeed / baseMovementSpeed);
		recoilMultiplier *= movementSpeedMultiplier;
		double horizontalRecoil = 1.2 * recoilMultiplier;
		double verticalRecoil = 1.2 * verticalRecoilMultiplier * recoilMultiplier;
		horizontalRecoil = Math.max(0.0, horizontalRecoil);
		verticalRecoil = Math.max(0.0, verticalRecoil);
		if (horizontalRecoil < 0.01 && verticalRecoil < 0.01) {
			return;
		}
		float yaw = entity.getYHeadRot() * ((float) Math.PI / 180F);
		float pitch = (entity instanceof Player player) ? player.getXRot() * ((float) Math.PI / 180F) : 0.0F; // Default pitch for mobs
		double motionX = Math.sin(yaw) * horizontalRecoil * Math.cos(pitch);
		double motionZ = -Math.cos(yaw) * horizontalRecoil * Math.cos(pitch);
		double motionY = 0.0;
		final float MIN_PITCH_FOR_VERTICAL_DEGREES = 45.0F;
		float minPitchForVertical = (float) Math.toRadians(MIN_PITCH_FOR_VERTICAL_DEGREES);
		double horizontalScale = 1.0;
		if (pitch > minPitchForVertical) {
			float pitchFactor = (pitch - minPitchForVertical) / ((float) Math.PI / 2 - minPitchForVertical);
			pitchFactor = Math.min(1.0f, pitchFactor);
			double verticalScale = Math.cos(pitchFactor * Math.PI / 2);
			motionY = verticalRecoil * (1.0 - verticalScale);
			horizontalScale = Math.max(0.5, 1.0 - pitchFactor * 0.5);
			motionX *= horizontalScale;
			motionZ *= horizontalScale;
		}
		motionX = Mth.clamp(motionX, -1.5, 1.5);
		motionY = Mth.clamp(motionY, -1.5 * verticalRecoilMultiplier, 1.5 * verticalRecoilMultiplier);
		motionZ = Mth.clamp(motionZ, -1.5, 1.5);
		entity.setDeltaMovement(entity.getDeltaMovement().add(motionX, motionY, motionZ));
		entity.hurtMarked = true;
		if (entity instanceof ServerPlayer serverPlayer) {
			serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
		} else if (!entity.level().isClientSide) {
			entity.level().getEntities(null, entity.getBoundingBox().inflate(10.0)).stream().filter(e -> e instanceof ServerPlayer).forEach(e -> ((ServerPlayer) e).connection.send(new ClientboundSetEntityMotionPacket(entity)));
		}
	}

	public static boolean matchesList(Set<String> set, ResourceLocation itemKey, Item item, boolean isWhitelist) {
		if (set == null || set.isEmpty()) {
			return false;
		}
		for (String entry : set) {
			entry = entry.trim();
			if (entry.isEmpty()) {
				continue;
			}
			if (entry.equals("all")) {
				if (ForgeRegistries.ITEMS.containsKey(itemKey)) {
					return true;
				}
				continue;
			}
			if (entry.startsWith("#")) {
				try {
					String tagName = entry.substring(1);
					TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation(tagName));
					if (item.builtInRegistryHolder().is(tag)) {
						return true;
					}
				} catch (ResourceLocationException e) {
					LOGGER.warn("Invalid tag entry in list (ignoring): '{}'", entry);
				}
				continue;
			}
			if (entry.equals("bows:*")) {
				if (item instanceof BowItem) {
					return true;
				}
			} else if (entry.equals("crossbows:*")) {
				if (item instanceof CrossbowItem) {
					return true;
				}
			} else if (entry.equals("swords:*")) {
				if (item instanceof SwordItem) {
					return true;
				}
			} else if (entry.equals("pickaxes:*")) {
				if (item instanceof PickaxeItem) {
					return true;
				}
			} else if (entry.equals("axes:*")) {
				if (item instanceof AxeItem) {
					return true;
				}
			} else if (entry.equals("blocks:*")) {
				if (item instanceof BlockItem) {
					return true;
				}
			} else if (entry.equals("edibles:*")) {
				if (item.getFoodProperties() != null) {
					return true;
				}
			} else if (entry.equals("shovels:*")) {
				if (item instanceof ShovelItem) {
					return true;
				}
			} else if (entry.equals("hoes:*")) {
				if (item instanceof HoeItem) {
					return true;
				}
			} else if (entry.equals("armor:*")) {
				if (item instanceof ArmorItem) {
					return true;
				}
			} else if (entry.contains(":*")) {
				String[] parts = entry.split(":");
				if (parts.length == 2 && parts[1].equals("*")) {
					String namespace = parts[0];
					if (itemKey.getNamespace().equals(namespace)) {
						return true;
					}
				}
			} else if (entry.contains("*")) {
				String[] parts = entry.split(":");
				if (parts.length == 2) {
					String namespace = parts[0];
					String pattern = parts[1].replace("*", "");
					if (itemKey.getNamespace().equals(namespace) && itemKey.getPath().contains(pattern)) {
						return true;
					}
				} else if (parts.length == 1) {
					String pattern = parts[0].replace("*", "");
					if (itemKey.getPath().contains(pattern)) {
						return true;
					}
				} else {
					LOGGER.warn("Invalid wildcard pattern (ignoring): '{}'. Expected format: 'namespace:pattern' or 'pattern'", entry);
				}
			} else {
				try {
					ResourceLocation resourceLocation = new ResourceLocation(entry);
					if (ForgeRegistries.ITEMS.containsKey(resourceLocation) && itemKey.equals(resourceLocation)) {
						return true;
					}
				} catch (ResourceLocationException e) {
					LOGGER.warn("Invalid entry in list (ignoring): '{}'", entry);
				}
			}
		}
		return false;
	}
}
