package ovh.corail.tombstone.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.item.ItemGraveKey;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class BlockTombstone extends BlockDecorativeTombstone<TileEntityTombstone> {
	protected final static String name = "tombstone";
	public BlockTombstone() {
		super(name);
		setCreativeTab(null);
		setBlockUnbreakable();
		setResistance(18000000.0f);
		setLightLevel(0.5f);
	}

	@Override
	public TileEntityTombstone createTileEntity(World world, IBlockState state) {
		return new TileEntityTombstone();
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) { return true; }
		TileEntityTombstone tile = getTileEntity(world, pos);
		if (tile == null) { return false; }
		boolean valid = false;
		/** creative mode or no need of access */
		if (!tile.getNeedAccess() || player.isCreative()) {
			valid = true;
		/** if the tomb is linked with a key */
		} else if (player.getHeldItemMainhand().getItem() == Main.grave_key) {
			/** only check the same position/dimension for the tomb and the key */
			ItemStack stack = player.getHeldItemMainhand();
			if (ItemGraveKey.getTombPos(stack).compareTo(tile.getPos()) == 0 && ItemGraveKey.getTombDim(stack)==world.provider.getDimension()) {
				valid = true;
			} else {
				Helper.sendMessage("message.open_grave.wrong_key", player, true);
			}
		}
		if (valid) {
			tile.giveInventory(player);
		} else {
			Helper.sendMessage("message.open_grave.need_key", player, true);
		}
		return valid;
	}
	
	@Override
	public boolean canHoldSoul() {
		return false;
	}
	
}
