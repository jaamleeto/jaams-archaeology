
package net.jaams.archaeology.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class GuardianIdolItem extends Item {
	public GuardianIdolItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}
}
