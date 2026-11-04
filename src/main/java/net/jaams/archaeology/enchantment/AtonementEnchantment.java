
package net.jaams.archaeology.enchantment;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.archaeology.init.JaamsArchaeologyModItems;

import java.util.List;

public class AtonementEnchantment extends Enchantment {
	public AtonementEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, slots);
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	protected boolean checkCompatibility(Enchantment ench) {
		return this != ench && !List.of(Enchantments.MENDING).contains(ench);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack itemstack) {
		return Ingredient.of(new ItemStack(JaamsArchaeologyModItems.SWORD_OF_UNDYING.get())).test(itemstack);
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}
}
