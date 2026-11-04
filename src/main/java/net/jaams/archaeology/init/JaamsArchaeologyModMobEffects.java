

package net.jaams.archaeology.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.jaams.archaeology.potion.TraitorVigorMobEffect;
import net.jaams.archaeology.potion.StrangeVigorMobEffect;
import net.jaams.archaeology.potion.SkeletalVigorMobEffect;
import net.jaams.archaeology.potion.RottenVigorMobEffect;
import net.jaams.archaeology.potion.GuardianVigorMobEffect;
import net.jaams.archaeology.potion.ExplosiveVigorMobEffect;
import net.jaams.archaeology.potion.ArthropodsVigorMobEffect;
import net.jaams.archaeology.JaamsArchaeologyMod;

public class JaamsArchaeologyModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, JaamsArchaeologyMod.MODID);
	public static final RegistryObject<MobEffect> EXPLOSIVE_VIGOR = REGISTRY.register("explosive_vigor", () -> new ExplosiveVigorMobEffect());
	public static final RegistryObject<MobEffect> STRANGE_VIGOR = REGISTRY.register("strange_vigor", () -> new StrangeVigorMobEffect());
	public static final RegistryObject<MobEffect> SKELETAL_VIGOR = REGISTRY.register("skeletal_vigor", () -> new SkeletalVigorMobEffect());
	public static final RegistryObject<MobEffect> ROTTEN_VIGOR = REGISTRY.register("rotten_vigor", () -> new RottenVigorMobEffect());
	public static final RegistryObject<MobEffect> TRAITOR_VIGOR = REGISTRY.register("traitor_vigor", () -> new TraitorVigorMobEffect());
	public static final RegistryObject<MobEffect> ARTHROPODS_VIGOR = REGISTRY.register("arthropods_vigor", () -> new ArthropodsVigorMobEffect());
	public static final RegistryObject<MobEffect> GUARDIAN_VIGOR = REGISTRY.register("guardian_vigor", () -> new GuardianVigorMobEffect());
}
