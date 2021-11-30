package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class TankControllerBlock extends TankBlock {
	public TankControllerBlock(int buckets) {
		super(Properties.of(Material.METAL).sound(SoundType.METAL), buckets);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new TankControllerBlockEntity();
	}

	@Override
	@Deprecated
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean bl) {
		super.onPlace(state, level, pos, state1, bl);

		BlockEntity entity = level.getBlockEntity(pos);

		if (entity instanceof TankControllerBlockEntity) {
			((TankControllerBlockEntity) entity).resize();
		}
	}
}
