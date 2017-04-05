package ovh.corail.tombstone.handler;

import java.util.UUID;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ovh.corail.tombstone.block.BlockFacing;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.core.ModProps;
import ovh.corail.tombstone.item.ItemGraveKey;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class EventHandler {
	/** Events */
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.getModID().equals(ModProps.MOD_ID)) {
			ConfigurationHandler.refreshConfig();
		}
	}

	@SubscribeEvent
	/** avoid to drop the tombstone key */
	public void itemEvent(ItemTossEvent event) {
		EntityPlayer player = event.getPlayer();
		/** not in creative mode */
		if (player.isCreative()) { return; }
		/** need to be a grave key */
		if (!event.getEntityItem().getEntityItem().getItem().equals(Main.grave_key)) { return; }
		if (event.isCancelable()) {
			if (event.getPlayer().world.isRemote) {
				Helper.sendMessage("item.message.cantDrop", player, true);
			}
			/** need to put the item in inventory */
			player.inventory.addItemStackToInventory(event.getEntityItem().getEntityItem());
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	/** create a tombstone with drops on death */
	public void onLivingDrops(LivingDropsEvent event) {
		/** if it's a player */
		if (!(event.getEntityLiving() instanceof EntityPlayer)) { return; }
		EntityPlayer playerIn = (EntityPlayer) event.getEntityLiving();
		/** not in creative mode and no rule of keep inventory on death */
		if (playerIn.world.getGameRules().getBoolean("keepInventory") || playerIn.isCreative()) { return; }
		/** no drop or too much drops */
		if (event.getDrops().size() <= 0 || event.getDrops().size() > 45) {	return; }
		/** create the tombstone */
		//buildTombstone(event, playerIn);
		playerIn.addStat(AchievementHandler.getAchievement("firstTomb"), 1);
	}

	@SubscribeEvent
	/** change experience loss depending on config */
	public void give(PlayerEvent.Clone event) {
		/** if player was dead and not in creative mode */
		if (event.isWasDeath() && !event.getEntityPlayer().isCreative()) {
			/** calcul of experience */
			if (!ConfigurationHandler.xpLoss) {
				event.getEntityPlayer().experienceTotal = event.getOriginal().experienceTotal;
			} else {
				event.getEntityPlayer().experienceTotal = (int) Math.floor(event.getOriginal().experienceTotal * (100 - ConfigurationHandler.percentXpLoss) / 100);
			}
			int[] result = Helper.calculXp(event.getEntityPlayer().experienceTotal);
			event.getEntityPlayer().experienceLevel = result[0];
			event.getEntityPlayer().experience = (float) result[1] / result[2];
		}
	}

	@SubscribeEvent
	/** remove experience balls */
	public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			event.setCanceled(true);
		}
	}

	public void buildTombstone(LivingDropsEvent event, EntityPlayer playerIn) {
		// TODO
		/** world dimension */
		World world = event.getEntityLiving().world;
		/** surface */
		BlockPos currentPos = event.getEntityLiving().getPosition();
		/** go up to find air */
		while (!Helper.isSafeBlock(world, currentPos)) {
			currentPos = currentPos.up();
		}
		world.setBlockState(currentPos, Main.tombstone.getDefaultState().withProperty(BlockFacing.FACING, event.getEntityLiving().getHorizontalFacing().getOpposite()));
		TileEntityTombstone tile = (TileEntityTombstone) world.getTileEntity(currentPos);
		/** owner infos */
		UUID tombId = playerIn.getUniqueID();
		//tile.setTombId(tombId);
		String ownerName = (event.getEntityLiving().hasCustomName() ? event.getEntityLiving().getCustomNameTag() : event.getEntityLiving().getName());
		//tile.setOwnerName(ownerName);
		//tile.setOwnerDeathDate(new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()));
		/** fill tombstone with items except xp balls */
		/** reverse the inventory (equipable first) */
		int i;
		for (i = event.getDrops().size() - 1; i >= 0; i--) {
			ItemStack stack = event.getDrops().get(i).getEntityItem();
			if (!stack.isEmpty()) {
				EntityItem n = event.getDrops().get(i);
				n.setEntityItemStack(Helper.addToInventoryWithLeftover(stack, tile, false));
				event.getDrops().remove(i);
			}
		}
		/** access are for players only */
		if (ConfigurationHandler.tombAccess && event.getEntityLiving() instanceof EntityPlayer) {
			//tile.setNeedAccess(true);
			ItemStack stack = new ItemStack(Main.grave_key, 1, 0);
			ItemGraveKey.setOwner(stack, tombId, ownerName, currentPos, world.provider.getDimension());
			playerIn.inventory.setInventorySlotContents(playerIn.inventory.getFirstEmptyStack(), stack);
		} else {
			//tile.setNeedAccess(false);
		}
	}
}
