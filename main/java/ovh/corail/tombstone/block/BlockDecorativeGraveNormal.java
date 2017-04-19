package ovh.corail.tombstone.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import ovh.corail.tombstone.core.Helper;

public class BlockDecorativeGraveNormal extends BlockFacingGrave {
	public static final String name = "decorative_grave_normal";
	protected static final AxisAlignedBB east = new AxisAlignedBB(0f, 0f, 0.25f, 0.875f, 0.5f, 0.75f);
	protected static final AxisAlignedBB north = new AxisAlignedBB(0.25f, 0f, 0.125f, 0.75f, 0.5f, 1f);
	protected static final AxisAlignedBB west = new AxisAlignedBB(0.125f, 0f, 0.25f, 1f, 0.5f, 0.75f);
	protected static final AxisAlignedBB south = new AxisAlignedBB(0.25f, 0f, 0f, 0.75f, 0.5f, 0.875f);


	public BlockDecorativeGraveNormal() {
		super(Material.ROCK, name);
		this.setHardness(2f);
		this.setResistance(30f);
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
    public int quantityDropped(Random random) {
        return Helper.getRandom(0, 4);
    }

	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.STONE);
    }
	
	@Override
	protected boolean canSilkHarvest() {
		return true;
	}
	
}
