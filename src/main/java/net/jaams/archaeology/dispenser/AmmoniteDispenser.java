package net.jaams.archaeology.dispenser;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.Position;

import net.jaams.archaeology.init.JaamsArchaeologyModEntities;
import net.jaams.archaeology.entity.AmmoniteProjectileEntity;

public class AmmoniteDispenser extends AbstractProjectileDispenseBehavior {
	@Override
	protected Projectile getProjectile(Level level, Position pos, ItemStack stack) {
		AmmoniteProjectileEntity entity = new AmmoniteProjectileEntity(JaamsArchaeologyModEntities.AMMONITE_PROJECTILE.get(), level);
		entity.ammoniteItem = stack.copy();
		entity.setPos(pos.x(), pos.y(), pos.z());
		return entity;
	}
}
