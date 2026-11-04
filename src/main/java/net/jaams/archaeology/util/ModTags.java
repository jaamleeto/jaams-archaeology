package net.jaams.archaeology.util;

import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

public class ModTags {
	// Weapons
	public static final TagKey<Item> BROKEN_SWORD_FRAGMENTS = create("weapon/broken_sword_fragments");
	public static final TagKey<Item> PRIMITIVE_SPEARS = create("weapon/primitive_spears");
	public static final TagKey<Item> SPEAR_FRAGMENTS = create("weapon/spear_fragments");
	public static final TagKey<Item> SWORDS_OF_UNDYING = create("weapon/swords_of_undying");
	// Misc
	public static final TagKey<Item> AMMONITES = create("misc/ammonites");
	public static final TagKey<Item> IDOLS = create("misc/idols");
	// Traits
	public static final TagKey<Item> DEFLECT_CHARGE = create("trait/deflect_charge");
	public static final TagKey<Item> JUDGMENT_BURST = create("trait/judgment_burst");
	public static final TagKey<Item> TOTEM_INCARNATE = create("trait/totem_incarnate");
	public static final TagKey<Item> UNDYING_RETRIBUTION = create("trait/undying_retribution");
	public static final TagKey<Item> FRAILSHARD = create("trait/frailshard");

	private static TagKey<Item> create(String path) {
		return TagKey.create(Registries.ITEM, new ResourceLocation("jaams_archaeology", path));
	}
}
