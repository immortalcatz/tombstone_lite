package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class TakeAllMessage implements IMessage {
	BlockPos currentPos;

	public TakeAllMessage() {
	}

	public TakeAllMessage(BlockPos currentPos) {
		this.currentPos = currentPos;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(currentPos.toLong());
	}

	public static class Handler implements IMessageHandler<TakeAllMessage, IMessage>  {
		@Override
		public IMessage onMessage(final TakeAllMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
				EntityPlayer player = ctx.getServerHandler().player;
				World worldIn = ctx.getServerHandler().player.world;
				TileEntity tile = worldIn.getTileEntity(message.currentPos);
				if (tile == null || !(tile instanceof TileEntityTombstone)) { return ; }
				TileEntityTombstone tombstone = (TileEntityTombstone) worldIn.getTileEntity(message.currentPos);

				
				for (int i = 0; i < tombstone.getSizeInventory(); i++) {
					if (!tombstone.getStackInSlot(i).isEmpty()) {
						ItemStack stack = tombstone.getStackInSlot(i);
						boolean set = false;
						if (stack.getItem() instanceof ItemArmor) {
							ItemArmor armor = (ItemArmor) stack.getItem();
							switch (armor.armorType) {
							case FEET:
								if (player.inventory.armorItemInSlot(0).isEmpty()) {
									player.inventory.armorInventory.set(0, stack);
									tombstone.setInventorySlotContents(i, ItemStack.EMPTY);
									set = true;
								}
								break;
							case LEGS:
								if (player.inventory.armorItemInSlot(1).isEmpty()) {
									player.inventory.armorInventory.set(1, stack);
									tombstone.setInventorySlotContents(i, ItemStack.EMPTY);
									set = true;
								}
								break;
							case CHEST:
								if (player.inventory.armorItemInSlot(2).isEmpty()) {
									player.inventory.armorInventory.set(2, stack);
									tombstone.setInventorySlotContents(i, ItemStack.EMPTY);
									set = true;
								}
								break;
							case HEAD:
								if (player.inventory.armorItemInSlot(3).isEmpty()) {
									player.inventory.armorInventory.set(3, stack);
									tombstone.setInventorySlotContents(i, ItemStack.EMPTY);
									set = true;
								}
								break;
							default:
								break;

							}
						}
						if (!set) {
							tombstone.setInventorySlotContents(i, Helper.addToInventoryWithLeftover(stack, player.inventory, false));
						}
					}
				}
				player.openContainer.detectAndSendChanges();
			}
		});
		return null;
		}
	}
	
}
