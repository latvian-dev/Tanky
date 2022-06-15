package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import dev.latvian.mods.tanky.block.entity.TankWallBlockEntity;
import dev.latvian.mods.tanky.util.TankTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class TankWallBlock extends TankBlock {
	public static final BooleanProperty VALID = BooleanProperty.create("valid");

	public TankWallBlock(Properties p, TankTier tier) {
		super(p, tier);
		registerDefaultState(getStateDefinition().any().setValue(VALID, false));
	}

	public TankWallBlock(TankTier tier) {
		this(Properties.of(Material.METAL).sound(SoundType.METAL), tier);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TankWallBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(VALID);
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean bl) {
		if (!level.isClientSide() && !state.is(state1.getBlock())) {
			BlockEntity entity = level.getBlockEntity(pos);
			TankControllerBlockEntity controller = entity instanceof TankWallBlockEntity wall ? wall.getController() : null;

			super.onRemove(state, level, pos, state1, bl);

			if (controller != null) {
				controller.resetTank();
				controller.sync();
			}
		} else {
			super.onRemove(state, level, pos, state1, bl);
		}
	}
}
