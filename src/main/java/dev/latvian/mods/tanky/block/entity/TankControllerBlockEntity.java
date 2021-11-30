package dev.latvian.mods.tanky.block.entity;

import dev.latvian.mods.tanky.TankyConfig;
import dev.latvian.mods.tanky.block.TankBlock;
import dev.latvian.mods.tanky.block.TankWallBlock;
import dev.latvian.mods.tanky.util.TankTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TankControllerBlockEntity extends BlockEntity implements TickableBlockEntity, TankEntityLookup {
	public final ControllerFluidTank tank;
	private LazyOptional<FluidTank> tankOptional;
	public FluidStack fluidLock;
	public int radius;
	public int height;
	public int formCooldown;

	public TankControllerBlockEntity() {
		super(TankyBlockEntities.TANK_CONTROLLER.get());
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
	public void load(BlockState state, CompoundTag tag) {
		super.load(state, tag);
		readData(tag);
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		writeData(tag);
		return tag;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		readData(tag);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		writeData(tag);
		return tag;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		readData(pkt.getTag());
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		writeData(tag);
		return new ClientboundBlockEntityDataPacket(worldPosition, 0, tag);
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
	public void tick() {
		if (height == 0 && level != null && formCooldown == 0 && !level.isClientSide() && getBlockState().getBlock() instanceof TankBlock) {
			height = 0;
			radius = 0;

			TankTier tier = ((TankBlock) getBlockState().getBlock()).tier;
			resize(tier);

			if (height > 0) {
				tank.setCapacity(FluidAttributes.BUCKET_VOLUME * tier.buckets * (height + 1) * (radius * 2 + 1) * (radius * 2 + 1));
			} else {
				tank.setCapacity(0);
			}

			sync();
		}

		if (formCooldown <= 0) {
			formCooldown = height > 0 ? 1200 : 20;
		}

		formCooldown--;
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
		if (state.getBlock() instanceof TankWallBlock && ((TankWallBlock) state.getBlock()).tier == tier) {
			BlockEntity e = level.getBlockEntity(pos);

			if (e instanceof TankWallBlockEntity && (((TankWallBlockEntity) e).controllerPos == null || ((TankWallBlockEntity) e).controllerPos.equals(worldPosition))) {
				return (TankWallBlockEntity) e;
			}
		}

		return null;
	}

	private void resize(TankTier tier) {
		int h = 0;

		int maxY = Math.min(TankyConfig.MAX_HEIGHT, level.getHeight() - worldPosition.getY());
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		for (int y = 1; y < maxY; y++) {
			mutablePos.set(worldPosition.getX(), worldPosition.getY() + y, worldPosition.getZ());
			BlockState state = level.getBlockState(mutablePos);

			if (getWall(tier, state, mutablePos) != null) {
				h++;
				break;
			} else if (isAir(state)) {
				h++;
			} else {
				return;
			}
		}

		if (h <= 1) {
			return;
		}

		int r = 0;

		for (int i = 0; i < TankyConfig.MAX_RADIUS; i++) {
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
			player.displayClientMessage(new TextComponent(String.format("0 / %,d", tank.getCapacity())), true);
		} else {
			player.displayClientMessage(new TextComponent(String.format("%,d / %,d", tank.getFluidAmount(), tank.getCapacity())).append(" of ").append(tank.getFluid().getDisplayName()), true);
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

							if (e instanceof TankWallBlockEntity) {
								((TankWallBlockEntity) e).setControllerPos(null);
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
}
