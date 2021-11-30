package dev.latvian.mods.tanky.util;

public enum TankTier {
	IRON(16),
	STEEL(32);

	public final int buckets;

	TankTier(int c) {
		buckets = c;
	}
}
