package ovh.corail.tombstone.handler;

import java.util.Date;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import nightkosh.gravestone.api.GraveStoneAPI;
import ovh.corail.tombstone.block.BlockFacingGrave;
import ovh.corail.tombstone.block.BlockTombstone;
import ovh.corail.tombstone.block.IPlayerGrave;
import ovh.corail.tombstone.block.ItemBlockGrave;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.core.ModProps;
import ovh.corail.tombstone.item.ItemGraveKey;
import ovh.corail.tombstone.packet.TombstoneActivatedMessage;
import ovh.corail.tombstone.packet.UpdateFavoriteGraveMessage;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class EventHandler {
	/** Events */
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		/** client side */
		if (event.getModID().equals(ModProps.MOD_ID)) {
			ConfigurationHandler.refreshConfig();
			if (!ConfigurationHandler.lastFavoriteGrave.equals(ConfigurationHandler.favoriteGrave)) {
				ConfigurationHandler.lastFavoriteGrave = ConfigurationHandler.favoriteGrave;
				PacketHandler.INSTANCE.sendToServer(new UpdateFavoriteGraveMessage(ConfigurationHandler.favoriteGrave, true));
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerLogued(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.getEntity();
			if (event.getWorld().isRemote) {
				PacketHandler.INSTANCE.sendToServer(new UpdateFavoriteGraveMessage(ConfigurationHandler.favoriteGrave, false));
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	/** create a tombstone with drops on death */
	public void onLivingDrops(LivingDropsEvent event) {
		/** if it's a player */
		if (!ConfigurationHandler.handlePlayerDeath || !(event.getEntityLiving() instanceof EntityPlayer)) { return; }
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		/** not in creative mode and no rule of keep inventory on death */
		if (player.world.getGameRules().getBoolean("keepInventory") || player.isCreative()) { return; }
		/** no drop or too much drops */
		if (event.getDrops().size() <= 0 || event.getDrops().size() > 64) {	return; }
		/** create the tombstone */
		buildTombstone(event, player);
		/** advancement first_tomb */
		Helper.grantAdvancement(player, "tutorial/first_tomb");
		if (Loader.isModLoaded("gravestone") || Loader.isModLoaded("GraveStone")) {
			if (GraveStoneAPI.graveGenerationAtDeath == null) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void give(PlayerEvent.Clone event) {
		/** change experience loss depending on config */
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
		if (!ConfigurationHandler.handlePlayerDeath || !ConfigurationHandler.tombAccess || player.world.getGameRules().getBoolean("keepInventory")) { return; }
		int slot;
		ItemStack originalStack;
		ItemStack playerStack;
		for (int i = 0; i < original.inventory.getSizeInventory(); i++) {
			if (original.inventory.getStackInSlot(i).isEmpty()) { continue; }
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

	public void buildTombstone(LivingDropsEvent event, EntityPlayer player) {
		World world = player.world;
		BlockPos currentPos = player.getPosition();
		/** go up to find air */
		while (!Helper.isSafeBlock(world, currentPos)) {
			currentPos = currentPos.up();
		}
		EnumFacing facing = event.getEntityLiving().getHorizontalFacing().getOpposite();
		Block graveType = DeathHandler.getInstance().getFavoriteGraveBlock(player.getUniqueID());
		IBlockState state = graveType.getDefaultState().withProperty(BlockFacingGrave.FACING, facing);
		world.setBlockState(currentPos, state);
		TileEntityTombstone tile = (TileEntityTombstone) world.getTileEntity(currentPos);
		/** owner infos */
		boolean needAccess = ConfigurationHandler.tombAccess;
		if (needAccess && ConfigurationHandler.pvpMode && event.getSource() != null) {
			Entity killer = event.getSource().getTrueSource();
			if (killer != null && killer instanceof EntityPlayer) {
				needAccess = false;
			}
		}
		/** fill tombstone with items and reverse the inventory (equipable first) */
		IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
		tile.setOwner(player, new Date().getTime(), needAccess);
		ItemStack stack;
		for (int i = event.getDrops().size() - 1; i >= 0; i--) {
			stack = event.getDrops().get(i).getItem();
			if (stack.isEmpty()) { continue; }
			EntityItem n = event.getDrops().get(i);
			if (!stack.getItem().equals(Main.grave_key)) {
				n.setItem(ItemHandlerHelper.insertItem(itemHandler, stack, false));
			} else {
				n.setItem(Helper.addToInventoryWithLeftover(stack, player.inventory, false));
			}
			event.getDrops().remove(i);
		}
		/** add a grave key to player inventory if access are needed */
		if (ConfigurationHandler.tombAccess) {;
			stack = new ItemStack(Main.grave_key, 1, 0);
			ItemGraveKey.setTombPos(stack, currentPos, world.provider.getDimension());
			Helper.addToInventoryWithLeftover(stack, player.inventory, false);
		}
		world.notifyBlockUpdate(currentPos, state, state, 2);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		Item input = event.getLeft().isEmpty() ? null : event.getLeft().getItem();
		Item ingredient = event.getRight().isEmpty() ? null : event.getRight().getItem();
		if (input instanceof ItemBlockGrave && ingredient == Items.STICK) {
			ItemStack output = event.getLeft().copy();
			if (ItemBlockGrave.setEngravedName(output, event.getName())) {
				event.setCost(2);
				event.setOutput(output);
			} else {
				event.setCost(0);
				event.setOutput(ItemStack.EMPTY);	
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onAnvilRepair(AnvilRepairEvent event) {
		Item input = event.getItemInput().isEmpty() ? null : event.getItemInput().getItem();
		Item ingredient = event.getIngredientInput().isEmpty() ? null : event.getIngredientInput().getItem();
		ItemStack output = event.getItemResult();
		if (input instanceof ItemBlockGrave && ingredient == Items.STICK && ItemBlockGrave.isEngraved(output)) {
			/** advancement engrave_decorative_grave */
			Helper.grantAdvancement(event.getEntityPlayer(), "tutorial/engrave_decorative_grave");
		}	
	}
	
	/** compatibility with Claimed Block Mod & Vanilla Spawn Protection */
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void uncancelGraveRightClick(PlayerInteractEvent.RightClickBlock event) {
		if (event.isCanceled()) {
			Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
			if (block instanceof IPlayerGrave) {
				event.setCanceled(false);
				event.setUseBlock(Result.DEFAULT);
				event.setUseItem(Result.DEFAULT);
			}
		}
		if (event.getWorld().isRemote && !Minecraft.getMinecraft().isIntegratedServerRunning()) {
			BlockPos currentPos = event.getPos();
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			Block block = state.getBlock();
			if (block instanceof IPlayerGrave) {
				PacketHandler.INSTANCE.sendToServer(new TombstoneActivatedMessage(event.getPos()));
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void uncancelGraveBuild(BlockEvent.PlaceEvent event) {
		if (event.isCanceled()) {
			Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
			if (block instanceof IPlayerGrave) {
				event.setCanceled(false);
			}
		}
	}
	
	/** compatibility with EuhDawson Grave Mod */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLivingDropsStart(LivingDropsEvent event) {
		if (Loader.isModLoaded("gravestone") || Loader.isModLoaded("GraveStone")) {
			if (GraveStoneAPI.graveGenerationAtDeath == null) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onLivingDropsEnd(LivingDropsEvent event) {
		if (Loader.isModLoaded("gravestone") || Loader.isModLoaded("GraveStone")) {
			if (GraveStoneAPI.graveGenerationAtDeath == null) {
				event.setCanceled(false);
			}
		}
	}
}
