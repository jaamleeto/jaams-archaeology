package net.jaams.archaeology.loot;

import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import net.jaams.archaeology.init.JaamsArchaeologyModItems;
import net.jaams.archaeology.init.JaamsArchaeologyModEnchantments;

import javax.annotation.Nonnull;

import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.Codec;

import com.google.common.base.Suppliers;

public class AddItemsLootModifier extends LootModifier {
	public static final Supplier<Codec<AddItemsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, AddItemsLootModifier::new)));
	public static float COMMON_ITEMS_CHANCE = 0.2f;
	public static float SWORD_OF_UNDYING_CHANCE = 0.05f;
	public static float ATONEMENT_BOOK_CHANCE_PYRAMID = 0.15f;
	public static float ENCHANTED_BOOK_CHANCE_RUINS = 0.15f;
	public static float ATONEMENT_LEVEL_ONE_CHANCE_PYRAMID = 0.5f;
	public static float ENCHANTED_BOOK_LEVEL_ONE_CHANCE_RUINS = 0.6f;

	protected AddItemsLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Nonnull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		ResourceLocation lootTableId = context.getQueriedLootTableId();
		if (lootTableId.equals(new ResourceLocation("minecraft", "chests/desert_pyramid"))) {
			addChestItems(generatedLoot, context, COMMON_ITEMS_CHANCE);
			if (context.getRandom().nextFloat() < SWORD_OF_UNDYING_CHANCE) {
				generatedLoot.add(new ItemStack(JaamsArchaeologyModItems.SWORD_OF_UNDYING.get()));
			}
			addEnchantedBook(generatedLoot, context, lootTableId);
		} else if (lootTableId.equals(new ResourceLocation("minecraft", "chests/underwater_ruin_small")) || lootTableId.equals(new ResourceLocation("minecraft", "chests/underwater_ruin_big"))) {
			addChestItems(generatedLoot, context, COMMON_ITEMS_CHANCE);
			addEnchantedBook(generatedLoot, context, lootTableId);
		}
		return generatedLoot;
	}

	private void addChestItems(ObjectArrayList<ItemStack> loot, LootContext context, float chance) {
		if (context.getRandom().nextFloat() < chance) {
			int roll = context.getRandom().nextInt(6);
			ItemStack stack = switch (roll) {
				case 0 -> new ItemStack(JaamsArchaeologyModItems.EXPLOSIVE_IDOL.get());
				case 1 -> new ItemStack(JaamsArchaeologyModItems.ROTTEN_IDOL.get());
				case 2 -> new ItemStack(JaamsArchaeologyModItems.TRAITOR_IDOL.get());
				case 3 -> new ItemStack(JaamsArchaeologyModItems.GUARDIAN_IDOL.get());
				case 4 -> new ItemStack(JaamsArchaeologyModItems.ARTHROPODS_IDOL.get());
				case 5 -> new ItemStack(JaamsArchaeologyModItems.STRANGE_IDOL.get());
				default -> ItemStack.EMPTY;
			};
			if (!stack.isEmpty()) {
				loot.add(stack);
			}
		}
	}

	private void addEnchantedBook(ObjectArrayList<ItemStack> loot, LootContext context, ResourceLocation lootTableId) {
		if (lootTableId.equals(new ResourceLocation("minecraft", "chests/desert_pyramid"))) {
			if (context.getRandom().nextFloat() < ATONEMENT_BOOK_CHANCE_PYRAMID) {
				ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
				int level = context.getRandom().nextFloat() < ATONEMENT_LEVEL_ONE_CHANCE_PYRAMID ? 1 : 2;
				EnchantmentHelper.setEnchantments(java.util.Collections.singletonMap(JaamsArchaeologyModEnchantments.ATONEMENT.get(), level), book);
				loot.add(book);
			}
		} else if (lootTableId.equals(new ResourceLocation("minecraft", "chests/underwater_ruin_small")) || lootTableId.equals(new ResourceLocation("minecraft", "chests/underwater_ruin_big"))) {
			if (context.getRandom().nextFloat() < ENCHANTED_BOOK_CHANCE_RUINS) {
				ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
				int roll = context.getRandom().nextInt(3);
				int level = context.getRandom().nextFloat() < ENCHANTED_BOOK_LEVEL_ONE_CHANCE_RUINS ? 1 : 2;
				switch (roll) {
					case 0 -> EnchantmentHelper.setEnchantments(java.util.Collections.singletonMap(JaamsArchaeologyModEnchantments.ATONEMENT.get(), level), book);
					case 1 -> EnchantmentHelper.setEnchantments(java.util.Collections.singletonMap(JaamsArchaeologyModEnchantments.STRIKEHOLD.get(), level), book);
					case 2 -> EnchantmentHelper.setEnchantments(java.util.Collections.singletonMap(JaamsArchaeologyModEnchantments.VENGEANCE.get(), level), book);
				}
				loot.add(book);
			}
		}
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}
