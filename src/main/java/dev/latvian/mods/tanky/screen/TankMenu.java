package dev.latvian.mods.tanky.screen;

import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class TankMenu extends AbstractContainerMenu {
	public final TankControllerBlockEntity entity;
	public final ContainerData containerData;

	public TankMenu(int id, Inventory playerInv, TankControllerBlockEntity e, ContainerData d) {
		super(TankyMenus.TANK.get(), id);
		entity = e;
		containerData = d;
	}

	public TankMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (TankControllerBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(3));
	}

	@Override
	public ItemStack quickMoveStack(Player player, int i) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
