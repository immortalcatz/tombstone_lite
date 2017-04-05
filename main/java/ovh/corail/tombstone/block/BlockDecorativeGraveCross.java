package ovh.corail.tombstone.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

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
	protected boolean canSilkHarvest() {
		return true;
	}
}
