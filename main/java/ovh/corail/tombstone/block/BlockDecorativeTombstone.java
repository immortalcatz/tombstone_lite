package ovh.corail.tombstone.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockDecorativeTombstone extends BlockFacing {
	protected final static String name = "decorative_tombstone";
	protected static final AxisAlignedBB east = new AxisAlignedBB(0.1875f, 0f, 0.03125f, 0.8125f, 0.5f, 0.96875f);
	protected static final AxisAlignedBB north = new AxisAlignedBB(0.03125, 0f, 0.1875f, 0.96875f, 0.5f, 0.8125f);
	protected static final AxisAlignedBB west = new AxisAlignedBB(0.1875f, 0f, 0.03125f, 0.8125f, 0.5f, 0.96875f);
	protected static final AxisAlignedBB south = new AxisAlignedBB(0.03125f, 0f, 0.1875f, 0.96875f, 0.5f, 0.8125f);
	
	public BlockDecorativeTombstone() {
		super(Material.ROCK, name);
		this.setHardness(2.0F);
		this.setResistance(30.0F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);
		switch (enumfacing) {
		case NORTH:
			return north;
		case SOUTH:
			return south; 
		case WEST:
			return west;
		case EAST:
			return east;
		default:
			return north;
		}
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

}
