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

public class BlockDecorativeGraveCross<T extends TileEntityWritableGrave> extends BlockTileEntityGrave<T> {
	public static final String name = "decorative_grave_cross";
	protected static final AxisAlignedBB cross_center_bounds_north = new AxisAlignedBB(0.40625d, 0.125d, 0.6875d, 0.59375d, 0.9375d, 0.875d);
	protected static final AxisAlignedBB cross_center_bounds_south = new AxisAlignedBB(0.40625d, 0.125d, 0.125d, 0.59375d, 0.9375d, 0.3125d);
	protected static final AxisAlignedBB cross_center_bounds_west = new AxisAlignedBB(0.6875d, 0.125d, 0.40625d, 0.875d, 0.9375d, 0.59375d);
	protected static final AxisAlignedBB cross_center_bounds_east = new AxisAlignedBB(0.125d, 0.125d, 0.40625d, 0.3125d, 0.9375d, 0.59375d);
	protected static final AxisAlignedBB cross_border_bounds_north = new AxisAlignedBB(0.21875d, 0.5625d, 0.6875d, 0.78125d, 0.75d, 0.875d);
	protected static final AxisAlignedBB cross_border_bounds_south = new AxisAlignedBB(0.21875d, 0.5625d, 0.125d, 0.78125d, 0.75d, 0.3125d);
	protected static final AxisAlignedBB cross_border_bounds_west = new AxisAlignedBB(0.6875d, 0.5625d, 0.21875d, 0.875d, 0.75d, 0.78125d);
	protected static final AxisAlignedBB cross_border_bounds_east = new AxisAlignedBB(0.125d, 0.5625d, 0.21875d, 0.3125d, 0.75d, 0.78125d);
	protected static final AxisAlignedBB cross_base_down_bounds_north = new AxisAlignedBB(0.28125d, 0.0625d, 0.5625d, 0.71875d, 0.125d, 1.0d);
	protected static final AxisAlignedBB cross_base_down_bounds_south = new AxisAlignedBB(0.28125d, 0.0625d, 0.0d, 0.71875d, 0.125d, 0.4375d);
	protected static final AxisAlignedBB cross_base_down_bounds_west = new AxisAlignedBB(0.5625d, 0.0625d, 0.28125d, 1.0d, 0.125d, 0.71875d);
	protected static final AxisAlignedBB cross_base_down_bounds_east = new AxisAlignedBB(0.0d, 0.0625d, 0.28125d, 0.4375d, 0.125d, 0.71875d);
	protected static final AxisAlignedBB cross_base_top_bounds_north = new AxisAlignedBB(0.34375d, 0.125d, 0.625d, 0.65625d, 0.1875d, 0.9375d);
	protected static final AxisAlignedBB cross_base_top_bounds_south = new AxisAlignedBB(0.34375d, 0.125d, 0.0625d, 0.65625d, 0.1875d, 0.375d);
	protected static final AxisAlignedBB cross_base_top_bounds_west = new AxisAlignedBB(0.625d, 0.125d, 0.34375d, 0.9375d, 0.1875d, 0.65625d);
	protected static final AxisAlignedBB cross_base_top_bounds_east = new AxisAlignedBB(0.0625d, 0.125d, 0.34375d, 0.375d, 0.1875d, 0.65625d);

	public BlockDecorativeGraveCross() {
		super(Material.ROCK, name);
	}
	
	public BlockDecorativeGraveCross(String name) {
		super(Material.ROCK, name);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		switch (state.getValue(FACING)) {
		case SOUTH:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_center_bounds_south);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_border_bounds_south);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_base_down_bounds_south);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_base_top_bounds_south);
			break;
		case WEST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_center_bounds_west);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_border_bounds_west);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_base_down_bounds_west);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_base_top_bounds_west);
			break;
		case EAST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_center_bounds_east);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_border_bounds_east);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_base_down_bounds_east);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_base_top_bounds_east);
			break;
		case NORTH:default:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_center_bounds_north);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_border_bounds_north);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_base_down_bounds_north);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, cross_base_top_bounds_north);
		}
		super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
    }

	@Override
	public T createTileEntity(World world, IBlockState state) {
		return (T) new TileEntityWritableGrave();
	}
	
}
