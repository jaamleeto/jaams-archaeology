
package net.jaams.archaeology.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class OssifiedAmmoniteItem extends Item {
	public OssifiedAmmoniteItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}
}
