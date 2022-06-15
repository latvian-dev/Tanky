package dev.latvian.mods.tanky.block.entity;

import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.jetbrains.annotations.Nullable;

public interface TankEntityLookup extends IForgeBlockEntity {
	@Nullable
	TankControllerBlockEntity getController();
}
