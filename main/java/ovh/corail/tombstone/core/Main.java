package ovh.corail.tombstone.core;

import static ovh.corail.tombstone.core.ModProps.MOD_ID;
import static ovh.corail.tombstone.core.ModProps.MOD_NAME;
import static ovh.corail.tombstone.core.ModProps.MOD_UPDATE;
import static ovh.corail.tombstone.core.ModProps.MOD_VER;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.tombstone.block.BlockDecorativeGraveCross;
import ovh.corail.tombstone.block.BlockDecorativeGraveNormal;
import ovh.corail.tombstone.block.BlockDecorativeGraveSimple;
import ovh.corail.tombstone.block.BlockDecorativeTombstone;
import ovh.corail.tombstone.block.BlockTombstone;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.EventHandler;
import ovh.corail.tombstone.handler.PacketHandler;
import ovh.corail.tombstone.handler.SoundHandler;
import ovh.corail.tombstone.item.ItemAdvancement001;
import ovh.corail.tombstone.item.ItemAdvancement002;
import ovh.corail.tombstone.item.ItemAdvancement003;
import ovh.corail.tombstone.item.ItemFakeFog;
import ovh.corail.tombstone.item.ItemGraveKey;
import ovh.corail.tombstone.item.ItemScrollOfRecall;
import ovh.corail.tombstone.item.ItemSoul;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VER,  updateJSON = MOD_UPDATE, guiFactory = "ovh.corail." + MOD_ID + ".gui.GuiFactory")
public class Main {
	@Instance(MOD_ID)
	public static Main instance;
	@SidedProxy(clientSide = "ovh.corail." + MOD_ID + ".core.ClientProxy", serverSide = "ovh.corail." + MOD_ID + ".core.CommonProxy")
	public static CommonProxy proxy;
	public static CreativeTabs tabTombstone = new CreativeTabs(MOD_ID) {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Item.getItemFromBlock(Main.decorative_tombstone), 1, 0);
		}

		@Override
		public String getTranslatedTabLabel() {
			return MOD_NAME;
		}
	};
	public static BlockTombstone tombstone = new BlockTombstone();
	public static ItemGraveKey grave_key = new ItemGraveKey();
	public static ItemFakeFog fake_fog = new ItemFakeFog();
	public static ItemSoul soul = new ItemSoul();
	public static ItemScrollOfRecall scroll_of_recall = new ItemScrollOfRecall();
	public static BlockDecorativeGraveNormal decorative_grave_normal = new BlockDecorativeGraveNormal();
	public static BlockDecorativeGraveSimple decorative_grave_simple = new BlockDecorativeGraveSimple();
	public static BlockDecorativeGraveCross decorative_grave_cross = new BlockDecorativeGraveCross();
	public static BlockDecorativeTombstone decorative_tombstone = new BlockDecorativeTombstone();

	public static ItemAdvancement001 itemAchievement001 = new ItemAdvancement001();
	public static ItemAdvancement002 itemAchievement002 = new ItemAdvancement002();
	public static ItemAdvancement003 itemAchievement003 = new ItemAdvancement003();
	
	public static File whitelistFile;
	public static Set<String> whitelist = new HashSet<String>();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		/** TODO init advancements */
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		/** config */
		ConfigurationHandler.loadConfig(new File(event.getModConfigurationDirectory(), ModProps.MOD_ID));
		Main.whitelistFile = new File(ConfigurationHandler.getConfigDir(), "whitelist_blocks.json");
		Main.whitelist = Helper.loadWhitelist(Main.whitelistFile);
		SoundHandler.registerSounds();
		/** register tileentities */
		GameRegistry.registerTileEntity(TileEntityTombstone.class, "inventoryTombstone");
		/** register items and blocks */
		Helper.register();
		/** register encoded recipes */
		Helper.registerEncodedRecipes();
		/** packet handler */
		PacketHandler.init();
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		/** TODO register advancements */
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}
