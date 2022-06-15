package dev.latvian.mods.tanky;

import dev.latvian.mods.tanky.block.TankyBlocks;
import dev.latvian.mods.tanky.block.entity.TankyBlockEntities;
import dev.latvian.mods.tanky.client.TankyClient;
import dev.latvian.mods.tanky.item.TankyItems;
import dev.latvian.mods.tanky.screen.TankyMenus;
import dev.latvian.mods.tanky.util.TankyUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Tanky.MOD_ID)
public class Tanky {
	public static final String MOD_ID = "tanky";
	public static final String MOD_NAME = "Tanky";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static TankyCommon PROXY;

	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(TankyItems.IRON_TANK_GLASS.get());
		}
	};

	public Tanky() {
		PROXY = DistExecutor.safeRunForDist(() -> TankyClient::new, () -> TankyCommon::new);
		TankyBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		TankyItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		TankyBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		TankyMenus.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		TankyConfig.init();
		PROXY.init();
	}
}
