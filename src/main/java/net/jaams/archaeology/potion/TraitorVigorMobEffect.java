
package net.jaams.archaeology.potion;

import org.joml.Vector3f;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

public class TraitorVigorMobEffect extends MobEffect {
	private static final int PARTICLE_COUNT = 40;
	private static final double PARTICLE_RADIUS = 0.5d;
	private static final double PARTICLE_SPEED = 0.02d;

	public TraitorVigorMobEffect() {
		super(MobEffectCategory.NEUTRAL, -3407872);
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		if (entity != null) {
			spawnColoredParticles(entity, 0.5f, 0.1f, 0.1f);
			ModUtils.playSound(entity, "jaams_archaeology:traitor_vigor_started");
		}
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		if (entity != null && !entity.hasEffect(JaamsArchaeologyModMobEffects.TRAITOR_VIGOR.get())) {
			spawnColoredParticles(entity, 0.3f, 0.05f, 0.05f);
			ModUtils.playSound(entity, "jaams_archaeology:traitor_vigor_expires");
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	private void spawnColoredParticles(LivingEntity entity, float red, float green, float blue) {
		if (entity.level() instanceof ServerLevel serverWorld) {
			serverWorld.sendParticles(new DustParticleOptions(new Vector3f(red, green, blue), 1.5f), entity.getX(), entity.getY() + 1, entity.getZ(), PARTICLE_COUNT, PARTICLE_RADIUS, PARTICLE_RADIUS, PARTICLE_RADIUS, PARTICLE_SPEED);
		}
	}
}
