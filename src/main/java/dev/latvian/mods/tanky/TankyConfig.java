package dev.latvian.mods.tanky;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class TankyConfig {
	public static void init() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TankyConfig.COMMON_CONFIG);
	}

	private static final ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec COMMON_CONFIG;

	public static final General GENERAL;
	public static final Capacity CAPACITY;

	static {
		GENERAL = new General();
		CAPACITY = new Capacity();
		COMMON_CONFIG = COMMON.build();
	}

	public static class General {
		public final ForgeConfigSpec.IntValue MAX_HEIGHT;

		public final ForgeConfigSpec.IntValue MAX_RADIUS;

		public General() {
			COMMON.comment("General settings related to tanks").push("General");
			MAX_HEIGHT = COMMON.comment("Maximum height of the tank").defineInRange("max_height", 9, 1, 32);
			MAX_RADIUS = COMMON.comment("Maximum radius of the tank").defineInRange("max_radius", 24, 1, 32);
			COMMON.pop();
		}

	}

	public static class Capacity {
		public final ForgeConfigSpec.IntValue IRON_CAPACITY;
		public final ForgeConfigSpec.IntValue STEEL_CAPACITY;

		public Capacity() {
			COMMON.comment("Defines the capacity per block of each builtin tank tier").push("Capacity");
			IRON_CAPACITY = COMMON.comment("Base capacity in mB of iron tanks.").defineInRange("iron_capacity", 16000, 1, Integer.MAX_VALUE);
			STEEL_CAPACITY = COMMON.comment("Base capacity in mB of steel tanks.").defineInRange("steel_capacity", 32000, 1, Integer.MAX_VALUE);
			COMMON.pop();
		}
	}
}
