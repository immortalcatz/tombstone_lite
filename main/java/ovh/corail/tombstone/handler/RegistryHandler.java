package ovh.corail.tombstone.handler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import ovh.corail.tombstone.block.BlockGrave;
import ovh.corail.tombstone.block.ItemBlockGrave;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;

@Mod.EventBusSubscriber
public class RegistryHandler {
	private static IForgeRegistry<Item> itemsRegistry = null;
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> blocksRegistry = event.getRegistry();
		blocksRegistry.register(Main.decorative_grave_simple);
		blocksRegistry.register(Main.decorative_grave_normal);
		blocksRegistry.register(Main.decorative_grave_cross);
		blocksRegistry.register(Main.decorative_tombstone);
		blocksRegistry.register(Main.grave_simple);
		blocksRegistry.register(Main.decorative_grave_normal);
		blocksRegistry.register(Main.grave_cross);
		blocksRegistry.register(Main.tombstone);
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void registerItems(RegistryEvent.Register<Item> event) {
		itemsRegistry = event.getRegistry();
		/** register itemblocks */
		registerItemBlocks(Main.decorative_grave_simple);
		registerItemBlocks(Main.decorative_grave_normal);
		registerItemBlocks(Main.decorative_grave_cross);
		registerItemBlocks(Main.decorative_tombstone);
		/** register items */
		registerItems(Main.grave_key);
		registerItems(Main.fake_fog);
		registerItems(Main.soul);
		registerItems(Main.scroll_of_recall);
		/** register achievement icon items */
		registerItems(Main.itemAchievement001);
		registerItems(Main.itemAchievement002);
		registerItems(Main.itemAchievement003);
	}
	
	private static void registerItemBlocks(BlockGrave grave) {
		if (itemsRegistry != null) {
			ItemBlockGrave itemBlock = new ItemBlockGrave(grave);
			itemsRegistry.register(itemBlock.setRegistryName(grave.getRegistryName()));
			if (Main.proxy.getSide() == Side.CLIENT) {
				itemBlock.initModel();
			}
		}
	}
	
	private static void registerItems(Item item) {
		if (itemsRegistry != null) {
			itemsRegistry.register(item);
			if (Main.proxy.getSide() == Side.CLIENT) {
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		Helper.registerEncodedRecipes();
	}
	
}
