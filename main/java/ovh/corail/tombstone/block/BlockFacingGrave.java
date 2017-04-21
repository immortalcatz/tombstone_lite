package ovh.corail.tombstone.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.core.NBTStackHelper;
import ovh.corail.tombstone.core.ParticleShowItemOver;
import ovh.corail.tombstone.handler.AchievementHandler;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.PacketHandler;
import ovh.corail.tombstone.item.ISoulConsumption;
import ovh.corail.tombstone.item.ItemGraveKey;
import ovh.corail.tombstone.item.ItemScrollOfRecall;
import ovh.corail.tombstone.packet.UpdateSoulMessage;

public class BlockFacingGrave extends Block {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool HAS_SOUL = PropertyBool.create("has_soul");
	
	public BlockFacingGrave(Material materialIn, String name) {
		super(materialIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		/** default values */
		this.setCreativeTab(Main.tabTombstone);
		this.blockSoundType = SoundType.STONE;
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HAS_SOUL, false));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		super.randomDisplayTick(state, world, pos, rand);
		/** add particle to show soul */
		if (state.getValue(HAS_SOUL)) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleShowItemOver(world, Main.soul, pos.getX(), pos.getY(), pos.getZ()));
		/** update has_soul property */
		} else if (world.isThundering() && world.thunderingStrength > 0.5f && !(this instanceof BlockTombstone) && Helper.getRandom(1, 3000) <= 1) {
			PacketHandler.INSTANCE.sendToServer(new UpdateSoulMessage(pos, true));
		}
		/** fog particle */
		if (!ConfigurationHandler.showFog) { return; }
		if (!(this instanceof BlockTombstone) && ((double)world.getCelestialAngle(0.0f) < 0.245d || (double)world.getCelestialAngle(0.0f) > 0.755d)) { return; }
		Main.proxy.produceTombstoneParticles(pos);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) { return true; }
		/** activated by a scroll of recall to define the loc of teleport */
		if (state.getValue(HAS_SOUL)) {
			ItemStack stack = player.getHeldItemMainhand();
			if (!stack.isEmpty() && stack.getItem() instanceof ISoulConsumption) {
				ISoulConsumption item = ((ISoulConsumption)stack.getItem());
				if (!item.isEnchanted(stack)) {
					if (item.setEnchant(world, pos, player, stack)) {
						Helper.sendMessage("La magie de la tombe se répand dans votre objet.", player, false);
						world.setBlockState(pos, state.withProperty(HAS_SOUL, false), 2);
					} else {
						Helper.sendMessage("La magie ne semble pas opérer.", player, false);
					}
				} else {
					Helper.sendMessage("L'objet est déjà enchanté.", player, false);
				}
			}
		}
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
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
}
