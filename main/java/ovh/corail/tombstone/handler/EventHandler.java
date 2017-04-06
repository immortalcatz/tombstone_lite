package ovh.corail.tombstone.handler;

import java.util.Date;

import net.minecraft.block.state.IBlockState;
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

	@SubscribeEvent(priority = EventPriority.LOWEST)
	/** avoid to drop the tombstone key */
	public void itemEvent(ItemTossEvent event) {
		EntityPlayer player = event.getPlayer();
		/** not in creative mode */
		if (player.isCreative()) { return; }
		/** need to be a grave key */
		if (!event.getEntityItem().getEntityItem().getItem().equals(Main.grave_key)) { return; }
		if (event.isCancelable()) {
			if (event.getPlayer().world.isRemote && !player.isDead) {
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
		buildTombstone(event, playerIn);
		playerIn.addStat(AchievementHandler.getAchievement("firstTomb"), 1);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	/** change experience loss depending on config */
	public void give(PlayerEvent.Clone event) {
		EntityPlayer player = event.getEntityPlayer();
		EntityPlayer original = event.getOriginal();
		/** if player was dead and not in creative mode */
		if (!event.isWasDeath() || player.isCreative()) { return; }
		/** calcul of experience */
		if (!ConfigurationHandler.xpLoss) {
			player.experienceTotal = event.getOriginal().experienceTotal;
		} else {
			player.experienceTotal = (int) Math.floor(original.experienceTotal * (100 - ConfigurationHandler.percentXpLoss) / 100);
		}
		int[] result = Helper.calculXp(player.experienceTotal);
		player.experienceLevel = result[0];
		player.experience = (float) result[1] / result[2];
		/** transfer all the grave's keys from the original to the player */
		if (!ConfigurationHandler.tombAccess || player.world.getGameRules().getBoolean("keepInventory")) { return; }
		int slot;
		ItemStack stack;
		for (int i = 0; i < original.inventory.getSizeInventory(); i++) {
			stack = original.inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem().equals(Main.grave_key)) {
				slot = player.inventory.getFirstEmptyStack();
				if (slot != -1) {
					player.inventory.setInventorySlotContents(slot, stack);
				} else { break; }
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
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
		while (currentPos.getY() < 0 || !world.isAirBlock(currentPos)) {
			currentPos = currentPos.up();
		}
		IBlockState state = Main.tombstone.getDefaultState().withProperty(BlockFacing.FACING, event.getEntityLiving().getHorizontalFacing().getOpposite());
		world.setBlockState(currentPos, state);
		TileEntityTombstone tile = (TileEntityTombstone) world.getTileEntity(currentPos);
		/** owner infos */
		tile.setOwner(playerIn, new Date().getTime(), ConfigurationHandler.tombAccess);
		/** fill tombstone with items and reverse the inventory (equipable first) */
		ItemStack stack;
		for (int i = event.getDrops().size() - 1; i >= 0; i--) {
			stack = event.getDrops().get(i).getEntityItem();
			if (!stack.isEmpty() && !stack.getItem().equals(Main.grave_key)) {
				EntityItem n = event.getDrops().get(i);
				n.setEntityItemStack(Helper.addToInventoryWithLeftover(stack, tile, false));
				event.getDrops().remove(i);
			}
		}
		/** add a grave key to player inventory if access are needed */
		if (ConfigurationHandler.tombAccess) {;
			stack = new ItemStack(Main.grave_key, 1, 0);
			ItemGraveKey.setTombPos(stack, currentPos, world.provider.getDimension());
			playerIn.inventory.setInventorySlotContents(playerIn.inventory.getFirstEmptyStack(), stack);
		}
		world.notifyBlockUpdate(currentPos, state, state, 2);
	}
}
