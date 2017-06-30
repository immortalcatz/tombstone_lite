package ovh.corail.tombstone.core;

import java.io.File;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.PacketHandler;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		/** config */
		ConfigurationHandler.loadConfig(new File(event.getModConfigurationDirectory(), ModProps.MOD_ID));
		Main.whitelistFile = new File(ConfigurationHandler.getConfigDir(), "whitelist_blocks.json");
		Main.whitelist = Helper.loadWhitelist(Main.whitelistFile);
		/** register tileentities */
		GameRegistry.registerTileEntity(TileEntityTombstone.class, "inventoryTombstone");
		/** register items and blocks */
		Helper.register();
		/** new crafting recipes */
		/** recipe to upgrade the tomb's key */
		ResourceLocation group = new ResourceLocation(ModProps.MOD_ID, "recipes");
		if (ConfigurationHandler.upgradeTombKey) {
			String[] parts = ConfigurationHandler.ingredientToUpgradeKey.split(":");
			ItemStack ingredient;
			if (parts.length == 4) {
				ingredient = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1])), Integer.valueOf(parts[2]), Integer.valueOf(parts[3]));
			} else if (parts.length == 3) {
				ingredient = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1])), Integer.valueOf(parts[2]));
			} else {
				ingredient = new ItemStack(Items.ENDER_PEARL, 1);
			}
			ItemStack res = new ItemStack(Main.grave_key);
			res.setTagCompound(new NBTTagCompound());
			res.getTagCompound().setBoolean("enchant", true);

			NonNullList<Ingredient> inputList = NonNullList.create();
			inputList.add(Ingredient.fromItem(Main.grave_key));
			inputList.add(Ingredient.fromStacks(ingredient));
			RecipeSorter.register(ModProps.MOD_ID + ":upgrade_key", UpgradeGraveKeyRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
			UpgradeGraveKeyRecipe recipe_upgrade_key = new UpgradeGraveKeyRecipe(group, res, inputList);
			recipe_upgrade_key.setRegistryName(new ResourceLocation(ModProps.MOD_ID, "upgrade_key"));
			ForgeRegistries.RECIPES.register(recipe_upgrade_key);
		}
		/** decorative_grave_simple recipe */
		ShapedOreRecipe recipe_grave_simple = new ShapedOreRecipe(group, new ItemStack(Main.decorative_grave_simple, 1),	new Object[] { " 0 ", "010", 
			Character.valueOf('0'), "stone",
			Character.valueOf('1'), Items.SKULL, 
		});
		recipe_grave_simple.setRegistryName(new ResourceLocation(ModProps.MOD_ID, "recipe_grave_simple"));
		ForgeRegistries.RECIPES.register(recipe_grave_simple);
		/** decorative_grave_normal recipe */
		ShapedOreRecipe recipe_grave_normal = new ShapedOreRecipe(group, new ItemStack(Main.decorative_grave_normal, 1),	new Object[] { " 0 ", " 0 ", "010", 
			Character.valueOf('0'), "stone",
			Character.valueOf('1'), Items.SKULL, 
		});
		recipe_grave_normal.setRegistryName(new ResourceLocation(ModProps.MOD_ID, "recipe_grave_normal"));
		ForgeRegistries.RECIPES.register(recipe_grave_normal);
		/** decorative_grave_cross recipe */
		ShapedOreRecipe recipe_grave_cross = new ShapedOreRecipe(group, new ItemStack(Main.decorative_grave_cross, 1),	new Object[] { " 0 ", "010", " 0 ", 
			Character.valueOf('0'), "stone",
			Character.valueOf('1'), Items.SKULL, 
		});
		recipe_grave_cross.setRegistryName(new ResourceLocation(ModProps.MOD_ID, "recipe_grave_cross"));
		ForgeRegistries.RECIPES.register(recipe_grave_cross);
		/** decorative_tombstone recipe */
		ShapedOreRecipe recipe_tombstone = new ShapedOreRecipe(group, new ItemStack(Main.decorative_tombstone, 1),	new Object[] { " 0 ", "010", "000", 
			Character.valueOf('0'), "stone",
			Character.valueOf('1'), Items.SKULL, 
		});
		recipe_tombstone.setRegistryName(new ResourceLocation(ModProps.MOD_ID, "recipe_tombstone"));
		ForgeRegistries.RECIPES.register(recipe_tombstone);
		/** scroll of recall */
		ShapelessOreRecipe recipe_scroll_recall = new ShapelessOreRecipe(group, new ItemStack(Main.scroll_of_recall, 1, 0),
			Items.PAPER,
			new ItemStack(Items.DYE, 1, 15)
		);
		recipe_scroll_recall.setRegistryName(new ResourceLocation(ModProps.MOD_ID, "recipe_scroll_recall"));
		ForgeRegistries.RECIPES.register(recipe_scroll_recall);
		/** packet handler */
		PacketHandler.init();
	}

	public void init(FMLInitializationEvent event) {
		/** advancements */		
		/** TODO register advancements */
	}

	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public Side getSide() {
		return Side.SERVER;
	}

	public void produceTombstoneParticles(BlockPos currentPos) {
		
	}
	
}
