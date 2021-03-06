package dev.latvian.mods.tanky.util;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface TankyUtils {
	@SafeVarargs
	static Block[] getAllBlocks(Supplier<Block>... inputs) {
		var array = new Block[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			array[i] = inputs[i].get();
		}
		return array;
	}
}
