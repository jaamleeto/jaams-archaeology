
package net.jaams.archaeology.potion;

import org.joml.Vector3f;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;

import java.util.UUID;

public class RottenVigorMobEffect extends MobEffect {
	private static final int PARTICLE_COUNT = 40;
	private static final double PARTICLE_RADIUS = 0.5d;
	private static final double PARTICLE_SPEED = 0.02d;
	private static final UUID ROTTEN_VIGOR_ATTACK_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

	public RottenVigorMobEffect() {
		super(MobEffectCategory.NEUTRAL, -16751053);
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		if (entity != null) {
			AttributeInstance attackAttribute = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
			if (attackAttribute != null) {
				float attackIncrease = 2.0f + (0.5f * amplifier);
				attackAttribute.addTransientModifier(new AttributeModifier(ROTTEN_VIGOR_ATTACK_UUID, "rotten_vigor_attack", attackIncrease, AttributeModifier.Operation.ADDITION));
			}
			spawnColoredParticles(entity, 0.3f, 0.5f, 0.1f);
			ModUtils.playSound(entity, "jaams_archaeology:rotten_vigor_started");
		}
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		AttributeInstance attackAttribute = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
		if (attackAttribute != null) {
			attackAttribute.removeModifier(ROTTEN_VIGOR_ATTACK_UUID);
		}
		if (entity != null && !entity.hasEffect(JaamsArchaeologyModMobEffects.ROTTEN_VIGOR.get())) {
			spawnColoredParticles(entity, 0.1f, 0.2f, 0.05f);
			ModUtils.playSound(entity, "jaams_archaeology:rotten_vigor_expires");
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
