
package net.jaams.archaeology.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class StrangeIdolItem extends Item {
	public StrangeIdolItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}
}
