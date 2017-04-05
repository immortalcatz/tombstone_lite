package ovh.corail.tombstone.handler;

import net.minecraftforge.common.config.Configuration;
import ovh.corail.tombstone.core.Helper;

public class ConfigurationHandler {
	public static Configuration config;
	public static boolean tombAccess, upgradeTombKey, uniqueTeleport, xpLoss;
	public static String ingredientToUpgradeKey;
	public static int percentXpLoss;

	public static void refreshConfig() {
		tombAccess = config.getBoolean("tombAccess", Configuration.CATEGORY_GENERAL, true, Helper.getTranslation("config.tombAccess"));
		upgradeTombKey = config.getBoolean("upgradeTombKey", Configuration.CATEGORY_GENERAL, true, Helper.getTranslation("config.upgradeTombKey"));
		uniqueTeleport = config.getBoolean("uniqueTeleport", Configuration.CATEGORY_GENERAL, false, Helper.getTranslation("config.uniqueTeleport"));
		xpLoss = config.getBoolean("xpLoss", Configuration.CATEGORY_GENERAL, false, Helper.getTranslation("config.xpLoss"));
		percentXpLoss = config.getInt("percentXpLoss", Configuration.CATEGORY_GENERAL, 0, 0, 100, Helper.getTranslation("config.percentXpLoss"));
		ingredientToUpgradeKey = config.getString("ingredientToUpgradeKey", Configuration.CATEGORY_GENERAL, "minecraft:ender_pearl:1", Helper.getTranslation("config.ingredientToUpgradeKey"));
		if (config.hasChanged()) {
			config.save();
		}
	}
}
