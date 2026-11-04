package net.jaams.archaeology.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ModTraits {
	public static boolean isDeflectChargeItem(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains("DeflectChargeTrait")) {
			return tag.getBoolean("DeflectChargeTrait");
		}
		return stack.is(ModTags.DEFLECT_CHARGE);
	}
}
