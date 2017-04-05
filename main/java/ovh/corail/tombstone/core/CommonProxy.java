package ovh.corail.tombstone.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import ovh.corail.tombstone.handler.AchievementHandler;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.GuiHandler;
import ovh.corail.tombstone.handler.PacketHandler;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.config = new Configuration(event.getSuggestedConfigurationFile());
		ConfigurationHandler.config.load();
		ConfigurationHandler.refreshConfig();
		/** register items and blocks */
		Helper.register();
		/** register tileentities */
		GameRegistry.registerTileEntity(TileEntityTombstone.class, "inventoryTombstone");
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
			RecipeSorter.register("recycler:upgradeKey", UpgradeGraveKeyRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
			GameRegistry.addRecipe(new UpgradeGraveKeyRecipe(res, inputList));
		}
		/** packet handler */
		PacketHandler.init();
	}

	public void init(FMLInitializationEvent event) {
		/** achievements */
		AchievementHandler.registerAchievements();
		/** gui handler */
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
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
