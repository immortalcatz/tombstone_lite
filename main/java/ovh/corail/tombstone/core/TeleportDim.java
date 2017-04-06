package ovh.corail.tombstone.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/** ref Blood Magic */
public class TeleportDim extends Teleporter {
	
	private static TeleportDim instance;

	private TeleportDim(WorldServer worldIn) {
		super(worldIn);
	}
	
	/** singleton pattern */
	public static TeleportDim getInstance() {
        if (instance == null) {
            instance = new TeleportDim(Main.proxy.getWorldServer(0));
        }
        return instance;
    }
	
	public boolean teleport(Entity entity, BlockPos newPos, int newDimId) {
		if (entity.world.isRemote) { return false; }
		if (entity.dimension == newDimId) {
		/** same dimension */
			((EntityPlayerMP)entity).setPositionAndUpdate(newPos.getX() + .5, newPos.getY() + 1.05, newPos.getZ() + .5);
		} else {
		/** different dimension */
			this.teleportDimensional(entity, newPos, newDimId);
		}
		entity.timeUntilPortal = 50;
		Main.proxy.getWorldServer(0).resetUpdateEntityTick();
		return true;
	}
	
	@Override
	/** don't create the portal */
	public void placeInPortal(Entity entityIn, float rotationYaw) {
		
	}

	@Override
	/** portal always exists */
	public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
		return true;
	}
	
	@Override
	/** to avoid creating a portal */
	public boolean makePortal(Entity entityIn) {
		return true;
	}
	
	@Override
	/** to avoid remove of a portal */
	public void removeStalePortalLocations(long worldTime) {
		
	}
	
	private void teleportDimensional(Entity entity, BlockPos pos, int newDimId) {
		int oldDimId = entity.dimension;
        WorldServer oldWorldserver = Main.proxy.getWorldServer(oldDimId);
        WorldServer newWorldserver = Main.proxy.getWorldServer(newDimId);
        if (entity instanceof EntityPlayer) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            float playerRotationYaw = player.rotationYaw;

            player.dismountRidingEntity();
            PlayerList playerList = player.mcServer.getPlayerList();
            player.dimension = newDimId;

            /** Removing the player from the old world */
            player.connection.sendPacket(new SPacketRespawn(player.dimension, newWorldserver.getDifficulty(), newWorldserver.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
            playerList.updatePermissionLevel(player);
            oldWorldserver.removeEntityDangerously(player);
            player.isDead = false;

            /** Placing the player in the new world */
            oldWorldserver.profiler.startSection("moving");
            player.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, playerRotationYaw, player.rotationPitch);
            oldWorldserver.profiler.endSection();

            oldWorldserver.profiler.startSection("placing");
            if (player.isEntityAlive()) {
                player.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, playerRotationYaw, player.rotationPitch);
                player.motionX = 0;
                player.motionZ = 0;
                newWorldserver.spawnEntity(player);
                newWorldserver.updateEntityWithOptionalForce(player, false);
            }
            oldWorldserver.profiler.endSection();

            player.setWorld(newWorldserver);

            /** Sync the client */
            playerList.preparePlayer(player, oldWorldserver);
            player.connection.setPlayerLocation(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, playerRotationYaw, player.rotationPitch);
            player.interactionManager.setWorld(newWorldserver);
            player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
            playerList.updateTimeAndWeatherForPlayer(player, newWorldserver);
            playerList.syncPlayerInventory(player);
            for (PotionEffect potioneffect : player.getActivePotionEffects()) {
                player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
            }
            net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDimId, newDimId);
        }
        oldWorldserver.resetUpdateEntityTick();
        newWorldserver.resetUpdateEntityTick();
    }
}
