package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import dev.latvian.mods.tanky.block.entity.TankyBlockEntities;
import dev.latvian.mods.tanky.util.TankTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class TankControllerBlock extends TankBlock {
	public TankControllerBlock(TankTier tier) {
		super(Properties.of(Material.METAL).sound(SoundType.METAL), tier);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TankControllerBlockEntity(pos, state);
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean bl) {
		if (!level.isClientSide()) {
			BlockEntity entity = level.getBlockEntity(pos);

			if (entity instanceof TankControllerBlockEntity controller) {
				controller.resetTank();
			}
		}

		super.onRemove(state, level, pos, state1, bl);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState pos, BlockEntityType<T> type) {
		return createTickerHelper(type, TankyBlockEntities.TANK_CONTROLLER.get(), TankControllerBlockEntity::tick);
	}
}
