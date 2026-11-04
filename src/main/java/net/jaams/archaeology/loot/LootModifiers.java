package net.jaams.archaeology.loot;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.loot.IGlobalLootModifier;

import net.jaams.archaeology.JaamsArchaeologyMod;

import com.mojang.serialization.Codec;

public class LootModifiers {
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, JaamsArchaeologyMod.MODID);
	public static final RegistryObject<Codec<AddItemsLootModifier>> ADD_ITEMS = LOOT_MODIFIER_SERIALIZERS.register("add_items", () -> AddItemsLootModifier.CODEC.get());
	public static final RegistryObject<Codec<AddArchaeologyItemsModifier>> ADD_ARCHAEOLOGY_ITEMS = LOOT_MODIFIER_SERIALIZERS.register("add_archaeology_items", () -> AddArchaeologyItemsModifier.CODEC.get());

	public static void register(IEventBus modEventBus) {
		LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
	}
}
