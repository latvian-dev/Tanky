package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import dev.latvian.mods.tanky.block.entity.TankEntityLookup;
import dev.latvian.mods.tanky.util.TankTier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class TankBlock extends BaseEntityBlock {
	public final TankTier tier;

	public TankBlock(Properties arg, TankTier b) {
		super(arg.strength(3F, 4F).requiresCorrectToolForDrops());
		tier = b;
	}

	@Override
	public RenderShape getRenderShape(BlockState arg) {
		return RenderShape.MODEL;
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (this instanceof TankWallBlock && !state.getValue(TankWallBlock.VALID)) {
			return InteractionResult.PASS;
		} else if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		BlockEntity entity = level.getBlockEntity(pos);
		TankControllerBlockEntity controller = entity instanceof TankEntityLookup lookup ? lookup.getController() : null;

		if (controller != null) {
			controller.rightClick(player, hand);
		} else {
			player.displayClientMessage(Component.literal("Invalid tank!"), true);
		}

		return InteractionResult.SUCCESS;
	}
}
