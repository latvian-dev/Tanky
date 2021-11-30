package dev.latvian.mods.tanky.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class TankControllerBlockEntity extends BlockEntity implements TankEntityLookup, Predicate<FluidStack> {
	public final FluidTank tank;
	private LazyOptional<FluidTank> tankOptional;
	public FluidStack fluidLock;
	public int radius;
	public int height;

	public TankControllerBlockEntity() {
		super(TankyBlockEntities.TANK_CONTROLLER.get());
		tank = new FluidTank(0, this);
		tankOptional = null;
		fluidLock = FluidStack.EMPTY;
		radius = 0;
		height = 0;
	}

	public void writeData(CompoundTag tag) {
		tag.putInt("Radius", radius);
		tag.putInt("Height", height);
		tag.put("Tank", tank.writeToNBT(new CompoundTag()));
	}

	public void readData(CompoundTag tag) {
		radius = tag.getInt("Radius");
		height = tag.getInt("Height");
		resizeCapacity();
		tank.readFromNBT(tag.getCompound("Tank"));
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
	public boolean test(FluidStack stack) {
		return stack.isEmpty() || fluidLock.isEmpty() || stack.isFluidEqual(fluidLock);
	}

	@Override
	public TankControllerBlockEntity getController() {
		return this;
	}

	public void resize() {
		radius = 0;
		height = 0;

		// scan for tank blocks

		resizeCapacity();
		sync();
	}

	public void sync() {
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
		setChanged();
	}

	public void resizeCapacity() {
		tank.setCapacity(0);

		if (height > 0) {
			tank.setCapacity((height + 1) * (radius * 2 + 1) * (radius * 2 + 1) * FluidAttributes.BUCKET_VOLUME * 16);
		}
	}

	public void rightClick(Player player) {
		if (!level.isClientSide()) {
			player.displayClientMessage(new TextComponent(tank.getFluidAmount() + " / " + tank.getCapacity()), true);
		}
	}
}
