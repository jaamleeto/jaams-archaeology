
package net.jaams.archaeology.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.jaams.archaeology.init.JaamsArchaeologyModItems;

public class SwordOfUndyingItem extends SwordItem {
	public SwordOfUndyingItem() {
		super(new Tier() {
			public int getUses() {
				return 676;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 4f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(JaamsArchaeologyModItems.SWORD_OF_UNDYING.get()));
			}
		}, 3, -2.8f, new Item.Properties());
	}
}
