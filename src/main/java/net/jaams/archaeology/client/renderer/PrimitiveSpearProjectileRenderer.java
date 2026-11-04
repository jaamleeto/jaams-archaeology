package net.jaams.archaeology.client.renderer;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;

import net.jaams.archaeology.entity.PrimitiveSpearProjectileEntity;
import net.jaams.archaeology.data.ProjectileRenderData;
import net.jaams.archaeology.configuration.client.ProjectileClientConfig;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class PrimitiveSpearProjectileRenderer extends EntityRenderer<PrimitiveSpearProjectileEntity> {
	public PrimitiveSpearProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(PrimitiveSpearProjectileEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		matrixStack.pushPose();
		ItemStack weapon = entity.weaponItem;
		ProjectileRenderData data = new ProjectileRenderData(weapon);
		float scale = data.getFloat("ProjectileScale", ProjectileClientConfig.PRIMITIVE_SPEAR_PROJECTILE_SCALE.get().floatValue());
		float rotX = data.getFloat("ProjectileRotX", -90f);
		float rotY = data.getFloat("ProjectileRotY", -180f);
		float rotZ = data.getFloat("ProjectileRotZ", 0f);
		float offsetX = data.getFloat("ProjectileOffsetX", 0f);
		float offsetY = data.getFloat("ProjectileOffsetY", -1.25f);
		float offsetZ = data.getFloat("ProjectileOffsetZ", 0f);
		float spinSpeed = data.getFloat("ProjectileSpin", 0f);
		String spinAxis = data.getString("ProjectileSpinAxis", "Z");
		float spinOffset = data.getFloat("ProjectileSpinOffset", 0f);
		float pitchOffset = data.getFloat("ProjectilePitchOffset", 0f);
		ItemDisplayContext display = data.getDisplay("ProjectileDisplay", ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
		matrixStack.scale(scale, scale, scale);
		matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + rotY));
		matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + rotX + pitchOffset));
		float spinRotation = 0f;
		if (spinSpeed > 0 && !entity.hasImpacted()) {
			float velocityFactor = (float) entity.getDeltaMovement().length() * 10f;
			float finalSpinSpeed = spinSpeed + velocityFactor;
			spinRotation = (entity.tickCount + partialTicks) * finalSpinSpeed + spinOffset;
		}
		switch (spinAxis) {
			case "X" -> matrixStack.mulPose(Axis.XP.rotationDegrees(rotZ + spinRotation));
			case "Y" -> matrixStack.mulPose(Axis.YP.rotationDegrees(rotZ + spinRotation));
			default -> matrixStack.mulPose(Axis.ZP.rotationDegrees(rotZ + spinRotation));
		}
		matrixStack.translate(offsetX, offsetY, offsetZ);
		Minecraft.getInstance().getItemRenderer().renderStatic(weapon, display, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, entity.level(), entity.getId());
		matrixStack.popPose();
		super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(PrimitiveSpearProjectileEntity entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
