package ovh.corail.tombstone.tileentity;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ovh.corail.tombstone.core.Helper;

public class TileEntityWritableGrave extends TileEntity {
	protected String ownerName = "";
	protected long deathDate;
	
	public TileEntityWritableGrave() {
		super();	
	}
	
	public <T extends Entity> void setOwner(T owner, long deathDate) {
		setOwner(owner.getDisplayName().getUnformattedText(), deathDate);
	}
	
	public void setOwner(String ownerName, long deathDate) {
		this.ownerName = ownerName;
		this.deathDate = deathDate;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public String getOwnerDeathDate(int part, boolean formated) {
		Date date = new Date(deathDate);
		String dateString = new SimpleDateFormat("dd/MM/yyyy").format(date);
		String timeString = new SimpleDateFormat("HH:mm:ss").format(date);
		String part1 = formated ? Helper.getTranslation("message.death_date.died_on") + " " +dateString : dateString;
		String part2 = formated ? Helper.getTranslation("message.death_date.at") + " " + timeString : timeString;
		return part==0 ? part1 + " " + part2 : (part==1 ? part1 : part2);	
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("ownerName", ownerName);
		compound.setLong("deathDate", deathDate);
		super.writeToNBT(compound);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		ownerName = compound.getString("ownerName");
		deathDate = compound.getLong("deathDate");
		super.readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1, serializeNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
}
