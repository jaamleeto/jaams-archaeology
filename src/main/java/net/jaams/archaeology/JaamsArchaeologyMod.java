package net.jaams.archaeology;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import net.jaams.archaeology.util.ModTab;
import net.jaams.archaeology.registry.CustomParticles;
import net.jaams.archaeology.packet.AmountPacket;
import net.jaams.archaeology.packet.AberrationPacket;
import net.jaams.archaeology.loot.LootModifiers;
import net.jaams.archaeology.init.JaamsArchaeologyModSounds;
import net.jaams.archaeology.init.JaamsArchaeologyModMobEffects;
import net.jaams.archaeology.init.JaamsArchaeologyModItems;
import net.jaams.archaeology.init.JaamsArchaeologyModEntities;
import net.jaams.archaeology.init.JaamsArchaeologyModEnchantments;
import net.jaams.archaeology.dispenser.SpearFragmentDispenser;
import net.jaams.archaeology.dispenser.AmmoniteDispenser;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod("jaams_archaeology")
public class JaamsArchaeologyMod {
	public static final Logger LOGGER = LogManager.getLogger(JaamsArchaeologyMod.class);
	public static final String MODID = "jaams_archaeology";

	public JaamsArchaeologyMod() {
		// Mod
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		JaamsArchaeologyModSounds.REGISTRY.register(bus);
		JaamsArchaeologyModItems.REGISTRY.register(bus);
		JaamsArchaeologyModEntities.REGISTRY.register(bus);
		JaamsArchaeologyModEnchantments.REGISTRY.register(bus);
		JaamsArchaeologyModMobEffects.REGISTRY.register(bus);
		// Loot Modifier
		LootModifiers.register(bus);
		// Creative Tab
		ModTab.REGISTRY.register(bus);
		// Custom Particles Data
		CustomParticles.register(bus);
		// Common Setup
		bus.addListener(this::commonSetup);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Dispenser
		DispenserBlock.registerBehavior(JaamsArchaeologyModItems.PETRIFIED_AMMONITE.get(), new AmmoniteDispenser());
		DispenserBlock.registerBehavior(JaamsArchaeologyModItems.OSSIFIED_AMMONITE.get(), new AmmoniteDispenser());
		DispenserBlock.registerBehavior(JaamsArchaeologyModItems.SHINY_AMMONITE.get(), new AmmoniteDispenser());
		DispenserBlock.registerBehavior(JaamsArchaeologyModItems.SPEAR_FRAGMENT.get(), new SpearFragmentDispenser());
		// Packets
		addNetworkMessage(AmountPacket.class, AmountPacket::encode, AmountPacket::decode, AmountPacket::handle);
		addNetworkMessage(AberrationPacket.class, AberrationPacket::encode, AberrationPacket::decode, AberrationPacket::handle);
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}
