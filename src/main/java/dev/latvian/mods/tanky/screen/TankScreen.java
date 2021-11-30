package dev.latvian.mods.tanky.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TankScreen extends AbstractContainerScreen<TankMenu> {
	public TankScreen(TankMenu arg, Inventory arg2, Component arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	protected void renderBg(PoseStack arg, float f, int i, int j) {

	}
}
