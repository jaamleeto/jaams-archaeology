package net.jaams.archaeology.loot;

import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import net.jaams.archaeology.init.JaamsArchaeologyModItems;

import javax.annotation.Nonnull;

import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.Codec;

import com.google.common.base.Suppliers;

public class AddArchaeologyItemsModifier extends LootModifier {
	public static final Supplier<Codec<AddArchaeologyItemsModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, AddArchaeologyItemsModifier::new)));
	public static float CUSTOM_ITEM_CHANCE = 0.4f;

	public AddArchaeologyItemsModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Nonnull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		ResourceLocation lootTableId = context.getQueriedLootTableId();
		if (lootTableId.equals(new ResourceLocation("minecraft", "archaeology/desert_pyramid")) || lootTableId.equals(new ResourceLocation("minecraft", "archaeology/desert_well"))
				|| lootTableId.equals(new ResourceLocation("minecraft", "archaeology/trail_ruins_rare")) || lootTableId.equals(new ResourceLocation("minecraft", "archaeology/ocean_ruin_cold"))
				|| lootTableId.equals(new ResourceLocation("minecraft", "archaeology/ocean_ruin_warm"))) {
			for (LootItemCondition condition : this.conditions) {
				if (!condition.test(context)) {
					return generatedLoot;
				}
			}
			if (context.getRandom().nextFloat() < CUSTOM_ITEM_CHANCE) {
				generatedLoot.clear();
				int roll = context.getRandom().nextInt(36);
				ItemStack stack;
				if (roll < 5) {
					stack = new ItemStack(JaamsArchaeologyModItems.SPEAR_FRAGMENT.get());
				} else if (roll < 10) {
					stack = new ItemStack(JaamsArchaeologyModItems.PETRIFIED_AMMONITE.get());
				} else if (roll < 15) {
					stack = new ItemStack(JaamsArchaeologyModItems.OSSIFIED_AMMONITE.get());
				} else if (roll < 20) {
					stack = new ItemStack(JaamsArchaeologyModItems.SHINY_AMMONITE.get());
				} else if (roll < 22) {
					stack = new ItemStack(JaamsArchaeologyModItems.BROKEN_SWORD_FRAGMENT.get());
					if (stack.getMaxDamage() > 0) {
						int maxDamage = stack.getMaxDamage();
						stack.setDamageValue(context.getRandom().nextInt((int) (maxDamage * 0.1f), (int) (maxDamage * 0.9f)));
					}
				} else if (roll < 24) {
					stack = new ItemStack(JaamsArchaeologyModItems.EXPLOSIVE_IDOL.get());
				} else if (roll < 26) {
					stack = new ItemStack(JaamsArchaeologyModItems.ROTTEN_IDOL.get());
				} else if (roll < 28) {
					stack = new ItemStack(JaamsArchaeologyModItems.TRAITOR_IDOL.get());
				} else if (roll < 30) {
					stack = new ItemStack(JaamsArchaeologyModItems.GUARDIAN_IDOL.get());
				} else if (roll < 32) {
					stack = new ItemStack(JaamsArchaeologyModItems.ARTHROPODS_IDOL.get());
				} else if (roll < 34) {
					stack = new ItemStack(JaamsArchaeologyModItems.STRANGE_IDOL.get());
				} else {
					stack = new ItemStack(JaamsArchaeologyModItems.SKELETAL_IDOL.get());
				}
				generatedLoot.add(stack);
			}
		}
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}
