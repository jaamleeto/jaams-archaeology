package net.jaams.archaeology.handler.events;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.archaeology.item.SwordOfUndyingItem;
import net.jaams.archaeology.item.PrimitiveSpearItem;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class AttributesHandler {
	private static final Map<String, UUID> ATTRIBUTE_UUID_MAP = new ConcurrentHashMap<>();

	@SubscribeEvent
	public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
		EquipmentSlot slot = event.getSlotType();
		Item item = event.getItemStack().getItem();
		ItemStack itemStack = event.getItemStack();
		List<AttributeEntry> attributeEntries = new ArrayList<>();
		event.getModifiers().entries().forEach(entry -> {
			attributeEntries.add(new AttributeEntry(entry.getKey(), entry.getValue()));
		});
		if (slot == EquipmentSlot.MAINHAND && !ModList.get().isLoaded("bettercombat")) {
			if (item instanceof SwordOfUndyingItem) {
				attributeEntries.add(createEntry("Sword Of Undying Reach Modifier", ForgeMod.ENTITY_REACH.get(), 0.5, AttributeModifier.Operation.ADDITION));
			} else if (item instanceof PrimitiveSpearItem) {
				attributeEntries.add(createEntry("Primitive Spear Reach Modifier", ForgeMod.ENTITY_REACH.get(), 1.5, AttributeModifier.Operation.ADDITION));
			}
			for (AttributeEntry entry : attributeEntries) {
				event.addModifier(entry.attribute, entry.modifier);
			}
		}
	}

	private static AttributeEntry createEntry(String name, net.minecraft.world.entity.ai.attributes.Attribute attribute, double value, AttributeModifier.Operation operation) {
		UUID uuid = ATTRIBUTE_UUID_MAP.computeIfAbsent(name, key -> UUID.randomUUID());
		AttributeModifier modifier = new AttributeModifier(uuid, name, value, operation);
		return new AttributeEntry(attribute, modifier);
	}

	private static class AttributeEntry {
		final net.minecraft.world.entity.ai.attributes.Attribute attribute;
		final AttributeModifier modifier;

		AttributeEntry(net.minecraft.world.entity.ai.attributes.Attribute attribute, AttributeModifier modifier) {
			this.attribute = attribute;
			this.modifier = modifier;
		}
	}
}
