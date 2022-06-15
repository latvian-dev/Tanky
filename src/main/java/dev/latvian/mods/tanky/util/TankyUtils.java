package dev.latvian.mods.tanky.util;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class TankyUtils {

	@SafeVarargs
	public static Block[] getAllBlocks(Supplier<Block>... inputs) {
		var array = new Block[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			array[i] = inputs[i].get();
		}
		return array;
	}

	public static void init() {
	}
}
