package ovh.corail.tombstone.tileentity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import ovh.corail.tombstone.handler.ConfigurationHandler;

public class TileEntityTombstone extends TileEntityInventory {
	private final String name = "tileEntityTombstone";
	private boolean needAccess = false;
	private String ownerName = "Unknown";
	private String deathDate = "Never";
	
	public TileEntityTombstone() {
		super();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public TextComponentString getDisplayName() {
		return new TextComponentString(Helper.getTranslation("gui.message.tombOf") + " " + ownerName);
	}

	public <T extends Entity> void setOwner(T owner, String deathDate) {
		setOwner(owner, deathDate, false);
	}
	
	public <T extends Entity> void setOwner(T owner, String deathDate, boolean needAccess) {
		this.ownerName = owner.getDisplayName().getUnformattedText();
		this.deathDate = deathDate;
		this.needAccess = needAccess;
		
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getOwnerDeathDate() {
		return deathDate;
	}

	public boolean getNeedAccess() {
		if (!needAccess) { return false; }
		if (ConfigurationHandler.decayTime == -1) { return true; }
		/** decay time before access are no more needed */
		int seconds = 0;
		Date deathDate = null;
		try {
			deathDate = new SimpleDateFormat("ddMMyyyy_HHmmss").parse(getOwnerDeathDate());
		} catch (ParseException e) { e.printStackTrace(); }
		long timeElapsed = world.getWorldTime()-deathDate.getTime();
		seconds = (int) (timeElapsed % 60);
		return seconds > ConfigurationHandler.decayTime ? false : true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("ownerName", ownerName);
		compound.setString("deathDate", deathDate);
		compound.setBoolean("needAccess", needAccess);
		super.writeToNBT(compound);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		ownerName = compound.getString("ownerName");
		deathDate = compound.getString("deathDate");
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
