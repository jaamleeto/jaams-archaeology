
package net.jaams.archaeology.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.jaams.archaeology.JaamsArchaeologyMod;

public class JaamsArchaeologyModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JaamsArchaeologyMod.MODID);
	public static final RegistryObject<SoundEvent> AMMONITE_FIRED = REGISTRY.register("ammonite_fired", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "ammonite_fired")));
	public static final RegistryObject<SoundEvent> AMMONITE_BREAKS = REGISTRY.register("ammonite_breaks", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "ammonite_breaks")));
	public static final RegistryObject<SoundEvent> PRIMITIVE_SPEAR_FIRED = REGISTRY.register("primitive_spear_fired", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "primitive_spear_fired")));
	public static final RegistryObject<SoundEvent> PRIMITIVE_SPEAR_GROUND = REGISTRY.register("primitive_spear_ground", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "primitive_spear_ground")));
	public static final RegistryObject<SoundEvent> PRIMITIVE_SPEAR_HIT = REGISTRY.register("primitive_spear_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "primitive_spear_hit")));
	public static final RegistryObject<SoundEvent> PRIMITIVE_SPEAR_RETURN = REGISTRY.register("primitive_spear_return", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "primitive_spear_return")));
	public static final RegistryObject<SoundEvent> SPEAR_FRAGMENT_FIRED = REGISTRY.register("spear_fragment_fired", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "spear_fragment_fired")));
	public static final RegistryObject<SoundEvent> SPEAR_FRAGMENT_GROUND = REGISTRY.register("spear_fragment_ground", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "spear_fragment_ground")));
	public static final RegistryObject<SoundEvent> SPEAR_FRAGMENT_HIT = REGISTRY.register("spear_fragment_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "spear_fragment_hit")));
	public static final RegistryObject<SoundEvent> IDOL_BREAKS = REGISTRY.register("idol_breaks", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "idol_breaks")));
	public static final RegistryObject<SoundEvent> EMPTY_SOUND = REGISTRY.register("empty_sound", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "empty_sound")));
	public static final RegistryObject<SoundEvent> EXPLOSIVE_VIGOR_STARTED = REGISTRY.register("explosive_vigor_started", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "explosive_vigor_started")));
	public static final RegistryObject<SoundEvent> EXPLOSIVE_VIGOR_EXPIRES = REGISTRY.register("explosive_vigor_expires", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "explosive_vigor_expires")));
	public static final RegistryObject<SoundEvent> ROTTEN_VIGOR_STARTED = REGISTRY.register("rotten_vigor_started", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "rotten_vigor_started")));
	public static final RegistryObject<SoundEvent> ROTTEN_VIGOR_EXPIRES = REGISTRY.register("rotten_vigor_expires", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "rotten_vigor_expires")));
	public static final RegistryObject<SoundEvent> SKELETAL_VIGOR_STARTED = REGISTRY.register("skeletal_vigor_started", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "skeletal_vigor_started")));
	public static final RegistryObject<SoundEvent> SKELETAL_VIGOR_EXPIRES = REGISTRY.register("skeletal_vigor_expires", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "skeletal_vigor_expires")));
	public static final RegistryObject<SoundEvent> TRAITOR_VIGOR_STARTED = REGISTRY.register("traitor_vigor_started", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "traitor_vigor_started")));
	public static final RegistryObject<SoundEvent> TRAITOR_VIGOR_EXPIRES = REGISTRY.register("traitor_vigor_expires", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "traitor_vigor_expires")));
	public static final RegistryObject<SoundEvent> STRANGE_VIGOR_STARTED = REGISTRY.register("strange_vigor_started", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "strange_vigor_started")));
	public static final RegistryObject<SoundEvent> STRANGE_VIGOR_EXPIRES = REGISTRY.register("strange_vigor_expires", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "strange_vigor_expires")));
	public static final RegistryObject<SoundEvent> GUARDIAN_VIGOR_STARTED = REGISTRY.register("guardian_vigor_started", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "guardian_vigor_started")));
	public static final RegistryObject<SoundEvent> GUARDIAN_VIGOR_EXPIRES = REGISTRY.register("guardian_vigor_expires", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "guardian_vigor_expires")));
	public static final RegistryObject<SoundEvent> ARTHROPODS_VIGOR_STARTED = REGISTRY.register("arthropods_vigor_started", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "arthropods_vigor_started")));
	public static final RegistryObject<SoundEvent> ARTHROPODS_VIGOR_EXPIRES = REGISTRY.register("arthropods_vigor_expires", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "arthropods_vigor_expires")));
	public static final RegistryObject<SoundEvent> PROJECTILE_GROUND = REGISTRY.register("projectile_ground", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "projectile_ground")));
	public static final RegistryObject<SoundEvent> PROJECTILE_RETURN = REGISTRY.register("projectile_return", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "projectile_return")));
	public static final RegistryObject<SoundEvent> PROJECTILE_HIT = REGISTRY.register("projectile_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "projectile_hit")));
	public static final RegistryObject<SoundEvent> PROJECTILE_THROW = REGISTRY.register("projectile_throw", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jaams_archaeology", "projectile_throw")));
}
