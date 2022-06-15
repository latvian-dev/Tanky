package dev.latvian.mods.tanky.util;

import dev.latvian.mods.tanky.TankyConfig;

public interface TankTier {

	TankTier IRON = TankyConfig.CAPACITY.IRON_CAPACITY::get;

	TankTier STEEL = TankyConfig.CAPACITY.STEEL_CAPACITY::get;

	int getCapacity();
}
