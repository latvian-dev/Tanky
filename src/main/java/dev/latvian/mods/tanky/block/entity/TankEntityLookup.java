package dev.latvian.mods.tanky.block.entity;

import org.jetbrains.annotations.Nullable;

public interface TankEntityLookup {
	@Nullable
	TankControllerBlockEntity getController();

	@Nullable
	default TankControllerBlockEntity findController() {
		// pathfind controller

		return getController();
	}
}
