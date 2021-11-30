package dev.latvian.mods.tanky.block;

import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import dev.latvian.mods.tanky.block.entity.TankEntityLookup;
import dev.latvian.mods.tanky.util.TankTier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolType;

public class TankBlock extends Block {
	public final TankTier tier;

	public TankBlock(Properties arg, TankTier b) {
		super(arg.strength(3F, 4F).harvestTool(ToolType.PICKAXE));
		tier = b;
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
		TankControllerBlockEntity controller = entity instanceof TankEntityLookup ? ((TankEntityLookup) entity).getController() : null;

		if (controller != null) {
			controller.rightClick(player, hand);
		} else {
			player.displayClientMessage(new TextComponent("Invalid tank!"), true);
		}

		return InteractionResult.SUCCESS;
	}
}
