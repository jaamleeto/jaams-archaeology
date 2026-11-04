
package net.jaams.archaeology.entity;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;

import net.jaams.archaeology.init.JaamsArchaeologyModItems;
import net.jaams.archaeology.init.JaamsArchaeologyModEntities;

import javax.annotation.Nullable;

public class AmmoniteProjectileEntity extends AbstractArrow implements IEntityAdditionalSpawnData {
	private static final EntityDataAccessor<Boolean> ID_IMPACTED = SynchedEntityData.defineId(AmmoniteProjectileEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> ID_SPIN_TICKS = SynchedEntityData.defineId(AmmoniteProjectileEntity.class, EntityDataSerializers.INT);
	public ItemStack ammoniteItem;
	private int bubbleTime = 0;
	@Nullable
	private BlockState lastState;
	private boolean dealtDamage;
	private final SoundEvent breakSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("jaams_archaeology:ammonite_breaks"));
	private final SoundEvent shootSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.arrow.shoot"));

	public AmmoniteProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(JaamsArchaeologyModEntities.AMMONITE_PROJECTILE.get(), world);
		initializeAmmoniteItem();
	}

	public AmmoniteProjectileEntity(EntityType<? extends AmmoniteProjectileEntity> type, Level world) {
		super(type, world);
		initializeAmmoniteItem();
	}

	public AmmoniteProjectileEntity(EntityType<? extends AmmoniteProjectileEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
		initializeAmmoniteItem();
	}

	public AmmoniteProjectileEntity(EntityType<? extends AmmoniteProjectileEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
		initializeAmmoniteItem();
	}

	public AmmoniteProjectileEntity(Level world, LivingEntity shooter, ItemStack ammoniteItem) {
		super(JaamsArchaeologyModEntities.AMMONITE_PROJECTILE.get(), shooter, world);
		this.ammoniteItem = ammoniteItem.copy();
	}

	private void initializeAmmoniteItem() {
		this.ammoniteItem = new ItemStack(JaamsArchaeologyModItems.PETRIFIED_AMMONITE.get());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ID_IMPACTED, false);
		this.entityData.define(ID_SPIN_TICKS, 0);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("Weapon", 10)) {
			this.ammoniteItem = ItemStack.of(tag.getCompound("Weapon"));
		} else {
			this.ammoniteItem = ItemStack.EMPTY;
		}
		this.dealtDamage = tag.getBoolean("DealtDamage");
		this.entityData.set(ID_IMPACTED, tag.getBoolean("HasImpacted"));
		this.entityData.set(ID_SPIN_TICKS, tag.getInt("SpinTicks"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Weapon", this.ammoniteItem.save(new CompoundTag()));
		tag.putBoolean("DealtDamage", this.dealtDamage);
		tag.putBoolean("HasImpacted", this.entityData.get(ID_IMPACTED));
		tag.putInt("SpinTicks", this.entityData.get(ID_SPIN_TICKS));
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeItem(this.ammoniteItem);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		this.ammoniteItem = additionalData.readItem();
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected @NotNull ItemStack getPickupItem() {
		ItemStack itemStack = this.ammoniteItem.copy();
		itemStack.setCount(1);
		return itemStack;
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.EMPTY;
	}

	@Override
	public int getPortalWaitTime() {
		return 72000;
	}

	public boolean isFoil() {
		return false;
	}

	public boolean hasImpacted() {
		return this.entityData.get(ID_IMPACTED);
	}

	public int getSpinTicks() {
		return this.entityData.get(ID_SPIN_TICKS);
	}

	public static AmmoniteProjectileEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 1f, 5, 5);
	}

	public static AmmoniteProjectileEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		AmmoniteProjectileEntity entityarrow = new AmmoniteProjectileEntity(JaamsArchaeologyModEntities.AMMONITE_PROJECTILE.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		if (entityarrow.shootSound != null) {
			world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), entityarrow.shootSound, SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		}
		return entityarrow;
	}

	public static AmmoniteProjectileEntity shoot(LivingEntity entity, LivingEntity target) {
		AmmoniteProjectileEntity entityarrow = new AmmoniteProjectileEntity(JaamsArchaeologyModEntities.AMMONITE_PROJECTILE.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 1f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(5);
		entityarrow.setKnockback(5);
		entityarrow.setCritArrow(false);
		entity.level().addFreshEntity(entityarrow);
		if (entityarrow.shootSound != null) {
			entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), entityarrow.shootSound, SoundSource.PLAYERS, 1, 1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		}
		return entityarrow;
	}
}
