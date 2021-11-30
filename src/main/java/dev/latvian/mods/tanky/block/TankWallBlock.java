package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import dev.latvian.mods.tanky.block.entity.TankWallBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class TankWallBlock extends TankBlock {
	public TankWallBlock(int buckets) {
		super(Properties.of(Material.METAL).sound(SoundType.METAL), buckets);
	}

	public TankWallBlock(Properties p, int buckets) {
		super(p, buckets);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new TankWallBlockEntity();
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean bl) {
		BlockEntity entity = level.getBlockEntity(pos);
		TankControllerBlockEntity controller = entity instanceof TankWallBlockEntity ? ((TankWallBlockEntity) entity).getController() : null;

		super.onRemove(state, level, pos, state1, bl);

		if (controller != null) {
			controller.resize();
		}
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean bl) {
		super.onPlace(state, level, pos, state1, bl);

		BlockEntity entity = level.getBlockEntity(pos);
		TankControllerBlockEntity controller = entity instanceof TankWallBlockEntity ? ((TankWallBlockEntity) entity).findController() : null;

		if (controller != null) {
			controller.resize();
		}
	}
}
