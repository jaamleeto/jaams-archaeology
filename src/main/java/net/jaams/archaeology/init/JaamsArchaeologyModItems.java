
package net.jaams.archaeology.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.jaams.archaeology.item.TraitorIdolItem;
import net.jaams.archaeology.item.SwordOfUndyingItem;
import net.jaams.archaeology.item.StrangeIdolItem;
import net.jaams.archaeology.item.SpearFragmentItem;
import net.jaams.archaeology.item.SkeletalIdolItem;
import net.jaams.archaeology.item.ShinyAmmoniteItem;
import net.jaams.archaeology.item.RottenIdolItem;
import net.jaams.archaeology.item.PrimitiveSpearItem;
import net.jaams.archaeology.item.PetrifiedAmmoniteItem;
import net.jaams.archaeology.item.OssifiedAmmoniteItem;
import net.jaams.archaeology.item.GuardianIdolItem;
import net.jaams.archaeology.item.ExplosiveIdolItem;
import net.jaams.archaeology.item.BrokenSwordFragmentItem;
import net.jaams.archaeology.item.ArthropodsIdolItem;
import net.jaams.archaeology.JaamsArchaeologyMod;

public class JaamsArchaeologyModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, JaamsArchaeologyMod.MODID);
	public static final RegistryObject<Item> PRIMITIVE_SPEAR = REGISTRY.register("primitive_spear", () -> new PrimitiveSpearItem());
	public static final RegistryObject<Item> SPEAR_FRAGMENT = REGISTRY.register("spear_fragment", () -> new SpearFragmentItem());
	public static final RegistryObject<Item> SWORD_OF_UNDYING = REGISTRY.register("sword_of_undying", () -> new SwordOfUndyingItem());
	public static final RegistryObject<Item> PETRIFIED_AMMONITE = REGISTRY.register("petrified_ammonite", () -> new PetrifiedAmmoniteItem());
	public static final RegistryObject<Item> OSSIFIED_AMMONITE = REGISTRY.register("ossified_ammonite", () -> new OssifiedAmmoniteItem());
	public static final RegistryObject<Item> SHINY_AMMONITE = REGISTRY.register("shiny_ammonite", () -> new ShinyAmmoniteItem());
	public static final RegistryObject<Item> BROKEN_SWORD_FRAGMENT = REGISTRY.register("broken_sword_fragment", () -> new BrokenSwordFragmentItem());
	public static final RegistryObject<Item> EXPLOSIVE_IDOL = REGISTRY.register("explosive_idol", () -> new ExplosiveIdolItem());
	public static final RegistryObject<Item> STRANGE_IDOL = REGISTRY.register("strange_idol", () -> new StrangeIdolItem());
	public static final RegistryObject<Item> SKELETAL_IDOL = REGISTRY.register("skeletal_idol", () -> new SkeletalIdolItem());
	public static final RegistryObject<Item> ROTTEN_IDOL = REGISTRY.register("rotten_idol", () -> new RottenIdolItem());
	public static final RegistryObject<Item> TRAITOR_IDOL = REGISTRY.register("traitor_idol", () -> new TraitorIdolItem());
	public static final RegistryObject<Item> GUARDIAN_IDOL = REGISTRY.register("guardian_idol", () -> new GuardianIdolItem());
	public static final RegistryObject<Item> ARTHROPODS_IDOL = REGISTRY.register("arthropods_idol", () -> new ArthropodsIdolItem());
}
