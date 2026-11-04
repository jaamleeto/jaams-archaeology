package net.jaams.archaeology.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.enchantment.Enchantment;

import net.jaams.archaeology.enchantment.VengeanceEnchantment;
import net.jaams.archaeology.enchantment.StrikeholdEnchantment;
import net.jaams.archaeology.enchantment.AtonementEnchantment;
import net.jaams.archaeology.JaamsArchaeologyMod;

public class JaamsArchaeologyModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, JaamsArchaeologyMod.MODID);
	public static final RegistryObject<Enchantment> ATONEMENT = REGISTRY.register("atonement", () -> new AtonementEnchantment());
	public static final RegistryObject<Enchantment> STRIKEHOLD = REGISTRY.register("strikehold", () -> new StrikeholdEnchantment());
	public static final RegistryObject<Enchantment> VENGEANCE = REGISTRY.register("vengeance", () -> new VengeanceEnchantment());
}
