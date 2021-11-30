package dev.latvian.mods.tanky.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.tanky.Tanky;
import dev.latvian.mods.tanky.block.TankyBlocks;
import dev.latvian.mods.tanky.item.TankyItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Tanky.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TankyDataGen {
	public static final String MODID = Tanky.MOD_ID;

	@SubscribeEvent
	public static void dataGenEvent(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper efh = event.getExistingFileHelper();

		if (event.includeClient()) {
			gen.addProvider(new TankyLang(gen, MODID, "en_us"));
			gen.addProvider(new TankyBlockStates(gen, MODID, efh));
			gen.addProvider(new TankyItemModels(gen, MODID, efh));
		}

		if (event.includeServer()) {
			gen.addProvider(new TankyRecipes(gen));
			gen.addProvider(new TankyLootTableProvider(gen));
		}
	}

	private static class TankyLang extends LanguageProvider {
		public TankyLang(DataGenerator gen, String modid, String locale) {
			super(gen, modid, locale);
		}

		@Override
		protected void addTranslations() {
			add("itemGroup.tanky", "Tanky");

			addBlock(TankyBlocks.IRON_TANK_CONTROLLER, "Iron Tank Controller");
			addBlock(TankyBlocks.IRON_TANK_WALL, "Iron Tank Wall");
			addBlock(TankyBlocks.IRON_TANK_GLASS, "Iron Tank Glass");
			addBlock(TankyBlocks.STEEL_TANK_CONTROLLER, "Steel Tank Controller");
			addBlock(TankyBlocks.STEEL_TANK_WALL, "Steel Tank Wall");
			addBlock(TankyBlocks.STEEL_TANK_GLASS, "Steel Tank Glass");

			add("block.tanky.tank_controller.info", "This is a tank controller");
		}
	}

	private static class TankyBlockStates extends BlockStateProvider {
		public TankyBlockStates(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
			super(gen, modid, exFileHelper);
		}

		@Override
		protected void registerStatesAndModels() {
			simpleBlock(TankyBlocks.IRON_TANK_CONTROLLER.get());
			simpleBlock(TankyBlocks.IRON_TANK_WALL.get());
			simpleBlock(TankyBlocks.IRON_TANK_GLASS.get());
			simpleBlock(TankyBlocks.STEEL_TANK_CONTROLLER.get());
			simpleBlock(TankyBlocks.STEEL_TANK_WALL.get());
			simpleBlock(TankyBlocks.STEEL_TANK_GLASS.get());
		}
	}

	private static class TankyItemModels extends ItemModelProvider {
		public TankyItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		private void basicBlockItem(Supplier<Block> block) {
			String id = block.get().getRegistryName().getPath();
			withExistingParent(id, modLoc("block/" + id));
		}

		@Override
		protected void registerModels() {
			basicBlockItem(TankyBlocks.IRON_TANK_CONTROLLER);
			basicBlockItem(TankyBlocks.IRON_TANK_WALL);
			basicBlockItem(TankyBlocks.IRON_TANK_GLASS);
			basicBlockItem(TankyBlocks.STEEL_TANK_CONTROLLER);
			basicBlockItem(TankyBlocks.STEEL_TANK_WALL);
			basicBlockItem(TankyBlocks.STEEL_TANK_GLASS);
		}
	}

	private static class TankyRecipes extends RecipeProvider {
		public TankyRecipes(DataGenerator arg) {
			super(arg);
		}

		@Override
		protected final void buildShapelessRecipes(Consumer<FinishedRecipe> consumer) {
			ShapedRecipeBuilder.shaped(TankyItems.IRON_TANK_WALL.get(), 3)
					.unlockedBy("has_item", has(Tags.Items.INGOTS_IRON))
					.group(MODID + ":iron_tank_wall")
					.pattern("III")
					.pattern("IGI")
					.pattern("III")
					.define('I', Tags.Items.INGOTS_IRON)
					.define('G', Tags.Items.GLASS)
					.save(consumer);

			ShapedRecipeBuilder.shaped(TankyItems.IRON_TANK_CONTROLLER.get())
					.unlockedBy("has_item", has(TankyItems.IRON_TANK_WALL.get()))
					.group(MODID + ":iron_tank_controller")
					.pattern("BHB")
					.pattern("TCT")
					.pattern("BHB")
					.define('T', TankyItems.IRON_TANK_WALL.get())
					.define('H', Items.HOPPER)
					.define('B', Items.BUCKET)
					.define('C', Items.CAULDRON)
					.save(consumer);

			ShapedRecipeBuilder.shaped(TankyItems.IRON_TANK_GLASS.get(), 4)
					.unlockedBy("has_item", has(TankyItems.IRON_TANK_WALL.get()))
					.group(MODID + ":iron_tank_glass")
					.pattern("TGT")
					.pattern("G G")
					.pattern("TGT")
					.define('T', TankyItems.IRON_TANK_WALL.get())
					.define('G', Tags.Items.GLASS)
					.save(consumer);

			Tag<Item> steelIngot = ItemTags.bind("forge:ingots/steel");

			ShapedRecipeBuilder.shaped(TankyItems.STEEL_TANK_WALL.get(), 3)
					.unlockedBy("has_item", has(steelIngot))
					.group(MODID + ":steel_tank_wall")
					.pattern("III")
					.pattern("IGI")
					.pattern("III")
					.define('I', steelIngot)
					.define('G', Tags.Items.GLASS)
					.save(consumer);

			ShapedRecipeBuilder.shaped(TankyItems.STEEL_TANK_CONTROLLER.get())
					.unlockedBy("has_item", has(TankyItems.STEEL_TANK_WALL.get()))
					.group(MODID + ":steel_tank_controller")
					.pattern("BHB")
					.pattern("TCT")
					.pattern("BHB")
					.define('T', TankyItems.STEEL_TANK_WALL.get())
					.define('H', Items.HOPPER)
					.define('B', Items.BUCKET)
					.define('C', Items.CAULDRON)
					.save(consumer);

			ShapedRecipeBuilder.shaped(TankyItems.STEEL_TANK_GLASS.get(), 4)
					.unlockedBy("has_item", has(TankyItems.STEEL_TANK_WALL.get()))
					.group(MODID + ":steel_tank_glass")
					.pattern("TGT")
					.pattern("G G")
					.pattern("TGT")
					.define('T', TankyItems.STEEL_TANK_WALL.get())
					.define('G', Tags.Items.GLASS)
					.save(consumer);
		}
	}

	private static class TankyLootTableProvider extends ForgeLootTableProvider {
		private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTables = Lists.newArrayList(Pair.of(TankyBlockLootTableProvider::new, LootContextParamSets.BLOCK));

		public TankyLootTableProvider(DataGenerator dataGeneratorIn) {
			super(dataGeneratorIn);
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
			return lootTables;
		}
	}

	public static class TankyBlockLootTableProvider extends BlockLoot {
		private final Map<ResourceLocation, LootTable.Builder> tables = Maps.newHashMap();

		@Override
		protected void addTables() {
			dropSelf(TankyBlocks.IRON_TANK_CONTROLLER.get());
			dropSelf(TankyBlocks.IRON_TANK_WALL.get());
			dropSelf(TankyBlocks.IRON_TANK_GLASS.get());
			dropSelf(TankyBlocks.STEEL_TANK_CONTROLLER.get());
			dropSelf(TankyBlocks.STEEL_TANK_WALL.get());
			dropSelf(TankyBlocks.STEEL_TANK_GLASS.get());
		}

		@Override
		public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
			addTables();

			for (ResourceLocation rs : new ArrayList<>(tables.keySet())) {
				if (rs != BuiltInLootTables.EMPTY) {
					LootTable.Builder builder = tables.remove(rs);

					if (builder == null) {
						throw new IllegalStateException(String.format("Missing loottable '%s'", rs));
					}

					consumer.accept(rs, builder);
				}
			}

			if (!tables.isEmpty()) {
				throw new IllegalStateException("Created block loot tables for non-blocks: " + tables.keySet());
			}
		}

		@Override
		protected void add(Block blockIn, LootTable.Builder table) {
			tables.put(blockIn.getLootTable(), table);
		}
	}
}
