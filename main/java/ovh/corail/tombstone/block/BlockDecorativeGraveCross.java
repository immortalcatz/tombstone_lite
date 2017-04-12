package ovh.corail.tombstone.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.handler.ConfigurationHandler;

public class BlockDecorativeGraveCross extends BlockFacing {
	public static final String name = "decorative_grave_cross";

	public BlockDecorativeGraveCross() {
		super(Material.ROCK, name);
		this.setHardness(2.0F);
		this.setResistance(30.0F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.25f, 0f, 0.25f, 0.75f, 0.5625f, 0.75f);
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		super.randomDisplayTick(state, world, pos, rand);
		if (!ConfigurationHandler.showFog) { return; }
		Helper.produceTombstoneParticles(pos);
	}
}
