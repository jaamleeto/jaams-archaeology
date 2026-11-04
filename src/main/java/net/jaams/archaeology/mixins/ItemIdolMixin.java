package net.jaams.archaeology.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;

import net.jaams.archaeology.util.ModUtils;

import java.util.List;
import java.util.ArrayList;

@Mixin(ItemStack.class)
public abstract class ItemIdolMixin {
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void onIdolUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (!ModUtils.isIdolItem(stack)) {
			return;
		}
		if (player.getCooldowns().isOnCooldown(stack.getItem())) {
			cir.setReturnValue(InteractionResultHolder.pass(stack));
			return;
		}
		List<MobEffectInstance> effects = getAllIdolEffects(stack);
		if (effects.isEmpty()) {
			MobEffect singleEffect = determineEffect(stack);
			if (singleEffect == null) {
				return;
			}
			int duration = ModUtils.getConfigOrNbtInt(stack, "IdolEffectDuration", () -> 200);
			int amplifier = ModUtils.getConfigOrNbtInt(stack, "IdolEffectAmplifier", () -> 1);
			effects = List.of(new MobEffectInstance(singleEffect, duration, amplifier, false, true, true));
		}
		int cooldown = ModUtils.getConfigOrNbtInt(stack, "IdolCooldown", () -> 120);
		boolean particles = ModUtils.getConfigOrNbtBoolean(stack, "IdolShowParticles", () -> true);
		int particleCount = ModUtils.getConfigOrNbtInt(stack, "IdolParticleCount", () -> 8);
		boolean animation = ModUtils.getConfigOrNbtBoolean(stack, "IdolDoAnimation", () -> true);
		boolean shake = ModUtils.getConfigOrNbtBoolean(stack, "IdolDoShake", () -> true);
		executeIdolActivation(level, player, stack, effects, cooldown, particles, particleCount, animation, shake, hand);
		cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, level.isClientSide()));
	}

	@Unique
	private static List<MobEffectInstance> getAllIdolEffects(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return List.of();
		}
		if (tag.contains("ImbuedEffects", Tag.TAG_LIST)) {
			ListTag list = tag.getList("ImbuedEffects", Tag.TAG_COMPOUND);
			if (!list.isEmpty()) {
				return parseEffectsList(list);
			}
		}
		if (tag.contains("IdolEffects", Tag.TAG_LIST)) {
			ListTag list = tag.getList("IdolEffects", Tag.TAG_COMPOUND);
			if (!list.isEmpty()) {
				return parseEffectsList(list);
			}
		}
		return List.of();
	}

	@Unique
	private static List<MobEffectInstance> parseEffectsList(ListTag list) {
		List<MobEffectInstance> result = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entry = list.getCompound(i);
			String effectIdStr = null;
			if (entry.contains("effect")) {
				effectIdStr = entry.getString("effect");
			} else if (entry.contains("id")) {
				effectIdStr = entry.getString("id");
			}
			if (effectIdStr == null || effectIdStr.isEmpty()) {
				continue;
			}
			ResourceLocation loc;
			try {
				loc = new ResourceLocation(effectIdStr);
			} catch (Exception e) {
				continue;
			}
			MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(loc);
			if (effect == null) {
				continue;
			}
			int duration = entry.getInt("duration");
			if (duration <= 0)
				duration = 200;
			int amplifier = entry.getInt("amplifier");
			if (amplifier < 0)
				amplifier = 0;
			boolean showParticles = true;
			if (entry.contains("show_particles")) {
				showParticles = entry.getBoolean("show_particles");
			} else if (entry.contains("ambient")) {
				showParticles = !entry.getBoolean("ambient");
			}
			result.add(new MobEffectInstance(effect, duration, amplifier, false, showParticles, true));
		}
		return result;
	}

	@Unique
	private static MobEffect determineEffect(ItemStack stack) {
		String effectIdStr = ModUtils.getConfigOrNbtString(stack, "IdolEffect", () -> null);
		if (effectIdStr != null && !effectIdStr.trim().isEmpty()) {
			try {
				ResourceLocation loc = new ResourceLocation(effectIdStr);
				MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(loc);
				if (effect != null)
					return effect;
			} catch (Exception ignored) {
			}
		}
		ResourceLocation itemKey = stack.getItem().builtInRegistryHolder().key().location();
		if (itemKey != null) {
			String path = itemKey.getPath();
			if (path.endsWith("_idol")) {
				String effectPath = path.substring(0, path.length() - 5) + "_vigor";
				ResourceLocation effectLoc = new ResourceLocation(itemKey.getNamespace(), effectPath);
				MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectLoc);
				if (effect != null)
					return effect;
			}
		}
		Item item = stack.getItem();
		if (ModUtils.isItemWeapon(stack))
			return MobEffects.DAMAGE_BOOST;
		if (item.isEdible())
			return MobEffects.SATURATION;
		if (item instanceof DiggerItem || item instanceof HoeItem)
			return MobEffects.DIG_SPEED;
		if (item instanceof ArmorItem)
			return MobEffects.DAMAGE_RESISTANCE;
		if (item instanceof ProjectileWeaponItem)
			return MobEffects.MOVEMENT_SPEED;
		return MobEffects.REGENERATION;
	}

	@Unique
	private static void executeIdolActivation(Level level, Player player, ItemStack stack, List<MobEffectInstance> effects, int cooldown, boolean showParticles, int particleCount, boolean doAnimation, boolean doShake, InteractionHand hand) {
		boolean isClient = level.isClientSide();
		boolean isCreative = player.getAbilities().instabuild;
		if (!isClient) {
			ServerLevel serverLevel = (ServerLevel) level;
			if (showParticles && particleCount > 0 && !isCreative) {
				double px = player.getX() + player.getLookAngle().x * 0.5;
				double py = player.getEyeY();
				double pz = player.getZ() + player.getLookAngle().z * 0.5;
				serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), px, py, pz, particleCount, 0.1, 0.1, 0.1, 0.05);
			}
			playSound(serverLevel, player.getX(), player.getY(), player.getZ(), "item.totem.use");
			playSound(serverLevel, player.getX(), player.getY(), player.getZ(), "jaams_archaeology:idol_breaks");
			for (MobEffectInstance effect : effects) {
				player.addEffect(effect);
			}
		}
		if (isClient && doAnimation) {
			Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
		}
		applyIdolCooldown(player, cooldown);
		if (!isCreative) {
			stack.shrink(1);
		}
		if (doShake) {
			ModUtils.applyShakeEffect(player, 1.0f, 3);
		}
		player.swing(hand, true);
	}

	@Unique
	private static void applyIdolCooldown(Player player, int cooldown) {
		for (ItemStack invStack : player.getInventory().items) {
			if (!invStack.isEmpty() && ModUtils.isIdolItem(invStack)) {
				player.getCooldowns().addCooldown(invStack.getItem(), cooldown);
			}
		}
		for (ItemStack invStack : player.getInventory().offhand) {
			if (!invStack.isEmpty() && ModUtils.isIdolItem(invStack)) {
				player.getCooldowns().addCooldown(invStack.getItem(), cooldown);
			}
		}
	}

	@Unique
	private static void playSound(Level level, double x, double y, double z, String soundEvent) {
		BlockPos pos = BlockPos.containing(x, y, z);
		level.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent)), SoundSource.PLAYERS, 1, 1);
	}
}
