package ovh.corail.tombstone.handler;

import java.util.Date;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ovh.corail.tombstone.block.BlockFacingGrave;
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
	/** create a tombstone with drops on death */
	public void onLivingDrops(LivingDropsEvent event) {
		/** if it's a player */
		if (!(event.getEntityLiving() instanceof EntityPlayer)) { return; }
		EntityPlayer playerIn = (EntityPlayer) event.getEntityLiving();
		/** not in creative mode and no rule of keep inventory on death */
		if (playerIn.world.getGameRules().getBoolean("keepInventory") || playerIn.isCreative()) { return; }
		/** no drop or too much drops */
		if (event.getDrops().size() <= 0 || event.getDrops().size() > 60) {	return; }
		/** create the tombstone */
		buildTombstone(event, playerIn);
		playerIn.addStat(AchievementHandler.getAchievement("firstTomb"), 1);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
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
		ItemStack originalStack;
		ItemStack playerStack;
		for (int i = 0; i < original.inventory.getSizeInventory(); i++) {
			originalStack = original.inventory.getStackInSlot(i).copy();
			playerStack = player.inventory.getStackInSlot(i);
			if (!originalStack.isEmpty() && originalStack.getItem() instanceof ItemGraveKey) {
				if (playerStack.getItem() instanceof ItemGraveKey) { continue; } /** already transferred */
				player.inventory.setInventorySlotContents(i, originalStack);
				original.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	/** remove experience balls */
	public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			event.setCanceled(true);
		}
	}

	public void buildTombstone(LivingDropsEvent event, EntityPlayer playerIn) {
		/** world dimension */
		World world = event.getEntityLiving().world;
		/** surface */
		BlockPos currentPos = event.getEntityLiving().getPosition();
		/** go up to find air */
		while (!Helper.isSafeBlock(world, currentPos)) {
			currentPos = currentPos.up();
		}
		IBlockState state = Main.tombstone.getDefaultState().withProperty(BlockFacingGrave.FACING, event.getEntityLiving().getHorizontalFacing().getOpposite());
		world.setBlockState(currentPos, state);
		TileEntityTombstone tile = (TileEntityTombstone) world.getTileEntity(currentPos);
		/** owner infos */
		tile.setOwner(playerIn, new Date().getTime(), ConfigurationHandler.tombAccess);
		/** fill tombstone with items and reverse the inventory (equipable first) */
		ItemStack stack;
		for (int i = event.getDrops().size() - 1; i >= 0; i--) {
			stack = event.getDrops().get(i).getEntityItem();
			if (stack.isEmpty()) { continue; }
			EntityItem n = event.getDrops().get(i);
			if (!stack.getItem().equals(Main.grave_key)) {			
				n.setEntityItemStack(Helper.addToInventoryWithLeftover(stack, tile, false));		
			} else {
				n.setEntityItemStack(Helper.addToInventoryWithLeftover(stack, playerIn.inventory, false));
			}
			event.getDrops().remove(i);
		}
		/** add a grave key to player inventory if access are needed */
		if (ConfigurationHandler.tombAccess) {;
			stack = new ItemStack(Main.grave_key, 1, 0);
			ItemGraveKey.setTombPos(stack, currentPos, world.provider.getDimension());
			Helper.addToInventoryWithLeftover(stack, playerIn.inventory, false);
		}
		world.notifyBlockUpdate(currentPos, state, state, 2);
	}
}
