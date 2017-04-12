package ovh.corail.tombstone.core;

import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ovh.corail.tombstone.handler.PacketHandler;
import ovh.corail.tombstone.packet.TeleportMessage;

/** ref : LimeLib, MrRiegel */
public class TeleportationHelper {

	public static void teleportToDim(EntityPlayer player, WorldServer target, BlockPos pos) {
		teleportToDim(player, target.provider.getDimension(), pos);
	}

	public static void teleportToPosAndUpdate(Entity e, BlockPos pos) {
		e.setPositionAndUpdate(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5);
	}

	public static void teleportToPos(Entity e, BlockPos pos) {
		e.setPositionAndRotation(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5, e.rotationYaw, e.rotationPitch);
	}

	public static boolean canTeleport(Entity e) {
		return e != null && !e.world.isRemote && e.isEntityAlive() && !e.isBeingRidden() && e.isNonBoss() && !e.isRiding() && (e instanceof EntityLivingBase || e instanceof EntityItem);
	}

	/** nicked from brandonscore */
	@Deprecated
	public static void teleportToDim(Entity entity, int newDimension, BlockPos pos) {
		if (!canTeleport(entity)) { return; }
		int oldDimension = entity.world.provider.getDimension();
		if (oldDimension == newDimension) { return; }
		MinecraftServer server = entity.world.getMinecraftServer();
		WorldServer oldWorld = server.worldServerForDimension(oldDimension);
		WorldServer newWorld = server.worldServerForDimension(newDimension);

		if (newWorld == null || newWorld.getMinecraftServer() == null) {
			throw new IllegalArgumentException("Dimension: " + newDimension + " doesn't exist!");
		}

		oldWorld.updateEntityWithOptionalForce(entity, false);

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.closeScreen();
			player.addExperienceLevel(0);
			player.dimension = newWorld.provider.getDimension();
			player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
			oldWorld.getPlayerChunkMap().removePlayer(player);
			oldWorld.playerEntities.remove(player);
			oldWorld.updateAllPlayersSleepingFlag();
			int i = player.chunkCoordX;
			int j = player.chunkCoordZ;
			if ((player.addedToChunk) && (oldWorld.getChunkFromChunkCoords(i, j)).isPopulated()) {
				oldWorld.getChunkFromChunkCoords(i, j).removeEntity(player);
				oldWorld.getChunkFromChunkCoords(i, j).setModified(true);
			}
			oldWorld.loadedEntityList.remove(player);
			oldWorld.onEntityRemoved(player);
		}

		newWorld.getChunkProvider().loadChunk(pos.getX() >> 4, pos.getZ() >> 4);

		newWorld.profiler.startSection("placing");
		if (!(entity instanceof EntityPlayer)) {
			NBTTagCompound entityNBT = new NBTTagCompound();
			entity.isDead = false;
			entityNBT.setString("id", EntityList.getEntityString(entity));
			entity.writeToNBT(entityNBT);
			entity.isDead = true;
			entity = EntityList.createEntityFromNBT(entityNBT, newWorld);
			if (entity == null) {
				throw new IllegalArgumentException("Failed to teleport entity to new location");
			}
			entity.dimension = newWorld.provider.getDimension();
		}
		boolean flag = entity.forceSpawn;
		entity.forceSpawn = true;
		int entsize = newWorld.loadedEntityList.size(), plsize = newWorld.playerEntities.size();
		newWorld.spawnEntity(entity);
		entity.forceSpawn = flag;
		entity.setWorld(newWorld);
		teleportToPosAndUpdate(entity, pos);

		if ((entity instanceof EntityPlayerMP)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.mcServer.getPlayerList().preparePlayer(player, newWorld);
			player.connection.setPlayerLocation(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5, player.rotationYaw, player.rotationPitch);
		}

		newWorld.updateEntityWithOptionalForce(entity, false);

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.interactionManager.setWorld(newWorld);
			player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, newWorld);
			player.mcServer.getPlayerList().syncPlayerInventory(player);

			for (PotionEffect potionEffect : player.getActivePotionEffects()) {
				player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potionEffect));
			}

			player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
			FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldWorld.provider.getDimension(), newWorld.provider.getDimension());
		}
		teleportToPosAndUpdate(entity, pos);
		newWorld.profiler.endSection();
		newWorld.loadEntities(Sets.newHashSet(entity));
		entity.fallDistance = 0;
		if (entity instanceof EntityPlayerMP) {
			PacketHandler.INSTANCE.sendTo(new TeleportMessage(), (EntityPlayerMP) entity);
		}
	}

	public static class TeleporterEIO extends Teleporter {

		public TeleporterEIO(WorldServer world) {
			super(world);
		}

		@Override
		public boolean makePortal(Entity entity) {
			return true;
		}

		@Override
		public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
			return true;
		}

		@Override
		public void placeInPortal(Entity entity, float rotationYaw) {
			int x = MathHelper.floor(entity.posX);
			int y = MathHelper.floor(entity.posY) - 1;
			int z = MathHelper.floor(entity.posZ);

			entity.setLocationAndAngles(x, y, z, entity.rotationPitch, entity.rotationYaw);
			entity.motionX = 0;
			entity.motionY = 0;
			entity.motionZ = 0;
		}

		@Override
		public void removeStalePortalLocations(long worldTime) {
			
		}
	}
	
}