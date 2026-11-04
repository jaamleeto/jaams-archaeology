
package net.jaams.archaeology.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class ShinyAmmoniteItem extends Item {
	public ShinyAmmoniteItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
	}
}
