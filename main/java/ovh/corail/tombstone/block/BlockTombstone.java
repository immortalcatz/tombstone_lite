package ovh.corail.tombstone.block;

import java.util.Random;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.item.ItemGraveKey;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class BlockTombstone extends BlockFacing implements ITileEntityProvider {
	private static final String name = "tombstone";
	protected static final AxisAlignedBB east = new AxisAlignedBB(0.1875f, 0f, 0.03125f, 0.8125f, 0.5f, 0.96875f);
	protected static final AxisAlignedBB north = new AxisAlignedBB(0.03125, 0f, 0.1875f, 0.96875f, 0.5f, 0.8125f);
	protected static final AxisAlignedBB west = new AxisAlignedBB(0.1875f, 0f, 0.03125f, 0.8125f, 0.5f, 0.96875f);
	protected static final AxisAlignedBB south = new AxisAlignedBB(0.03125f, 0f, 0.1875f, 0.96875f, 0.5f, 0.8125f);

	public BlockTombstone() {
		super(Material.ROCK, name);
		setCreativeTab(null);
		setBlockUnbreakable();
		setResistance(18000000.0f);
		setLightOpacity(4);
		setLightLevel(0.8F);
		isBlockContainer = true;
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityTombstone();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		if (worldIn.isRemote) { return true; }
		TileEntityTombstone tile = (TileEntityTombstone) worldIn.getTileEntity(pos);
		if (tile == null) { return false; }
		boolean valid = false;
		/** creative mode or no need of access */
		if (!tile.getNeedAccess() || playerIn.capabilities.isCreativeMode) {
			valid = true;
		/** if the tomb is linked with a key */
		} else if (playerIn.getHeldItemMainhand().getItem() == Main.grave_key) {
			/** only check the same position/dimension for the tomb and the key */
			ItemStack stack = playerIn.getHeldItemMainhand();
			if (ItemGraveKey.getTombPos(stack).compareTo(tile.getPos()) == 0 && ItemGraveKey.getTombDim(stack)==worldIn.provider.getDimension()) {
				valid = true;
			} else {
				Helper.sendMessage("gui.message.wrongKey", playerIn, true);
			}
		}
		if (valid) {
			tile.giveInventory(playerIn);
		} else {
			Helper.sendMessage("gui.message.youNeedAKey", playerIn, true);
		}
		return valid;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);
		if (enumfacing == EnumFacing.NORTH) {
			return north;
		} else if (enumfacing == EnumFacing.EAST) {
			return east;
		} else if (enumfacing == EnumFacing.SOUTH) {
			return south;
		} else if (enumfacing == EnumFacing.WEST) {
			return west;
		}
		return north;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		/** to be sure to drop the items */
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof TileEntityTombstone) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileentity);
			world.updateComparatorOutputLevel(pos, this);
		}
		if (tileentity != null) {
			world.removeTileEntity(pos);
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		super.randomDisplayTick(state, world, pos, rand);
		Helper.produceTombstoneParticles(pos);
	}
	
}
