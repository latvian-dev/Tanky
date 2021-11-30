package dev.latvian.mods.tanky.item;

import dev.latvian.mods.tanky.Tanky;
import dev.latvian.mods.tanky.block.TankyBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface TankyItems {
	DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Tanky.MOD_ID);

	static Supplier<BlockItem> blockItem(String id, Supplier<Block> sup) {
		return REGISTRY.register(id, () -> new BlockItem(sup.get(), new Item.Properties().tab(Tanky.TAB)));
	}

	Supplier<BlockItem> IRON_TANK_CONTROLLER = blockItem("iron_tank_controller", TankyBlocks.IRON_TANK_CONTROLLER);
	Supplier<BlockItem> IRON_TANK_WALL = blockItem("iron_tank_wall", TankyBlocks.IRON_TANK_WALL);
	Supplier<BlockItem> IRON_TANK_GLASS = blockItem("iron_tank_glass", TankyBlocks.IRON_TANK_GLASS);
	Supplier<BlockItem> STEEL_TANK_CONTROLLER = blockItem("steel_tank_controller", TankyBlocks.STEEL_TANK_CONTROLLER);
	Supplier<BlockItem> STEEL_TANK_WALL = blockItem("steel_tank_wall", TankyBlocks.STEEL_TANK_WALL);
	Supplier<BlockItem> STEEL_TANK_GLASS = blockItem("steel_tank_glass", TankyBlocks.STEEL_TANK_GLASS);
}
