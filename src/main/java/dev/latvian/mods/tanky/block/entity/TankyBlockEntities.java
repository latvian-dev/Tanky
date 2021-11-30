package dev.latvian.mods.tanky.block.entity;

import dev.latvian.mods.tanky.Tanky;
import dev.latvian.mods.tanky.block.TankyBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.function.Supplier;

public interface TankyBlockEntities {
	DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Tanky.MOD_ID);

	static Supplier<BlockEntityType<?>> register(String id, Supplier<BlockEntity> supplier, Supplier<Block>... block) {
		return REGISTRY.register(id, () -> BlockEntityType.Builder.of(supplier, Arrays.stream(block).map(Supplier::get).toArray(Block[]::new)).build(null));
	}

	Supplier<BlockEntityType<?>> TANK_CONTROLLER = register("tank_controller", TankControllerBlockEntity::new, TankyBlocks.IRON_TANK_CONTROLLER, TankyBlocks.STEEL_TANK_CONTROLLER);
	Supplier<BlockEntityType<?>> TANK_WALL = register("tank_wall", TankControllerBlockEntity::new, TankyBlocks.IRON_TANK_WALL, TankyBlocks.IRON_TANK_GLASS, TankyBlocks.STEEL_TANK_WALL, TankyBlocks.STEEL_TANK_GLASS);
}
