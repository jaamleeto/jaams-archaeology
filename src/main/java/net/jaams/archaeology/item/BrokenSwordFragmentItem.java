
package net.jaams.archaeology.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.jaams.archaeology.init.JaamsArchaeologyModItems;
import net.jaams.archaeology.dyeable.IDyeableItem;

public class BrokenSwordFragmentItem extends SwordItem implements IDyeableItem {
	public BrokenSwordFragmentItem() {
		super(new Tier() {
			public int getUses() {
				return 226;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return -3f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 10;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(JaamsArchaeologyModItems.BROKEN_SWORD_FRAGMENT.get()));
			}
		}, 3, -2.2f, new Item.Properties());
	}

	@Override
	public int getDefaultColor() {
		return 0xFF44CC44;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}
}
