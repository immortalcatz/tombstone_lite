package ovh.corail.tombstone.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;

public class BlockFacingGrave extends Block {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool HAS_SOUL = PropertyBool.create("has_soul");
	protected static final AxisAlignedBB ground_bounds = new AxisAlignedBB(0d, 0d, 0d, 1d, 0.0625d, 1d);
	
	public BlockFacingGrave(Material materialIn, String name) {
		super(materialIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setHardness(4.0f);
		this.setResistance(30.0f);
		this.setLightLevel(0f);
		this.setLightOpacity(255);
		this.setHarvestLevel("shovel", 0);
		this.useNeighborBrightness = true;
		/** default values */
		this.setCreativeTab(Main.tabTombstone);
		this.blockSoundType = SoundType.STONE;
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HAS_SOUL, false));
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, ground_bounds);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, HAS_SOUL });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return getDefaultState().withProperty(FACING, enumfacing).withProperty(HAS_SOUL, (meta & 8) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() + (state.getValue(HAS_SOUL) ? 8 : 0);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    	worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(HAS_SOUL, false));
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	/** DEBUG */
	private AxisAlignedBB getBounds(double voxelX1, double voxelY1, double voxelZ1, double voxelX2, double voxelY2, double voxelZ2, EnumFacing facing) {
		return getBounds(new AxisAlignedBB(voxelX1/16d, voxelY1/16d, voxelZ1/16d, voxelX2/16d, voxelY2/16d, voxelZ2/16d), facing);
	}
	
	private AxisAlignedBB getBounds(AxisAlignedBB shape, EnumFacing facing) {
		switch (facing) {
		case SOUTH:
			return new AxisAlignedBB(shape.minX, shape.minY, 1d - shape.maxZ, shape.maxX, shape.maxY, 1d - shape.minZ);
		case WEST:
			return new AxisAlignedBB(shape.minZ, shape.minY, shape.minX, shape.maxZ, shape.maxY, shape.maxX);
		case EAST:
			return new AxisAlignedBB(1d - shape.minZ, shape.minY, shape.minX, 1d - shape.maxZ, shape.maxY, shape.maxX);			
		case NORTH:default:
			return shape;
		}
	}
	
	private String showBounds(AxisAlignedBB shape) {
		return ("(" + shape.minX + "d, " + shape.minY + "d, " + shape.minZ + "d, " + shape.maxX + "d, " + shape.maxY + "d, " + shape.maxZ +"d)");
	}
	
	protected void showAllBounds(double voxelX1, double voxelY1, double voxelZ1, double voxelX2, double voxelY2, double voxelZ2) {
		AxisAlignedBB shape = getBounds(voxelX1, voxelY1, voxelZ1, voxelX2, voxelY2, voxelZ2, EnumFacing.NORTH);
		Helper.sendLog("\nNORTH : " + showBounds(shape)
		 + "\nSOUTH :" + showBounds(this.getBounds(shape, EnumFacing.SOUTH))
		 + "\nWEST : " + showBounds(this.getBounds(shape, EnumFacing.WEST))
		 + "\nEAST : " + showBounds(this.getBounds(shape, EnumFacing.EAST)));	
	}
}
