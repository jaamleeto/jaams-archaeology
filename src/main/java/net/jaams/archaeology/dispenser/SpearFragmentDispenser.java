package net.jaams.archaeology.dispenser;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.Position;

import net.jaams.archaeology.init.JaamsArchaeologyModEntities;
import net.jaams.archaeology.entity.SpearFragmentProjectileEntity;

public class SpearFragmentDispenser extends AbstractProjectileDispenseBehavior {
	@Override
	protected Projectile getProjectile(Level level, Position pos, ItemStack stack) {
		SpearFragmentProjectileEntity entity = new SpearFragmentProjectileEntity(JaamsArchaeologyModEntities.SPEAR_FRAGMENT_PROJECTILE.get(), level);
		entity.weaponItem = stack.copy();
		entity.setPos(pos.x(), pos.y(), pos.z());
		entity.pickup = AbstractArrow.Pickup.ALLOWED;
		return entity;
	}
}
