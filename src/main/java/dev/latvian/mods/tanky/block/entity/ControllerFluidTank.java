package dev.latvian.mods.tanky.block.entity;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ControllerFluidTank extends FluidTank {
	public final TankControllerBlockEntity entity;

	public ControllerFluidTank(TankControllerBlockEntity e) {
		super(0);
		entity = e;
	}

	@Override
	public boolean isFluidValid(FluidStack stack) {
		return stack.isEmpty() || entity.fluidLock.isEmpty() || stack.isFluidEqual(entity.fluidLock);
	}

	@Override
	protected void onContentsChanged() {
		entity.sync();
	}
}
