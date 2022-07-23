package dev.latvian.mods.tanky.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.tanky.Tanky;
import dev.latvian.mods.tanky.block.TankyBlocks;
import dev.latvian.mods.tanky.item.TankyItems;
import dev.latvian.mods.tanky.util.TankyUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
		var gen = event.getGenerator();
		var efh = event.getExistingFileHelper();

		if (event.includeServer()) {
			gen.addProvider(true, new TankyRecipes(gen));
			gen.addProvider(true, new TankyLootTableProvider(gen));
			gen.addProvider(true, new TankyBlockTagProvider(gen, efh));
		}
	}

	private static class TankyRecipes extends RecipeProvider {
		public TankyRecipes(DataGenerator arg) {
			super(arg);
		}

		@Override
		protected final void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
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

			TagKey<Item> steelIngot = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:ingots/steel"));

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

	private static class TankyLootTableProvider extends LootTableProvider {
		private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTables = Lists.newArrayList(Pair.of(TankyBlockLootTableProvider::new, LootContextParamSets.BLOCK));

		public TankyLootTableProvider(DataGenerator dataGeneratorIn) {
			super(dataGeneratorIn);
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
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

	private static class TankyBlockTagProvider extends BlockTagsProvider {
		public TankyBlockTagProvider(DataGenerator gen, ExistingFileHelper efh) {
			super(gen, Tanky.MOD_ID, efh);
		}

		@Override
		protected void addTags() {
			var ironTankBlocks = TankyUtils.getAllBlocks(TankyBlocks.IRON_TANK_CONTROLLER, TankyBlocks.IRON_TANK_WALL, TankyBlocks.IRON_TANK_GLASS);
			var steelTankBlocks = TankyUtils.getAllBlocks(TankyBlocks.STEEL_TANK_CONTROLLER, TankyBlocks.STEEL_TANK_WALL, TankyBlocks.STEEL_TANK_GLASS);

			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ironTankBlocks).add(steelTankBlocks);
			tag(BlockTags.NEEDS_STONE_TOOL).add(ironTankBlocks);
			tag(BlockTags.NEEDS_IRON_TOOL).add(steelTankBlocks);
		}
	}
}
