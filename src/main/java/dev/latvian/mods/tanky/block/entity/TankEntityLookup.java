package dev.latvian.mods.tanky.block.entity;

import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.jetbrains.annotations.Nullable;

public interface TankEntityLookup extends IForgeTileEntity {
	@Nullable
	TankControllerBlockEntity getController();
}
