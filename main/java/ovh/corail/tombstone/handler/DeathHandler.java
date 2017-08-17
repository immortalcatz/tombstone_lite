package ovh.corail.tombstone.handler;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ovh.corail.tombstone.core.Main;

public class DeathHandler {
	private static final DeathHandler instance = new DeathHandler();
	public static enum GraveType { GRAVE_SIMPLE, GRAVE_NORMAL, GRAVE_CROSS, TOMBSTONE }
	private HashMap<UUID, GraveType> favorite_graves = new HashMap<UUID, GraveType>();
	
	private DeathHandler() {}
	
	public static DeathHandler getInstance() {
		return instance;
	}
	
	public void setFavoriteGrave(UUID id, GraveType type) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		PlayerList playerList = server.getPlayerList();
		if (playerList != null) {
			EntityPlayerMP player = playerList.getPlayerByUUID(id);
			if (player != null && type != null) {
				favorite_graves.put(id, type);
			}
		}
	}
	
	public GraveType getFavoriteGrave(UUID id) {
		GraveType type = favorite_graves.get(id);
		return type == null ? GraveType.TOMBSTONE : type;
	}
	
	public Block getFavoriteGraveBlock(UUID id) {
		GraveType graveType = getFavoriteGrave(id);
		switch (graveType) {
			case GRAVE_CROSS:
				return Main.grave_cross;
			case GRAVE_NORMAL:
				return Main.grave_normal;
			case GRAVE_SIMPLE:
				return Main.grave_simple;
			case TOMBSTONE:default:
				return Main.tombstone;
		}
	}
}
