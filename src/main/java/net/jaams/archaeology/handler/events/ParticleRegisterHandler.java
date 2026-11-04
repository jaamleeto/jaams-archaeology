package net.jaams.archaeology.handler.events;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.jaams.archaeology.registry.CustomParticles;
import net.jaams.archaeology.particle.MiniSweepParticle;
import net.jaams.archaeology.particle.EnhancedHitParticle;
import net.jaams.archaeology.particle.CustomSweepParticle;
import net.jaams.archaeology.particle.CustomGlintParticle;
import net.jaams.archaeology.particle.CustomCritParticle;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleRegisterHandler {
	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(CustomParticles.ENHANCED_HIT_PARTICLE.get(), EnhancedHitParticle.EnhancedHitParticleProvider::new);
		event.registerSpriteSet(CustomParticles.MINI_SWEEP_PARTICLE.get(), MiniSweepParticle.MiniSweepParticleProvider::new);
		event.registerSpriteSet(CustomParticles.CUSTOM_SWEEP_PARTICLE.get(), CustomSweepParticle.CustomSweepParticleProvider::new);
		event.registerSpriteSet(CustomParticles.CUSTOM_GLINT_PARTICLE.get(), CustomGlintParticle.CustomGlintParticleProvider::new);
		event.registerSpriteSet(CustomParticles.CUSTOM_CRIT_PARTICLE.get(), CustomCritParticle.CustomCritParticleProvider::new);
	}
}
