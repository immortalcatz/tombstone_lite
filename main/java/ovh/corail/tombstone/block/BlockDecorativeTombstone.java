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

public class BlockDecorativeTombstone<T extends TileEntityWritableGrave> extends BlockTileEntityGrave<T> {
	protected final static String name = "decorative_tombstone";
	protected static final AxisAlignedBB container_north = new AxisAlignedBB(0.03125d, 0.0625d, 0.21875d, 0.96875d, 0.64375d, 0.96875d);
	protected static final AxisAlignedBB container_south = new AxisAlignedBB(0.03125d, 0.0625d, 0.03125d, 0.96875d, 0.64375d, 0.78125d);
	protected static final AxisAlignedBB container_west = new AxisAlignedBB(0.21875d, 0.0625d, 0.03125d, 0.96875d, 0.64375d, 0.96875d);
	protected static final AxisAlignedBB container_east = new AxisAlignedBB(0.03125d, 0.0625d, 0.03125d, 0.78125d, 0.64375d, 0.96875d);
	protected static final AxisAlignedBB stair_north = new AxisAlignedBB(0.0625d, 0.0625d, 0.0625d, 0.9375d, 0.09375d, 0.21875d);
	protected static final AxisAlignedBB stair_south = new AxisAlignedBB(0.0625d, 0.0625d, 0.78125d, 0.9375d, 0.09375d, 0.9375d);
	protected static final AxisAlignedBB stair_west = new AxisAlignedBB(0.0625d, 0.0625d, 0.0625d, 0.21875d, 0.09375d, 0.9375d);
	protected static final AxisAlignedBB stair_east = new AxisAlignedBB(0.78125d, 0.0625d, 0.0625d, 0.9375d, 0.09375d, 0.9375d);
	
	public BlockDecorativeTombstone() {
		super(Material.ROCK, name);
	}

	public BlockDecorativeTombstone(String name) {
		super(Material.ROCK, name);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		switch (state.getValue(FACING)) {
		case SOUTH:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, container_south);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, stair_south);
			break;
		case WEST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, container_west);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, stair_west);
			break;
		case EAST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, container_east);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, stair_east);
			break;
		case NORTH:default:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, container_north);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, stair_north);
		}
		super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
    }

	@Override
	public T createTileEntity(World world, IBlockState state) {
		return (T) new TileEntityWritableGrave();
	}

}
