package net.jaams.archaeology.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraft.network.chat.Component;
import net.minecraft.commands.Commands;
import net.minecraft.ChatFormatting;

@Mod.EventBusSubscriber
public class ArchaeologyCommand {
	private static String getModVersion() {
		try {
			return ModList.get().getModContainerById("archaeology").map(container -> container.getModInfo().getVersion().toString()).orElse("Unknown");
		} catch (Exception e) {
			return "Unknown";
		}
	}

	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("jaams").then(Commands.literal("archaeology").then(Commands.literal("version").executes(context -> {
			String version = getModVersion();
			context.getSource().sendSuccess(() -> Component.translatable("commands.jaams.archaeology.version", Component.literal(version).withStyle(ChatFormatting.YELLOW)), false);
			return 1;
		}))));
	}
}
