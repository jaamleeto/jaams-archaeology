package net.jaams.archaeology.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraft.core.particles.ParticleType;

import net.jaams.archaeology.particle.MiniSweepParticleData;
import net.jaams.archaeology.particle.EnhancedHitParticleData;
import net.jaams.archaeology.particle.CustomSweepParticleData;
import net.jaams.archaeology.particle.CustomGlintParticleData;
import net.jaams.archaeology.particle.CustomCritParticleData;

import com.mojang.serialization.Codec;

public class CustomParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "jaams_archaeology");
	public static final RegistryObject<ParticleType<CustomCritParticleData>> CUSTOM_CRIT_PARTICLE = PARTICLES.register("custom_crit", () -> new ParticleType<CustomCritParticleData>(false, CustomCritParticleData.DESERIALIZER) {
		@Override
		public Codec<CustomCritParticleData> codec() {
			return CustomCritParticleData.CODEC;
		}
	});
	public static final RegistryObject<ParticleType<CustomGlintParticleData>> CUSTOM_GLINT_PARTICLE = PARTICLES.register("custom_glint", () -> new ParticleType<CustomGlintParticleData>(false, CustomGlintParticleData.DESERIALIZER) {
		@Override
		public Codec<CustomGlintParticleData> codec() {
			return CustomGlintParticleData.CODEC;
		}
	});
	public static final RegistryObject<ParticleType<CustomSweepParticleData>> CUSTOM_SWEEP_PARTICLE = PARTICLES.register("custom_sweep", () -> new ParticleType<CustomSweepParticleData>(false, CustomSweepParticleData.DESERIALIZER) {
		@Override
		public Codec<CustomSweepParticleData> codec() {
			return CustomSweepParticleData.CODEC;
		}
	});
	public static final RegistryObject<ParticleType<MiniSweepParticleData>> MINI_SWEEP_PARTICLE = PARTICLES.register("mini_sweep", () -> new ParticleType<MiniSweepParticleData>(false, MiniSweepParticleData.DESERIALIZER) {
		@Override
		public Codec<MiniSweepParticleData> codec() {
			return MiniSweepParticleData.CODEC;
		}
	});
	public static final RegistryObject<ParticleType<EnhancedHitParticleData>> ENHANCED_HIT_PARTICLE = PARTICLES.register("enhanced_hit", () -> new ParticleType<EnhancedHitParticleData>(false, EnhancedHitParticleData.DESERIALIZER) {
		@Override
		public Codec<EnhancedHitParticleData> codec() {
			return EnhancedHitParticleData.CODEC;
		}
	});

	public static void register(IEventBus eventBus) {
		PARTICLES.register(eventBus);
	}
}
