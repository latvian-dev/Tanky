package dev.latvian.mods.tanky.screen;

import dev.latvian.mods.tanky.Tanky;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface TankyMenus {
	DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, Tanky.MOD_ID);

	static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(String id, IContainerFactory<T> factory) {
		return REGISTRY.register(id, () -> new MenuType<>(factory));
	}

	Supplier<MenuType<TankMenu>> TANK = register("tank", TankMenu::new);
}
