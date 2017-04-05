package ovh.corail.tombstone.block;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.GuiHandler;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class BlockTombstone extends BlockFacing implements ITileEntityProvider {
	private static final String name = "tombstone";
	protected static final AxisAlignedBB east = new AxisAlignedBB(3.0F / 16, 0.0F / 16, 0.5F / 16, 13.0F / 16, 8.0F / 16, 15.5F / 16);;
	protected static final AxisAlignedBB north = new AxisAlignedBB(0.5F / 16, 0.0F / 16, 3.0F / 16, 15.5F / 16, 8.0F / 16, 13.0F / 16);
	protected static final AxisAlignedBB west = new AxisAlignedBB(3.0F / 16, 0.0F / 16, 0.5F / 16, 13.0F / 16, 8.0F / 16, 15.5F / 16);
	protected static final AxisAlignedBB south = new AxisAlignedBB(0.5F / 16, 0.0F / 16, 3.0F / 16, 15.5F / 16, 8.0F / 16, 13.0F / 16);

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
		if (worldIn.isRemote) { return false; }
		TileEntityTombstone tile = (TileEntityTombstone) worldIn.getTileEntity(pos);
		if (tile == null) { return false; }
		boolean valid = false;	
		if (!tile.getNeedAccess() || playerIn.capabilities.isCreativeMode) {
			valid = true;
		
		} else {
			/** decay time before access are no more needed */
			if (ConfigurationHandler.decayTime > -1) {
				int seconds = 0;
				Date deathDate = null;
				try {
					deathDate = new SimpleDateFormat("ddMMyyyy_HHmmss").parse(tile.getOwnerDeathDate());
				} catch (ParseException e) { e.printStackTrace(); }
				long timeElapsed = worldIn.getWorldTime()-deathDate.getTime();
				seconds = (int) (timeElapsed % 60);
				if (seconds > ConfigurationHandler.decayTime) {
					valid = true;
				}
			}
			/** if the tomb is linked with a key and not in creative mode */
			if (!valid && playerIn.getHeldItemMainhand().getItem() == Main.grave_key) {
				/** check the id of the tomb */
				if (playerIn.getUniqueID().equals(tile.getPlayerId())) {
					valid = true;
				} else {
					if (worldIn.isRemote) {
						Helper.sendMessage("gui.message.wrongKey", playerIn, true);
					}
				}
			} else {
				if (worldIn.isRemote) {
					Helper.sendMessage("gui.message.youNeedAKey", playerIn, true);
				}
			}
		}
		if (valid) {
			playerIn.openGui(Main.instance, GuiHandler.TOMB, worldIn, pos.getX(), pos.getY(), pos.getZ());
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
}
