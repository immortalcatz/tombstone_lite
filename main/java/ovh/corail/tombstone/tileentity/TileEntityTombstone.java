package ovh.corail.tombstone.tileentity;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.handler.ConfigurationHandler;

public class TileEntityTombstone extends TileEntityInventory {
	private final String name = "tileEntityTombstone";
	private boolean needAccess = false;
	private String ownerName = "Unknown";
	private long deathDate;
	
	public TileEntityTombstone() {
		super();
	}
	
	public void giveInventory(EntityPlayer player) {
		if (player == null || world.isRemote) { return; }
		for (int i = 0; i < inventory.size() ; i++) {
			if (getStackInSlot(i).isEmpty()) { continue; }
			ItemStack stack = getStackInSlot(i);
			boolean set = false;
			if (stack.getItem() instanceof ItemArmor) {
				int slotId = ((ItemArmor) stack.getItem()).armorType.getIndex();
				if (player.inventory.armorInventory.get(slotId).isEmpty()) {
					player.inventory.armorInventory.set(slotId, stack);
					setInventorySlotContents(i, ItemStack.EMPTY);
					set = true;
				}
			}
			if (!set) {
				setInventorySlotContents(i, Helper.addToInventoryWithLeftover(stack, player.inventory, false));
			}
		}
		player.inventoryContainer.detectAndSendChanges();
		world.setBlockToAir(pos);
		Helper.sendMessage("gui.message.tombEnd", player, true);
		/** TODO SOUND */
		world.removeTileEntity(this.pos);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public TextComponentString getDisplayName() {
		return new TextComponentString(Helper.getTranslation("gui.message.tombOf") + " " + ownerName);
	}

	public <T extends Entity> void setOwner(T owner, long deathDate) {
		setOwner(owner, deathDate, false);
	}
	
	public <T extends Entity> void setOwner(T owner, long deathDate, boolean needAccess) {
		this.ownerName = owner.getDisplayName().getUnformattedText();
		this.deathDate = deathDate;
		this.needAccess = needAccess;
		
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getOwnerDeathDate(int part) {
		Date date = new Date(deathDate);
		String dateString = new SimpleDateFormat("dd/MM/yyyy").format(date);
		String timeString = new SimpleDateFormat(Helper.getTranslation("HH:mm:ss")).format(date);
		String part1 = Helper.getTranslation("gui.message.diedOn") + " " + dateString;
		String part2 = Helper.getTranslation("gui.message.prefixAt") + " " + timeString;
		return part==0 ? part1 + " " + part2 : (part==1 ? part1 : part2);
		
		
	}

	public boolean getNeedAccess() {
		if (!needAccess) { return false; }
		if (ConfigurationHandler.decayTime == -1) { return true; }
		/** decay time before access are no more needed */
		int seconds = 0;
		long timeElapsed = new Date().getTime() - deathDate;
		seconds = (int) (timeElapsed % 60);
		return seconds > ConfigurationHandler.decayTime ? false : true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("ownerName", ownerName);
		compound.setLong("deathDate", deathDate);
		compound.setBoolean("needAccess", needAccess);
		super.writeToNBT(compound);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		ownerName = compound.getString("ownerName");
		deathDate = compound.getLong("deathDate");
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
