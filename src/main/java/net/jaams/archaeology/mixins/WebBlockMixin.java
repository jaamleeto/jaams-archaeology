package net.jaams.archaeology.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.BlockPos;

import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

@Mixin(WebBlock.class)
public class WebBlockMixin {
	@Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
	private void reduceSlowdownForArthropodsVigor(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
		if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get())) {
			MobEffectInstance effect = livingEntity.getEffect(JaamsArchaeologyModMobEffects.ARTHROPODS_VIGOR.get());
			if (effect != null) {
				int amplifier = effect.getAmplifier();
				if (amplifier >= 8) {
					ci.cancel();
					return;
				}
				float speedMultiplier = 0.2F * (amplifier + 1);
				Vec3 originalSlowdown = new Vec3(0.25D, 0.05F, 0.25D);
				Vec3 modifiedSlowdown = new Vec3(originalSlowdown.x + (1.0 - originalSlowdown.x) * speedMultiplier, originalSlowdown.y + (1.0 - originalSlowdown.y) * speedMultiplier, originalSlowdown.z + (1.0 - originalSlowdown.z) * speedMultiplier);
				entity.makeStuckInBlock(state, modifiedSlowdown);
				ci.cancel();
			}
		}
	}
}
