package ovh.corail.tombstone.handler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.core.ModProps;

public class AchievementHandler {
	private static Map<String, Achievement> achievements = new HashMap<String, Achievement>();
	private static int achievementCount = 0;
	
	public static void initAchievements() {
		addAchievement("firstTomb", 0, 0, Main.itemAchievement001, null);
		addAchievement("upgradedKey", 1, 1, Main.itemAchievement002, "firstTomb");
	}
	
	private static void addAchievement(String name, int col, int row, Item icon, String parent) {
		String upperName = name.substring(0,1).toUpperCase()+name.substring(1);
		String lowerName = name.substring(0,1).toLowerCase()+name.substring(1);
		achievements.put(lowerName, new Achievement("achievement."+upperName, upperName, row, col, icon, achievements.get(parent)));
		achievementCount++;
	}
	
	public static Achievement getAchievement(String name) {
		return achievements.get(name);
	}
	
	public static void registerAchievements() {
		AchievementPage.registerAchievementPage(new AchievementPage(ModProps.MOD_ID, achievements.values().toArray(new Achievement[achievements.values().size()])));
	}
}
