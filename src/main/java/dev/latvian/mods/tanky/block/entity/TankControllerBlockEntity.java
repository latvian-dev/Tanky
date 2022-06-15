package dev.latvian.mods.tanky.block.entity;

import dev.latvian.mods.tanky.TankyConfig;
import dev.latvian.mods.tanky.block.TankBlock;
import dev.latvian.mods.tanky.block.TankWallBlock;
import dev.latvian.mods.tanky.util.TankTier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TankControllerBlockEntity extends BlockEntity implements TankEntityLookup {
	public final ControllerFluidTank tank;
	private LazyOptional<FluidTank> tankOptional;
	public FluidStack fluidLock;
	public int radius;
	public int height;
	public int formCooldown;

	public TankControllerBlockEntity(BlockPos pos, BlockState state) {
		super(TankyBlockEntities.TANK_CONTROLLER.get(), pos, state);
		tank = new ControllerFluidTank(this);
		tankOptional = null;
		fluidLock = FluidStack.EMPTY;
		radius = 0;
		height = 0;
		formCooldown = 0;
	}

	public void writeData(CompoundTag tag) {
		tag.putInt("Radius", radius);
		tag.putInt("Height", height);
		tag.putInt("Capacity", tank.getCapacity());
		tag.put("Tank", tank.writeToNBT(new CompoundTag()));
		tag.putInt("FormCooldown", formCooldown);
	}

	public void readData(CompoundTag tag) {
		radius = tag.getInt("Radius");
		height = tag.getInt("Height");
		tank.setCapacity(tag.getInt("Capacity"));
		tank.readFromNBT(tag.getCompound("Tank"));
		formCooldown = tag.getInt("FormCooldown");
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		readData(tag);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		writeData(tag);
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		readData(tag);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return Util.make(super.getUpdateTag(), this::writeData);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		readData(pkt.getTag());
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public LazyOptional<?> getTankOptional() {
		if (tankOptional == null) {
			tankOptional = LazyOptional.of(() -> tank);
		}

		return tankOptional;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();

		if (tankOptional != null) {
			tankOptional.invalidate();
			tankOptional = null;
		}
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return getTankOptional().cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public TankControllerBlockEntity getController() {
		return this;
	}

	private static boolean isAir(BlockState state) {
		return state.isAir() || (!state.getMaterial().blocksMotion() && !state.getMaterial().isLiquid());
	}

	@Nullable
	private TankWallBlockEntity getWall(TankTier tier, BlockState state, BlockPos pos) {
		if (state.getBlock() instanceof TankWallBlock wall && wall.tier == tier) {
			if (level.getBlockEntity(pos) instanceof TankWallBlockEntity wallBe &&
					(wallBe.controllerPos == null || wallBe.controllerPos.equals(worldPosition))) {
				return wallBe;
			}
		}

		return null;
	}

	private void resize(TankTier tier) {
		int h = 0;
		boolean foundTop = false;

		int maxY = Math.min(TankyConfig.GENERAL.MAX_HEIGHT.get(), level.getHeight() - worldPosition.getY());
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		for (int y = 1; y < maxY; y++) {
			mutablePos.set(worldPosition.getX(), worldPosition.getY() + y, worldPosition.getZ());
			BlockState state = level.getBlockState(mutablePos);

			if (getWall(tier, state, mutablePos) != null) {
				h++;
				foundTop = true;
				break;
			} else if (isAir(state)) {
				h++;
			} else {
				return;
			}
		}

		if (!foundTop || h <= 1) {
			return;
		}

		int r = 0;

		for (int i = 0; i < TankyConfig.GENERAL.MAX_RADIUS.get(); i++) {
			mutablePos.set(worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ() + i);

			if (isAir(level.getBlockState(mutablePos))) {
				r++;
			} else {
				break;
			}
		}

		if (r <= 0) {
			return;
		}

		List<TankWallBlockEntity> walls = new ArrayList<>();

		for (int y = 0; y <= h; y++) {
			for (int x = -r; x <= r; x++) {
				for (int z = -r; z <= r; z++) {
					if (y == 0 && x == 0 && z == 0) {
						continue;
					}

					mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
					BlockState state = level.getBlockState(mutablePos);

					if (y == 0 || y == h || x == -r || x == r || z == -r || z == r) {
						TankWallBlockEntity wall = getWall(tier, state, mutablePos);

						if (wall != null) {
							walls.add(wall);
						} else {
							return;
						}
					} else if (!isAir(state)) {
						return;
					}
				}
			}
		}

		for (TankWallBlockEntity entity : walls) {
			entity.setControllerPos(worldPosition);
		}

		height = h;
		radius = r;
	}

	public void sync() {
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
		setChanged();
	}

	public void rightClick(Player player, InteractionHand hand) {
		FluidUtil.interactWithFluidHandler(player, hand, tank);

		if (tank.getFluidAmount() == 0) {
			player.displayClientMessage(Component.literal(String.format("0 / %,d", tank.getCapacity())), true);
		} else {
			player.displayClientMessage(Component.literal(String.format("%,d / %,d", tank.getFluidAmount(), tank.getCapacity())).append(" of ").append(tank.getFluid().getDisplayName()), true);
		}
	}

	public void resetTank() {
		if (height > 0) {
			BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

			for (int y = 0; y <= height; y++) {
				for (int x = -radius; x <= radius; x++) {
					for (int z = -radius; z <= radius; z++) {
						mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);

						if (level.getBlockState(mutablePos).getBlock() instanceof TankWallBlock) {
							BlockEntity e = level.getBlockEntity(mutablePos);

							if (e instanceof TankWallBlockEntity wall) {
								wall.setControllerPos(null);
							}
						}
					}
				}
			}
		}

		height = 0;
		radius = 0;
		tank.setCapacity(0);
		formCooldown = 20;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		BlockPos p = worldPosition;
		return new AABB(p.getX() - radius, p.getY(), p.getZ() - radius, p.getX() + radius + 1D, p.getY() + height + 2D, p.getZ() + radius + 1D);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, TankControllerBlockEntity controller) {
		if (!level.isClientSide && controller.height == 0 && controller.formCooldown == 0 && controller.getBlockState().getBlock() instanceof TankBlock tank) {
			controller.radius = 0;

			TankTier tier = tank.tier;
			controller.resize(tier);

			if (controller.height > 0) {
				controller.tank.setCapacity(tier.getCapacity() * (controller.height - 1) * (controller.radius * 2 - 1) * (controller.radius * 2 - 1));
			} else {
				controller.tank.setCapacity(0);
			}

			controller.sync();
		}

		if (controller.formCooldown <= 0) {
			controller.formCooldown = controller.height > 0 ? 1200 : 20;
		}

		controller.formCooldown--;
	}
}
