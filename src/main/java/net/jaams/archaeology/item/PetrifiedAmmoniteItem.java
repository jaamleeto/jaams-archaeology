
package net.jaams.archaeology.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class PetrifiedAmmoniteItem extends Item {
	public PetrifiedAmmoniteItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}
}
