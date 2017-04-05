package ovh.corail.tombstone.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import ovh.corail.tombstone.gui.ContainerTombstone;
import ovh.corail.tombstone.gui.GuiTombstone;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class GuiHandler implements IGuiHandler {
	public static final int TOMB = 0;

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer playerIn, World worldIn, int x, int y, int z) {
		if (guiId != TOMB) {
			System.err.println("Invalid Id : expected " + TOMB + ", received " + guiId);
		}
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityTombstone) {
			return new ContainerTombstone(playerIn, worldIn, pos, (TileEntityTombstone) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer playerIn, World worldIn, int x, int y, int z) {
		if (guiId != TOMB) {
			System.err.println("Invalid Id : expected " + TOMB + ", received " + guiId);
		}
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityTombstone) {
			return new GuiTombstone(playerIn, worldIn, pos, (TileEntityTombstone) tileEntity);
		}
		return null;
	}
}
