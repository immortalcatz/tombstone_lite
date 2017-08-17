package ovh.corail.tombstone.block;

import java.util.Date;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import ovh.corail.tombstone.tileentity.TileEntityWritableGrave;

public abstract class BlockTileEntityGrave<TE extends TileEntityWritableGrave> extends BlockFacingGrave {

	public BlockTileEntityGrave(Material material, String name) {
		super(material, name);
	}
	
	public TE getTileEntity(IBlockAccess world, BlockPos pos) {
		return (TE)world.getTileEntity(pos);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public abstract TE createTileEntity(World world, IBlockState state);
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		String engraved_name = "";
		if (stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			engraved_name = nbt.getString("engraved_name");
		}
		if (!engraved_name.isEmpty()) {
			TE tile = getTileEntity(world, pos);
			if (tile != null) {
				tile.setOwner(engraved_name, new Date().getTime());
			}
		}
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TE tile = this.getTileEntity(world, pos);
		if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
			IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
			for (int i = 0; i < inventory.getSlots(); ++i) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack.isEmpty()) { continue; }
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
	        }
		} else {
			ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, 0);
			if (tile != null) {
				ItemBlockGrave.setEngravedName(stack, tile.getOwnerName());
			}
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	 public int quantityDropped(Random random) {
		return 0;
	}
}
