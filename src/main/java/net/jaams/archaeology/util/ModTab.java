package net.jaams.archaeology.util;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;

import net.jaams.archaeology.init.JaamsArchaeologyModItems;
import net.jaams.archaeology.init.JaamsArchaeologyModEnchantments;
import net.jaams.archaeology.configuration.client.CreativeTabConfig;
import net.jaams.archaeology.JaamsArchaeologyMod;

import com.google.common.collect.ImmutableMap;

public class ModTab {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JaamsArchaeologyMod.MODID);
	public static final RegistryObject<CreativeModeTab> archaeology = REGISTRY.register("archaeology",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.jaams_archaeology.tab_icon").withStyle(style -> style.withFont(new ResourceLocation("jaams_archaeology", "default")))).icon(() -> getTabIcon())
					.withTabsBefore(getPreferredTabBefore()).displayItems((parameters, tabData) -> {
						tabData.accept(JaamsArchaeologyModItems.GUARDIAN_IDOL.get());
						tabData.accept(JaamsArchaeologyModItems.TRAITOR_IDOL.get());
						tabData.accept(JaamsArchaeologyModItems.ROTTEN_IDOL.get());
						tabData.accept(JaamsArchaeologyModItems.STRANGE_IDOL.get());
						tabData.accept(JaamsArchaeologyModItems.SKELETAL_IDOL.get());
						tabData.accept(JaamsArchaeologyModItems.EXPLOSIVE_IDOL.get());
						tabData.accept(JaamsArchaeologyModItems.ARTHROPODS_IDOL.get());
						tabData.accept(JaamsArchaeologyModItems.PRIMITIVE_SPEAR.get());
						tabData.accept(JaamsArchaeologyModItems.SPEAR_FRAGMENT.get());
						tabData.accept(JaamsArchaeologyModItems.SHINY_AMMONITE.get());
						tabData.accept(JaamsArchaeologyModItems.PETRIFIED_AMMONITE.get());
						tabData.accept(JaamsArchaeologyModItems.OSSIFIED_AMMONITE.get());
						tabData.accept(JaamsArchaeologyModItems.SWORD_OF_UNDYING.get());
						tabData.accept(JaamsArchaeologyModItems.BROKEN_SWORD_FRAGMENT.get());
						// Enchanted Books
						tabData.accept(addEnchantments(new ItemStack(Items.ENCHANTED_BOOK), JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), 1));
						tabData.accept(addEnchantments(new ItemStack(Items.ENCHANTED_BOOK), JaamsArchaeologyModEnchantments.VENGEANCE.get(), 3));
						tabData.accept(addEnchantments(new ItemStack(Items.ENCHANTED_BOOK), JaamsArchaeologyModEnchantments.ATONEMENT.get(), 4));
					}).build());

	private static ItemStack addEnchantments(ItemStack itemStack, Enchantment enchantment, int level) {
		EnchantmentHelper.setEnchantments(ImmutableMap.of(enchantment, level), itemStack);
		return itemStack;
	}

	private static ResourceLocation getPreferredTabBefore() {
		if (ModList.get().isLoaded("jaams_cavenry")) {
			return new ResourceLocation("jaams_cavenry", "cavenry");
		} else if (ModList.get().isLoaded("jaams_weaponry")) {
			return new ResourceLocation("jaams_weaponry", "weaponry");
		}
		return CreativeModeTabs.SPAWN_EGGS.location();
	}

	private static ItemStack getTabIcon() {
		try {
			String itemId = CreativeTabConfig.CREATIVE_TAB_ICON.get();
			ResourceLocation resourceLocation = new ResourceLocation(itemId);
			Item item = BuiltInRegistries.ITEM.get(resourceLocation);
			if (item != null && item != Items.AIR) {
				return new ItemStack(item);
			} else {
				return new ItemStack(JaamsArchaeologyModItems.SWORD_OF_UNDYING.get());
			}
		} catch (Exception e) {
			return new ItemStack(JaamsArchaeologyModItems.SWORD_OF_UNDYING.get());
		}
	}

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
	}
}
