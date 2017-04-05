package ovh.corail.tombstone.gui;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class ContainerTombstone extends Container {

	private TileEntityTombstone inventory;
	private BlockPos currentPos;
	private int dimCase = 16;

	public ContainerTombstone(EntityPlayer player, World world, BlockPos pos, TileEntityTombstone tileEntity) {
		this.inventory = tileEntity;
		this.currentPos = pos;
		int i, j;
		for (i = 0; i < 5; i++) {
			for (j = 0; j < 9; j++) {
				this.addSlotToContainer(new SlotTombstone(inventory, j + (i * 9), 8 + j * (dimCase + 2), i * (dimCase + 2) + 5));
			}
		}
		bindPlayerInventory(player.inventory);
	}

	/** button takeAll */
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int i;
		int j;
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + (i + 1) * 9, 8 + j * (dimCase + 2), (i + 5) * (dimCase + 2) + 25));
			}
		}
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + (i * 18), (8 * 18) + 25));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return inventory.isUsableByPlayer(playerIn);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		World world = playerIn.world;
		if (inventory.isEmpty() && !world.isRemote) {
			BlockPos pos = inventory.getPos();
			InventoryPlayer inv = playerIn.inventory;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				if (!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() == Main.grave_key) {
					NBTTagCompound compound = inv.getStackInSlot(i).getTagCompound();
					//try {
					UUID.fromString(compound.getString("tombId"));
					//} catch (IllegalArgumentException e) {
					//	continue;
					//}
					UUID idKey = UUID.fromString(compound.getString("tombId"));
					BlockPos tombPos = BlockPos.fromLong(compound.getLong("tombPos"));
					if (world.getTileEntity(tombPos) instanceof TileEntityTombstone) {
						TileEntityTombstone tomb = (TileEntityTombstone) world.getTileEntity(currentPos);
						UUID idTomb = tomb.getPlayerId();
						if (idKey.equals(idTomb)) {
							inv.setInventorySlotContents(i, ItemStack.EMPTY);
							break;
						}
					}
				}
			}
			world.removeTileEntity(pos);
			world.setBlockToAir(pos);
			Helper.sendMessage("gui.message.tombEnd", playerIn, true);
			/** TODO SOUND */
		}
		super.onContainerClosed(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < 45) {
				if (!this.mergeItemStack(itemstack1, 45, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 45, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}
}
