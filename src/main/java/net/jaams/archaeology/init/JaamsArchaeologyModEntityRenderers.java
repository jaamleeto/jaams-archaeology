
package net.jaams.archaeology.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.jaams.archaeology.client.renderer.SpearFragmentProjectileRenderer;
import net.jaams.archaeology.client.renderer.PrimitiveSpearProjectileRenderer;
import net.jaams.archaeology.client.renderer.AmmoniteProjectileRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JaamsArchaeologyModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(JaamsArchaeologyModEntities.SPEAR_FRAGMENT_PROJECTILE.get(), SpearFragmentProjectileRenderer::new);
		event.registerEntityRenderer(JaamsArchaeologyModEntities.AMMONITE_PROJECTILE.get(), AmmoniteProjectileRenderer::new);
		event.registerEntityRenderer(JaamsArchaeologyModEntities.PRIMITIVE_SPEAR_PROJECTILE.get(), PrimitiveSpearProjectileRenderer::new);
	}
}
