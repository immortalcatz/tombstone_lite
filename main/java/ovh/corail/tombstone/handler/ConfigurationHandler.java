package ovh.corail.tombstone.handler;

import java.io.File;
import java.util.stream.Stream;

import net.minecraftforge.common.config.Configuration;
import ovh.corail.tombstone.block.BlockGrave.GraveType;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.ModProps;
import scala.Int;

public class ConfigurationHandler {
	public static Configuration config;
	private static File configDir;
	public static String lastFavoriteGrave;
	public static boolean handlePlayerDeath, tombAccess, upgradeTombKey, xpLoss, highlight, teleportDim, showFog, pvpMode;
	public static String ingredientToUpgradeKey, favoriteGrave;
	public static int percentXpLoss, decayTime, scrollOfRecallUseCount, chanceSoul, textColorRIP, textColorOwner, textColorDeathDate, snifferRange;
	
	private ConfigurationHandler() {
	}

	public static void refreshConfig() {
		handlePlayerDeath = config.getBoolean("handlePlayerDeath", Configuration.CATEGORY_GENERAL, true, Helper.getTranslation("config.handlePlayerDeath"));
		String[] allowValues = Stream.of(GraveType.values()).map(GraveType::name).toArray(String[]::new);
		favoriteGrave = config.getString("favoriteGrave", Configuration.CATEGORY_CLIENT, GraveType.TOMBSTONE.toString(), Helper.getTranslation("config.favoriteGrave"), allowValues);
		tombAccess = config.getBoolean("tombAccess", Configuration.CATEGORY_GENERAL, true, Helper.getTranslation("config.tombAccess"));
		upgradeTombKey = config.getBoolean("upgradeTombKey", Configuration.CATEGORY_GENERAL, true, Helper.getTranslation("config.upgradeTombKey"));
		xpLoss = config.getBoolean("xpLoss", Configuration.CATEGORY_GENERAL, false, Helper.getTranslation("config.xpLoss"));
		percentXpLoss = config.getInt("percentXpLoss", Configuration.CATEGORY_GENERAL, 0, 0, 100, Helper.getTranslation("config.percentXpLoss"));
		ingredientToUpgradeKey = config.getString("ingredientToUpgradeKey", Configuration.CATEGORY_GENERAL, "minecraft:ender_pearl:1", Helper.getTranslation("config.ingredientToUpgradeKey"));
		decayTime = config.getInt("decayTime", Configuration.CATEGORY_GENERAL, -1, -1, Int.MaxValue(), Helper.getTranslation("config.decayTime"));
		highlight = config.getBoolean("highlight", Configuration.CATEGORY_CLIENT, true, Helper.getTranslation("config.highlight"));
		teleportDim = config.getBoolean("teleportDim", Configuration.CATEGORY_GENERAL, true, Helper.getTranslation("config.teleportDim"));
		showFog = config.getBoolean("showFog", Configuration.CATEGORY_CLIENT, true, Helper.getTranslation("config.showFog"));
		pvpMode = config.getBoolean("pvpMode", Configuration.CATEGORY_GENERAL, false, Helper.getTranslation("config.pvpMode"));
		scrollOfRecallUseCount = config.getInt("scrollOfRecallUseCount", Configuration.CATEGORY_GENERAL, 3, 1, 10, Helper.getTranslation("config.scrollOfRecallUseCount"));
		chanceSoul = config.getInt("chanceSoul", Configuration.CATEGORY_GENERAL, 3000, 1000, 5000, Helper.getTranslation("config.chanceSoul"));
		textColorRIP = config.getInt("textColorRIP", Configuration.CATEGORY_CLIENT, 2962496, 0, 16777215, Helper.getTranslation("config.textColorRIP"));
		textColorOwner = config.getInt("textColorOwner", Configuration.CATEGORY_CLIENT, 5991302, 0, 16777215, Helper.getTranslation("config.textColorOwner"));
		textColorDeathDate = config.getInt("textColorDeathDate", Configuration.CATEGORY_CLIENT, 2962496, 0, 16777215, Helper.getTranslation("config.textColorDeathDate"));
		snifferRange = config.getInt("snifferRange", Configuration.CATEGORY_GENERAL, 5, 0, 10, Helper.getTranslation("config.snifferRange"));
		if (config.hasChanged()) {
			config.save();
		}
	}

	public static void loadConfig(File configDir) {
		ConfigurationHandler.configDir= configDir;
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		config = new Configuration(new File(configDir, ModProps.MOD_ID + ".cfg"), ModProps.MOD_VER);
		config.load();
		ConfigurationHandler.refreshConfig();
		lastFavoriteGrave = favoriteGrave;
	}

	public static File getConfigDir() {
		return configDir;
	}
}
