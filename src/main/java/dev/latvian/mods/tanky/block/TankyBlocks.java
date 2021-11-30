package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.Tanky;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface TankyBlocks {
	DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, Tanky.MOD_ID);

	Supplier<Block> IRON_TANK_CONTROLLER = REGISTRY.register("iron_tank_controller", () -> new TankControllerBlock(16));
	Supplier<Block> IRON_TANK_WALL = REGISTRY.register("iron_tank_wall", () -> new TankWallBlock(16));
	Supplier<Block> IRON_TANK_GLASS = REGISTRY.register("iron_tank_glass", () -> new TankGlassBlock(16));
	Supplier<Block> STEEL_TANK_CONTROLLER = REGISTRY.register("steel_tank_controller", () -> new TankControllerBlock(32));
	Supplier<Block> STEEL_TANK_WALL = REGISTRY.register("steel_tank_wall", () -> new TankWallBlock(32));
	Supplier<Block> STEEL_TANK_GLASS = REGISTRY.register("steel_tank_glass", () -> new TankGlassBlock(32));
}