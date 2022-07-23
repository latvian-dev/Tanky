package dev.latvian.mods.tanky.block.entity;

import dev.latvian.mods.tanky.Tanky;
import dev.latvian.mods.tanky.block.TankyBlocks;
import dev.latvian.mods.tanky.util.TankyUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface TankyBlockEntities {
	DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Tanky.MOD_ID);

	@SafeVarargs
	static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String id, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block>... block) {
		return REGISTRY.register(id, () -> BlockEntityType.Builder.of(supplier, TankyUtils.getAllBlocks(block)).build(null));
	}

	Supplier<BlockEntityType<TankControllerBlockEntity>> TANK_CONTROLLER = register("tank_controller", TankControllerBlockEntity::new, TankyBlocks.IRON_TANK_CONTROLLER, TankyBlocks.STEEL_TANK_CONTROLLER);
	Supplier<BlockEntityType<TankWallBlockEntity>> TANK_WALL = register("tank_wall", TankWallBlockEntity::new, TankyBlocks.IRON_TANK_WALL, TankyBlocks.IRON_TANK_GLASS, TankyBlocks.STEEL_TANK_WALL, TankyBlocks.STEEL_TANK_GLASS);
}
