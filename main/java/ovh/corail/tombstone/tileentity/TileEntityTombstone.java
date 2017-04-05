package ovh.corail.tombstone.tileentity;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import ovh.corail.tombstone.core.Helper;

public class TileEntityTombstone extends TileEntity implements ISidedInventory {
	private final String name = "tileEntityTombstone";
	private final int slotsCount = 45;
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(slotsCount, ItemStack.EMPTY);
	private boolean needAccess;
	private UUID ownerId;
	private String ownerName;
	private String deathDate;
	
	public TileEntityTombstone() {
		super();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean hasCustomName() {
		return true;
	}
	
	@Override
	public TextComponentString getDisplayName() {
		return new TextComponentString(Helper.getTranslation("gui.message.tombOf") + " " + ownerName);
	}

	public <T extends Entity> void setOwner(T owner, String deathDate) {
		setOwner(owner, deathDate, false);
	}
	
	public <T extends Entity> void setOwner(T owner, String deathDate, boolean needAccess) {
		this.ownerId = owner.getUniqueID();
		this.ownerName = owner.getDisplayName().getUnformattedText();
		this.deathDate = deathDate;
		this.needAccess = needAccess;
		
	}
	
	public UUID getPlayerId() {
		return ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getOwnerDeathDate() {
		return deathDate;
	}

	public boolean getNeedAccess() {
		// TODO decay time to access
		return needAccess;
	}

	@Override
	public int getSizeInventory() {
		return slotsCount;
	}
	
	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inventory) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public int getEmptySlot() {
		ItemStack stack;
		for (int i = 0 ; i < inventory.size() ; i++) {
			if (inventory.get(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public ItemStack getStackInSlot(int slotId) {
		return slotId >= 0 && slotId < slotsCount ? (ItemStack) inventory.get(slotId) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int slotId, int count) {
		return ItemStackHelper.getAndSplit(inventory, slotId, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int slotId) {
		return ItemStackHelper.getAndRemove(this.inventory, slotId);
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack stack) {
		if (slotId >= 0 && slotId < slotsCount) {
			inventory.set(slotId, stack);
		} else {
			inventory.set(slotId, ItemStack.EMPTY);
		}
		markDirty();
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public void markDirty() {
		
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}
	
	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	public int getSlotsCount() {
		return slotsCount;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setUniqueId("ownerId", ownerId);
		compound.setString("ownerName", ownerName);
		compound.setString("deathDate", deathDate);
		compound.setBoolean("needAccess", needAccess);
		ItemStackHelper.saveAllItems(compound, inventory);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		ownerId = compound.getUniqueId("ownerId");
		ownerName = compound.getString("ownerName");
		deathDate = compound.getString("deathDate");
		needAccess = compound.getBoolean("needAccess");
		ItemStackHelper.loadAllItems(compound, inventory);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		double renderExtention = 1.0d;
		AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - renderExtention, pos.getY() - renderExtention, pos.getZ() - renderExtention, pos.getX() + 1 + renderExtention, pos.getY() + 1 + renderExtention, pos.getZ() + 1 + renderExtention);
		return bb;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}
}
