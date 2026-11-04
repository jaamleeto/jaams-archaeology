package net.jaams.archaeology.configuration.client;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class TooltipsConfig {
	// Tooltips System 
	public static ForgeConfigSpec.BooleanValue TOOLTIPS;
	public static ForgeConfigSpec.BooleanValue CONTROL_TOOLTIPS;
	public static ForgeConfigSpec.BooleanValue ALT_TOOLTIPS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_TOOLTIPS_ITEMS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_CONTROL_TOOLTIPS_ITEMS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_ALT_TOOLTIPS_ITEMS;
	// Throwing
	public static ForgeConfigSpec.BooleanValue TOOLTIP_THROWING_PROPERTIES;
	public static ForgeConfigSpec.BooleanValue TOOLTIP_THROWING_MODE;

	public static void register(ForgeConfigSpec.Builder builder) {
		// Tooltips System Handler
		builder.push("Tooltips System Handler");
		builder.push("Tooltips Base Settings");
		TOOLTIPS = builder.comment("Enable or disable ALL custom tooltips added by the mod").define("Enable Tooltips", true);
		CONTROL_TOOLTIPS = builder.comment("Enable or disable tooltips shown when holding the Control key added by the mod").define("Enable Extra Info Tooltips", true);
		ALT_TOOLTIPS = builder.comment("Enable or disable tooltips shown when holding the Alt key added by the mod").define("Enable Descriptions Tooltips", true);
		EXCLUDED_TOOLTIPS_ITEMS = builder.comment("List of item IDs excluded from ALL custom tooltips added by the mod").defineList("Excluded Tooltips Items", List.of(), obj -> obj instanceof String);
		EXCLUDED_CONTROL_TOOLTIPS_ITEMS = builder.comment("List of item IDs excluded from Control key tooltips added by the mod").defineList("Excluded Extra Info Tooltips Items", List.of(), obj -> obj instanceof String);
		EXCLUDED_ALT_TOOLTIPS_ITEMS = builder.comment("List of item IDs excluded from Alt key tooltips added by the mod").defineList("Excluded Descriptions Tooltips Items", List.of(), obj -> obj instanceof String);
		builder.pop();
		builder.push("Weapon Traits Tooltips");
		builder.push("Throwable");
		TOOLTIP_THROWING_PROPERTIES = builder.comment("Show the 'Throwing Properties' section in tooltips added by the mod").define("Show Throwing Properties", true);
		TOOLTIP_THROWING_MODE = builder.comment("Show the 'Throwing Mode' section in tooltips added by the mod").define("Show Throwing Mode", true);
		builder.pop();
		builder.pop();
	}
}
