
package net.jaams.archaeology.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;

import net.jaams.archaeology.util.ModProjectiles;
import net.jaams.archaeology.init.JaamsArchaeologyModItems;
import net.jaams.archaeology.init.JaamsArchaeologyModEntities;
import net.jaams.archaeology.configuration.common.ProjectileCommonConfig;
import net.jaams.archaeology.component.BaseWeaponProjectileEntity;

public class PrimitiveSpearProjectileEntity extends BaseWeaponProjectileEntity {
	public PrimitiveSpearProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(JaamsArchaeologyModEntities.PRIMITIVE_SPEAR_PROJECTILE.get(), world);
		initializeProperties();
	}

	public PrimitiveSpearProjectileEntity(EntityType<? extends PrimitiveSpearProjectileEntity> type, Level world) {
		super(type, world);
		initializeProperties();
	}

	public PrimitiveSpearProjectileEntity(EntityType<? extends PrimitiveSpearProjectileEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
		initializeProperties();
	}

	public PrimitiveSpearProjectileEntity(EntityType<? extends PrimitiveSpearProjectileEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
		initializeProperties();
	}

	public PrimitiveSpearProjectileEntity(Level world, LivingEntity entity, ItemStack weaponItem) {
		super(JaamsArchaeologyModEntities.PRIMITIVE_SPEAR_PROJECTILE.get(), entity, world, weaponItem);
		initializeProperties();
	}

	private void initializeProperties() {
		this.setWeaponBaseDamage(getBaseDamageFromWeapon());
		this.setWeaponBaseKnockback(getBaseKnockbackFromWeapon());
		this.setPierceLevel(ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_PIERCING_LEVEL.get());
		refreshDimensions();
	}

	private float getBaseDamageFromWeapon() {
		ItemStack weapon = this.getWeaponItem();
		if (weapon.hasTag() && weapon.getTag().contains("BaseDamage", 99)) {
			return weapon.getTag().getFloat("BaseDamage");
		}
		return ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_BASE_DAMAGE.get().floatValue();
	}

	private float getBaseKnockbackFromWeapon() {
		ItemStack weapon = this.getWeaponItem();
		if (weapon.hasTag() && weapon.getTag().contains("BaseKnockback", 99)) {
			return weapon.getTag().getFloat("BaseKnockback");
		}
		return ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_BASE_KNOCKBACK.get().floatValue();
	}

	private int getDespawnTicksFromWeapon() {
		ItemStack weapon = this.getWeaponItem();
		if (weapon.hasTag() && weapon.getTag().contains("DespawnTicks", 99)) {
			int value = weapon.getTag().getInt("DespawnTicks");
			return Math.max(1, value);
		}
		return Math.max(1, (int) ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_DESPAWN_TIME.get().doubleValue());
	}

	private float getWaterInertiaFromWeapon() {
		ItemStack weapon = this.getWeaponItem();
		if (weapon.hasTag() && weapon.getTag().contains("WaterInertia", 99)) {
			return weapon.getTag().getFloat("WaterInertia");
		}
		return ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_WATER_INERTIA.get().floatValue();
	}

	private boolean getAllowCriticalsFromWeapon() {
		ItemStack weapon = this.getWeaponItem();
		if (weapon.hasTag() && weapon.getTag().contains("AllowCriticals")) {
			return weapon.getTag().getBoolean("AllowCriticals");
		}
		return ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_ALLOW_CRITICALS.get();
	}

	private int getIgnoreHitTicksFromWeapon() {
		ItemStack weapon = this.getWeaponItem();
		if (weapon.hasTag() && weapon.getTag().contains("IgnoreHitTicks", 99)) {
			int value = weapon.getTag().getInt("IgnoreHitTicks");
			return Math.max(0, value);
		}
		return ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_IGNORE_TICKS.get();
	}

	private boolean useGenericProjectileSounds() {
		ItemStack weapon = this.getWeaponItem();
		return weapon.hasTag() && weapon.getTag().getBoolean("GenericSoundSet");
	}

	private SoundEvent getSound(String genericId, String specificId, SoundEvent fallback) {
		String soundId = useGenericProjectileSounds() ? genericId : specificId;
		SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("jaams_archaeology:" + soundId));
		return sound != null ? sound : fallback;
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		ItemStack weapon = this.getWeaponItem();
		float width = ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_HITBOX_WIDTH.get().floatValue();
		float height = ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_HITBOX_HEIGHT.get().floatValue();
		if (weapon.hasTag()) {
			CompoundTag tag = weapon.getTag();
			if (tag.contains("HitboxWidth", 99)) {
				width = tag.getFloat("HitboxWidth");
			}
			if (tag.contains("HitboxHeight", 99)) {
				height = tag.getFloat("HitboxHeight");
			}
		}
		return EntityDimensions.scalable(width, height);
	}

	@Override
	public ItemStack getDefaultWeaponItem() {
		return new ItemStack(JaamsArchaeologyModItems.PRIMITIVE_SPEAR.get());
	}

	@Override
	protected SoundEvent getHitSound() {
		return getSound("projectile_hit", "primitive_spear_hit", SoundEvents.TRIDENT_HIT);
	}

	@Override
	protected SoundEvent getGroundSound() {
		return getSound("projectile_ground", "primitive_spear_ground", SoundEvents.TRIDENT_HIT_GROUND);
	}

	@Override
	protected SoundEvent getLoyaltySound() {
		return getSound("projectile_return", "primitive_spear_return", SoundEvents.TRIDENT_RETURN);
	}

	@Override
	protected void tickDespawn() {
		life++;
		int despawnTime = getDespawnTicksFromWeapon();
		if (life >= despawnTime) {
			if (ProjectileCommonConfig.PRIMITIVE_SPEAR_PROJECTILE_DESPAWN_AS_ITEM.get() && this.pickup != AbstractArrow.Pickup.CREATIVE_ONLY) {
				ModProjectiles.dropAsItem(this.level(), this.getPickupItem().copy(), this.getX(), this.getY(), this.getZ());
			} else if (!this.level().isClientSide) {
				ItemStack itemStack = this.weaponItem.copy();
				if (!itemStack.isEmpty()) {
					((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), getX(), getY() + 0.5, getZ(), 5, 0.1d, 0.1d, 0.1d, 0.05d);
				}
			}
			discard();
		}
	}

	@Override
	protected float getWaterInertia() {
		return getWaterInertiaFromWeapon();
	}

	@Override
	public boolean isCritical() {
		if (!getAllowCriticalsFromWeapon()) {
			return false;
		}
		return super.isCritical();
	}

	@Override
	public int getIgnoreHitTicks() {
		return getIgnoreHitTicksFromWeapon();
	}
}
