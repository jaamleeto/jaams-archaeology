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

import net.jaams.archaeology.entity.SpearFragmentProjectileEntity;
import net.jaams.archaeology.data.ProjectileRenderData;
import net.jaams.archaeology.configuration.client.ProjectileClientConfig;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class SpearFragmentProjectileRenderer extends EntityRenderer<SpearFragmentProjectileEntity> {
	public SpearFragmentProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(SpearFragmentProjectileEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		matrixStack.pushPose();
		ItemStack weapon = entity.weaponItem;
		ProjectileRenderData data = new ProjectileRenderData(weapon);
		float scale = data.getFloat("ProjectileScale", ProjectileClientConfig.SPEAR_FRAGMENT_PROJECTILE_SCALE.get().floatValue());
		float rotX = data.getFloat("ProjectileRotX", 0.0F);
		float rotY = data.getFloat("ProjectileRotY", -90.0F);
		float rotZ = data.getFloat("ProjectileRotZ", -135.0F);
		float offsetX = data.getFloat("ProjectileOffsetX", 0.0F);
		float offsetY = data.getFloat("ProjectileOffsetY", -0.175F);
		float offsetZ = data.getFloat("ProjectileOffsetZ", 0.0F);
		float spinSpeed = data.getFloat("ProjectileSpin", 0f);
		String spinAxis = data.getString("ProjectileSpinAxis", "Z");
		float spinOffset = data.getFloat("ProjectileSpinOffset", 0f);
		float pitchOffset = data.getFloat("ProjectilePitchOffset", 0f);
		ItemDisplayContext display = data.getDisplay("ProjectileDisplay", ItemDisplayContext.GROUND);
		matrixStack.scale(scale, scale, scale);
		matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + rotY));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + rotX + pitchOffset));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(rotZ));
		matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		float spinRotation = 0f;
		if (spinSpeed > 0 && !entity.hasImpacted()) {
			float velocityFactor = (float) entity.getDeltaMovement().length() * 10f;
			float finalSpin = spinSpeed + velocityFactor;
			spinRotation = (entity.tickCount + partialTicks) * finalSpin + spinOffset;
			switch (spinAxis) {
				case "X" -> matrixStack.mulPose(Axis.XP.rotationDegrees(spinRotation));
				case "Y" -> matrixStack.mulPose(Axis.YP.rotationDegrees(spinRotation));
				default -> matrixStack.mulPose(Axis.ZP.rotationDegrees(spinRotation));
			}
		}
		matrixStack.translate(offsetX, offsetY, offsetZ);
		Minecraft.getInstance().getItemRenderer().renderStatic(weapon, display, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, entity.level(), entity.getId());
		matrixStack.popPose();
		super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(SpearFragmentProjectileEntity entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
