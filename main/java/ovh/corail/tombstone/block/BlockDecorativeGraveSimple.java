package ovh.corail.tombstone.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ovh.corail.tombstone.tileentity.TileEntityWritableGrave;

public class BlockDecorativeGraveSimple<T extends TileEntityWritableGrave> extends BlockTileEntityGrave<T> {
	public static final String name = "decorative_grave_simple";
	protected static final AxisAlignedBB grave_bounds_north = new AxisAlignedBB(0.1875d, 0.0625d, 0.8125d, 0.8125d, 0.875d, 1.0d);
	protected static final AxisAlignedBB grave_bounds_south = new AxisAlignedBB(0.1875d, 0.0625d, 0.0d, 0.8125d, 0.875d, 0.1875d);
	protected static final AxisAlignedBB grave_bounds_west = new AxisAlignedBB(0.8125d, 0.0625d, 0.1875d, 1.0d, 0.875d, 0.8125d);
	protected static final AxisAlignedBB grave_bounds_east = new AxisAlignedBB(0.0d, 0.0625d, 0.1875d, 0.1875d, 0.875d, 0.8125d);
	
	public BlockDecorativeGraveSimple() {
		super(Material.ROCK, name);
	}
	
	public BlockDecorativeGraveSimple(String name) {
		super(Material.ROCK, name);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		switch (state.getValue(FACING)) {
		case SOUTH:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_bounds_south);
			break;
		case WEST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_bounds_west);
			break;
		case EAST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_bounds_east);
			break;
		case NORTH:default:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_bounds_north);
		}
		super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
    }

	@Override
	public T createTileEntity(World world, IBlockState state) {
		return (T) new TileEntityWritableGrave();
	}
	
}
