package ovh.corail.tombstone.tileentity;

import java.util.Date;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.SoundHandler;

public class TileEntityTombstone extends TileEntityWritableGrave {
	protected ItemStackHandler inventory = new ItemStackHandler(64);
	protected boolean needAccess = false;
	
	public TileEntityTombstone() {
		super();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
	  return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
	  return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
	}
	
	public void giveInventory(EntityPlayer player) {
		if (player == null || world.isRemote) { return; }
		IBaublesItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		for (int i = 0; i < inventory.getSlots() ; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) { continue; }
			boolean set = false;
			/** auto equip */
			if (handler != null) {
				for (int slot = 0 ; slot < handler.getSlots() ; slot++) {
					if (handler.getStackInSlot(slot).isEmpty() && handler.isItemValidForSlot(slot, stack, player)) {
						handler.setStackInSlot(slot, stack);
						inventory.setStackInSlot(i, ItemStack.EMPTY);
						set = true;
						break;
					}
				}
			}
			if (!set && stack.getItem() instanceof ItemShield && player.inventory.offHandInventory.get(0).isEmpty()) {
				player.inventory.offHandInventory.set(0, stack);
				inventory.setStackInSlot(i, ItemStack.EMPTY);
				set = true;
			}
			if (!set && stack.getItem() instanceof ItemArmor) {
				int slotId = ((ItemArmor) stack.getItem()).armorType.getIndex();
				if (player.inventory.armorInventory.get(slotId).isEmpty()) {
					player.inventory.armorInventory.set(slotId, stack);
					inventory.setStackInSlot(i, ItemStack.EMPTY);
					set = true;
				}
			}
			if (!set) {
				ItemHandlerHelper.giveItemToPlayer(player, stack);
				inventory.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
		player.inventoryContainer.detectAndSendChanges();
		world.setBlockToAir(pos);
		SoundHandler.playSoundAllAround("block.wooden_door.close", world, player.getPosition(), 10d);
		Helper.sendMessage("message.open_grave.success", player, true);
		world.removeTileEntity(this.pos);
	}
	
	public <T extends Entity> void setOwner(T owner, long deathDate, boolean needAccess) {
		super.setOwner(owner, deathDate);
		this.needAccess = needAccess;	
	}
	
	public boolean getNeedAccess() {
		if (!needAccess) { return false; }
		if (ConfigurationHandler.decayTime == -1) { return true; }
		/** decay time in minutes before access are no more needed */
		return (new Date().getTime() - deathDate)/60000 >= (long)ConfigurationHandler.decayTime ? false : true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", inventory.serializeNBT());
		compound.setBoolean("needAccess", needAccess);
		super.writeToNBT(compound);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		needAccess = compound.getBoolean("needAccess");
		super.readFromNBT(compound);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		double renderExtension = 1.0d;
		AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - renderExtension, pos.getY() - renderExtension, pos.getZ() - renderExtension, pos.getX() + 1 + renderExtension, pos.getY() + 1 + renderExtension, pos.getZ() + 1 + renderExtension);
		return bb;
	}
}
