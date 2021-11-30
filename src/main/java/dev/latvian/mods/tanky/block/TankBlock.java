package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import dev.latvian.mods.tanky.block.entity.TankEntityLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidUtil;

public class TankBlock extends Block {
	public final int buckets;

	public TankBlock(Properties arg, int b) {
		super(arg.strength(3F, 4F).harvestTool(ToolType.PICKAXE));
		buckets = b;
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (Block.byItem(player.getItemInHand(hand).getItem()) instanceof TankBlock) {
			return InteractionResult.PASS;
		}

		BlockEntity entity = level.getBlockEntity(pos);
		TankControllerBlockEntity controller = entity instanceof TankEntityLookup ? ((TankEntityLookup) entity).getController() : null;

		if (controller != null) {
			if (FluidUtil.interactWithFluidHandler(player, hand, controller.tank)) {
				return InteractionResult.SUCCESS;
			}
		}

		if (controller == null) {
			controller = entity instanceof TankEntityLookup ? ((TankEntityLookup) entity).findController() : null;

			if (controller != null) {
				controller.resize();
			}
		}

		if (controller != null) {
			controller.rightClick(player);
		}

		return InteractionResult.SUCCESS;
	}
}
