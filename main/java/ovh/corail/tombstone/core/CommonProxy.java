package ovh.corail.tombstone.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import ovh.corail.tombstone.handler.AchievementHandler;
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

			List<ItemStack> inputList = new ArrayList<ItemStack>();
			inputList.add(new ItemStack(Main.grave_key, 1));
			inputList.add(ingredient);
			RecipeSorter.register("tombstone:upgradeKey", UpgradeGraveKeyRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
			GameRegistry.addRecipe(new UpgradeGraveKeyRecipe(res, inputList));
		}
		/** decorative_grave_simple recipe */
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Main.decorative_grave_simple, 1),	new Object[] { " 0 ", "010", 
			Character.valueOf('0'), "stone",
			Character.valueOf('1'), Items.SKULL, 
		}));
		/** decorative_grave_normal recipe */
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Main.decorative_grave_normal, 1),	new Object[] { " 0 ", " 0 ", "010", 
			Character.valueOf('0'), "stone",
			Character.valueOf('1'), Items.SKULL, 
		}));
		/** decorative_grave_cross recipe */
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Main.decorative_grave_cross, 1),	new Object[] { " 0 ", "010", " 0 ", 
			Character.valueOf('0'), "stone",
			Character.valueOf('1'), Items.SKULL, 
		}));
		/** decorative_tombstone recipe */
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Main.decorative_tombstone, 1),	new Object[] { " 0 ", "010", "000", 
			Character.valueOf('0'), "stone",
			Character.valueOf('1'), Items.SKULL, 
		}));
		/** scroll of recall */
		GameRegistry.addShapelessRecipe(new ItemStack(Main.scroll_of_recall, 1, 0),
			Items.PAPER,
			new ItemStack(Items.DYE, 1, 15)
		);
		/** packet handler */
		PacketHandler.init();
	}

	public void init(FMLInitializationEvent event) {
		/** achievements */
		AchievementHandler.registerAchievements();
	}

	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public Side getSide() {
		return Side.CLIENT;
	}
	
    public WorldServer getWorldServer(int dimId) {
        return DimensionManager.getWorld(dimId);
    }
	
}
