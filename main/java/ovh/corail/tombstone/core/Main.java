package ovh.corail.tombstone.core;

import static ovh.corail.tombstone.core.ModProps.MC_ACCEPT;
import static ovh.corail.tombstone.core.ModProps.MOD_ID;
import static ovh.corail.tombstone.core.ModProps.MOD_NAME;
import static ovh.corail.tombstone.core.ModProps.MOD_UPDATE;
import static ovh.corail.tombstone.core.ModProps.MOD_VER;
import static ovh.corail.tombstone.core.ModProps.ROOT;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import nightkosh.gravestone.api.GraveStoneAPI;
import ovh.corail.tombstone.block.BlockGrave;
import ovh.corail.tombstone.block.BlockGrave.GraveType;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.DeathHandler;
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
import ovh.corail.tombstone.tileentity.TileEntityWritableGrave;

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VER, acceptedMinecraftVersions = MC_ACCEPT, updateJSON = MOD_UPDATE, guiFactory = ROOT + ".gui.GuiFactory", dependencies="before:gravestone;before:GraveStone")
public class Main {
	@Instance(MOD_ID)
	public static Main instance;
	@SidedProxy(clientSide = ROOT + ".core.ClientProxy", serverSide = ROOT + ".core.CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
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
	public static BlockGrave grave_simple = new BlockGrave("grave_normal", GraveType.GRAVE_NORMAL, false);
	public static BlockGrave grave_normal = new BlockGrave("grave_simple", GraveType.GRAVE_SIMPLE, false);
	public static BlockGrave grave_cross = new BlockGrave("grave_cross", GraveType.GRAVE_CROSS, false);
	public static BlockGrave tombstone = new BlockGrave("tombstone", GraveType.TOMBSTONE, false);
	public static ItemGraveKey grave_key = new ItemGraveKey();
	public static ItemFakeFog fake_fog = new ItemFakeFog();
	public static ItemSoul soul = new ItemSoul();
	public static ItemScrollOfRecall scroll_of_recall = new ItemScrollOfRecall();
	public static BlockGrave decorative_grave_normal = new BlockGrave("decorative_grave_normal", GraveType.GRAVE_NORMAL, true);
	public static BlockGrave decorative_grave_simple = new BlockGrave("decorative_grave_simple", GraveType.GRAVE_SIMPLE, true);
	public static BlockGrave decorative_grave_cross = new BlockGrave("decorative_grave_cross", GraveType.GRAVE_CROSS, true);
	public static BlockGrave decorative_tombstone = new BlockGrave("decorative_tombstone", GraveType.TOMBSTONE, true);

	public static ItemAdvancement001 itemAchievement001 = new ItemAdvancement001();
	public static ItemAdvancement002 itemAchievement002 = new ItemAdvancement002();
	public static ItemAdvancement003 itemAchievement003 = new ItemAdvancement003();
	
	public static File whitelistFile;
	public static Set<String> whitelist = new HashSet<String>();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		PacketHandler.init();
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		if (proxy.getSide() == Side.SERVER) {
			DeathHandler.getInstance();
		}
		SoundHandler.registerSounds();
		/** config */
		ConfigurationHandler.loadConfig(new File(event.getModConfigurationDirectory(), ModProps.MOD_ID));
		Main.whitelistFile = new File(ConfigurationHandler.getConfigDir(), "whitelist_blocks.json");
		Main.whitelist = Helper.loadWhitelist(Main.whitelistFile);
		/** register tileentities */
		GameRegistry.registerTileEntity(TileEntityWritableGrave.class, "writable_grave");
		GameRegistry.registerTileEntity(TileEntityTombstone.class, "tombstone");
		/** packet handler */
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
		/** register loot tables */
		LootTableList.register(new ResourceLocation(MOD_ID, "decorative_graves"));
		/** compatibility with NighKosh Grave Mod */
		if (Loader.isModLoaded("GraveStone") || Loader.isModLoaded("gravestone")) {
			if (ConfigurationHandler.handlePlayerDeath) {
				if (GraveStoneAPI.graveGenerationAtDeath != null) {
					GraveStoneAPI.graveGenerationAtDeath.addPlayerDeathHandler((player, source) -> { return true; });
				}
			}
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}
