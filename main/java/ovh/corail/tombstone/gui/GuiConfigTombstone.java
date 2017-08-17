package ovh.corail.tombstone.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import ovh.corail.tombstone.core.ModProps;
import ovh.corail.tombstone.handler.ConfigurationHandler;

public class GuiConfigTombstone extends GuiConfig {
	public GuiConfigTombstone(GuiScreen parentScreen) {
		super(parentScreen, new ConfigElement(ConfigurationHandler.config.getCategory(Configuration.CATEGORY_CLIENT)).getChildElements(), ModProps.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
	}
}
