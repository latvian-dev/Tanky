package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.Tanky;
import dev.latvian.mods.tanky.util.TankTier;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface TankyBlocks {
	DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, Tanky.MOD_ID);

	Supplier<Block> IRON_TANK_CONTROLLER = REGISTRY.register("iron_tank_controller", () -> new TankControllerBlock(TankTier.IRON));
	Supplier<Block> IRON_TANK_WALL = REGISTRY.register("iron_tank_wall", () -> new TankWallBlock(TankTier.IRON));
	Supplier<Block> IRON_TANK_GLASS = REGISTRY.register("iron_tank_glass", () -> new TankGlassBlock(TankTier.IRON));
	Supplier<Block> STEEL_TANK_CONTROLLER = REGISTRY.register("steel_tank_controller", () -> new TankControllerBlock(TankTier.STEEL));
	Supplier<Block> STEEL_TANK_WALL = REGISTRY.register("steel_tank_wall", () -> new TankWallBlock(TankTier.STEEL));
	Supplier<Block> STEEL_TANK_GLASS = REGISTRY.register("steel_tank_glass", () -> new TankGlassBlock(TankTier.STEEL));
}