package net.jaams.archaeology.configuration.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class CameraEffectsConfig {
	public static ForgeConfigSpec.BooleanValue FOV_MODIFIER;
	public static ForgeConfigSpec.BooleanValue CAMERA_SHAKE;

	public static void register(ForgeConfigSpec.Builder builder) {
		// Camera Effects Handler
		builder.push("Camera Effects Handler");
		// Fov Modifier
		builder.push("Fov Modifier");
		FOV_MODIFIER = builder.comment("Enable or disable FOV modifier").define("Fov Modifier", true);
		builder.pop();
		// Camera Shake
		builder.push("Camera Shake");
		CAMERA_SHAKE = builder.comment("Enables or disables camera shake.").define("Camera Shake", true);
		builder.pop();
		builder.pop();
	}
}
