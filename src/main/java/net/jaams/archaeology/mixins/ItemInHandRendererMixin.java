package net.jaams.archaeology.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ItemInHandRenderer;

import net.jaams.archaeology.util.ModUtils;
import net.jaams.archaeology.util.ModTraits;
import net.jaams.archaeology.util.ModTags;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Shadow
	private void renderItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
	}

	@Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
	private void onRenderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext transformType, boolean leftHand, PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo ci) {
		if (stack.isEmpty())
			return;
		boolean isFirstPerson = transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
		boolean isThirdPerson = transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
		boolean isUsing = entity.isUsingItem() && stack == entity.getUseItem();
		HumanoidArm arm = leftHand ? entity.getMainArm().getOpposite() : entity.getMainArm();
		int hand = (arm == HumanoidArm.RIGHT) ? 1 : -1;
		if (isFirstPerson) {
			if (isUsing) {
				if (shouldRenderDeflect(stack) && ModTraits.isDeflectChargeItem(stack) && stack.getUseAnimation() == UseAnim.BLOCK) {
					applyDeflectChargeFirstPerson(hand, pose);
				} else if (stack.getUseAnimation() == UseAnim.SPEAR && isSpearThrowable(stack)) {
					pose.mulPose(Axis.XP.rotationDegrees(-30.0F));
				}
			}
		}
		if (isThirdPerson) {
			if (isUsing) {
				if (stack.getUseAnimation() == UseAnim.SPEAR && isSpearThrowable(stack)) {
					pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
					pose.translate(0.0F, 0.1F, 0.0F);
				}
			}
			if (stack.is(ModTags.PRIMITIVE_SPEARS)) {
				pose.translate(0.0F, -0.067F, 0.009F);
			}
			if (!isUsing && ModUtils.isEntityInBattleMode(entity) && stack.is(ModTags.AMMONITES)) {
				pose.translate(0.0F, -0.34F, 0.0F);
			}
		}
	}

	private void applyDeflectChargeFirstPerson(int hand, PoseStack pose) {
		pose.translate(hand * -0.00042F, 0.15F, 0.1414F);
		pose.mulPose(Axis.XP.rotationDegrees(-88.25F));
		pose.mulPose(Axis.YP.rotationDegrees(hand * 13.37F));
		pose.mulPose(Axis.ZP.rotationDegrees(hand * 88.05F));
	}

	private boolean shouldRenderDeflect(ItemStack stack) {
		if (!stack.hasTag())
			return true;
		CompoundTag tag = stack.getTag();
		return tag == null || !tag.contains("DeflectChargeRender") || tag.getBoolean("DeflectChargeRender");
	}

	private boolean isSpearThrowable(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return stack.is(ModTags.PRIMITIVE_SPEARS) || stack.is(ModTags.SPEAR_FRAGMENTS) || (tag != null && (tag.contains("ForcePrimitiveSpearThrowable") || tag.contains("ForceSpearFragmentThrowable")));
	}
}
