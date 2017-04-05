package ovh.corail.tombstone.core;

import static ovh.corail.tombstone.core.ModProps.MOD_ID;
import static ovh.corail.tombstone.core.ModProps.MOD_NAME;
import static ovh.corail.tombstone.core.ModProps.MOD_VER;

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
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ovh.corail.tombstone.block.BlockDecorativeGraveCross;
import ovh.corail.tombstone.block.BlockDecorativeGraveNormal;
import ovh.corail.tombstone.block.BlockDecorativeGraveSimple;
import ovh.corail.tombstone.block.BlockDecorativeTombstone;
import ovh.corail.tombstone.block.BlockTombstone;
import ovh.corail.tombstone.handler.AchievementHandler;
import ovh.corail.tombstone.handler.CommandHandler;
import ovh.corail.tombstone.handler.EventHandler;
import ovh.corail.tombstone.handler.SoundHandler;
import ovh.corail.tombstone.item.ItemAchievement001;
import ovh.corail.tombstone.item.ItemAchievement002;
import ovh.corail.tombstone.item.ItemGraveKey;

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VER, guiFactory = "ovh.corail." + MOD_ID + ".gui.GuiFactory")
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
	public static BlockDecorativeGraveNormal decorative_grave_normal = new BlockDecorativeGraveNormal();
	public static BlockDecorativeGraveSimple decorative_grave_simple = new BlockDecorativeGraveSimple();
	public static BlockDecorativeGraveCross decorative_grave_cross = new BlockDecorativeGraveCross();
	public static BlockDecorativeTombstone decorative_tombstone = new BlockDecorativeTombstone();

	public static ItemAchievement001 itemAchievement001 = new ItemAchievement001();
	public static ItemAchievement002 itemAchievement002 = new ItemAchievement002();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		AchievementHandler.initAchievements();
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		SoundHandler.registerSounds();
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandHandler());
	}

}
