
package net.jaams.archaeology.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

import net.jaams.archaeology.entity.SpearFragmentProjectileEntity;
import net.jaams.archaeology.entity.PrimitiveSpearProjectileEntity;
import net.jaams.archaeology.entity.AmmoniteProjectileEntity;
import net.jaams.archaeology.JaamsArchaeologyMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JaamsArchaeologyModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JaamsArchaeologyMod.MODID);
	public static final RegistryObject<EntityType<SpearFragmentProjectileEntity>> SPEAR_FRAGMENT_PROJECTILE = register("spear_fragment_projectile",
			EntityType.Builder.<SpearFragmentProjectileEntity>of(SpearFragmentProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(SpearFragmentProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<AmmoniteProjectileEntity>> AMMONITE_PROJECTILE = register("ammonite_projectile", EntityType.Builder.<AmmoniteProjectileEntity>of(AmmoniteProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(AmmoniteProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<PrimitiveSpearProjectileEntity>> PRIMITIVE_SPEAR_PROJECTILE = register("primitive_spear_projectile",
			EntityType.Builder.<PrimitiveSpearProjectileEntity>of(PrimitiveSpearProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(PrimitiveSpearProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}
}
