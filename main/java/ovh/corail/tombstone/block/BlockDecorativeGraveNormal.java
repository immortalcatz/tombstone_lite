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

public class BlockDecorativeGraveNormal<T extends TileEntityWritableGrave> extends BlockTileEntityGrave<T> {
	public static final String name = "decorative_grave_normal";
	protected static final AxisAlignedBB grave_box_bounds_north = new AxisAlignedBB(0.1875d, 0.0625d, 0.0d, 0.8125d, 0.28125d, 1.0d);
	protected static final AxisAlignedBB grave_box_bounds_south = new AxisAlignedBB(0.1875d, 0.0625d, 0.0d, 0.8125d, 0.28125d, 1.0d);
	protected static final AxisAlignedBB grave_box_bounds_west = new AxisAlignedBB(0.0d, 0.0625d, 0.1875d, 1.0d, 0.28125d, 0.8125d);
	protected static final AxisAlignedBB grave_box_bounds_east = new AxisAlignedBB(0.0d, 0.0625d, 0.1875d, 1.0d, 0.28125d, 0.8125d);
	protected static final AxisAlignedBB grave_side_bounds_north = new AxisAlignedBB(0.1875d, 0.25d, 0.875d, 0.8125d, 0.875d, 1.0d);
	protected static final AxisAlignedBB grave_side_bounds_south = new AxisAlignedBB(0.1875d, 0.25d, 0.0d, 0.8125d, 0.875d, 0.125d);
	protected static final AxisAlignedBB grave_side_bounds_west = new AxisAlignedBB(0.875d, 0.25d, 0.1875d, 1.0d, 0.875d, 0.8125d);
	protected static final AxisAlignedBB grave_side_bounds_east = new AxisAlignedBB(0.0d, 0.25d, 0.1875d, 0.125d, 0.875d, 0.8125d);


	public BlockDecorativeGraveNormal() {
		super(Material.ROCK, name);
	}
	
	public BlockDecorativeGraveNormal(String name) {
		super(Material.ROCK, name);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		switch (state.getValue(FACING)) {
		case SOUTH:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_box_bounds_south);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_side_bounds_south);
			break;
		case WEST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_box_bounds_west);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_side_bounds_west);
			break;
		case EAST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_box_bounds_east);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_side_bounds_east);
			break;
		case NORTH:default:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_box_bounds_north);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, grave_side_bounds_north);
		}
		super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
    }

	@Override
	public T createTileEntity(World world, IBlockState state) {
		return (T) new TileEntityWritableGrave();
	}
	
}
