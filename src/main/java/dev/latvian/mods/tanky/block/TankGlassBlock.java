package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.util.TankTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TankGlassBlock extends TankWallBlock {
	public TankGlassBlock(TankTier tier) {
		super(Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isViewBlocking((arg, arg2, arg3) -> false).isRedstoneConductor((arg, arg2, arg3) -> false).isSuffocating((arg, arg2, arg3) -> false), tier);
	}

	@Override
	@Deprecated
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return Shapes.empty();
	}

	@Override
	@Deprecated
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	@Deprecated
	public boolean skipRendering(BlockState state, BlockState state1, Direction direction) {
		return state1.is(this) || super.skipRendering(state, state1, direction);
	}
}
