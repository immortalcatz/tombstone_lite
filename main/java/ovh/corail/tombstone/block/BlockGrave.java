package ovh.corail.tombstone.block;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.core.ParticleShowItemOver;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.PacketHandler;
import ovh.corail.tombstone.handler.SoundHandler;
import ovh.corail.tombstone.item.ISoulConsumption;
import ovh.corail.tombstone.item.ItemGraveKey;
import ovh.corail.tombstone.packet.UpdateSoulMessage;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;
import ovh.corail.tombstone.tileentity.TileEntityWritableGrave;

public class BlockGrave<T extends TileEntityWritableGrave> extends BlockTileEntityGrave<T> {
	public static enum GraveType { GRAVE_SIMPLE, GRAVE_NORMAL, GRAVE_CROSS, TOMBSTONE }
	protected final boolean isDecorative;
	protected final GraveType graveType;
	protected final HashMap<EnumFacing,List<AxisAlignedBB>> collisions = new HashMap<EnumFacing,List<AxisAlignedBB>>();
	
	public BlockGrave(String name, GraveType graveType, boolean isDecorative) {
		super(Material.ROCK, name);
		this.isDecorative = isDecorative;
		this.graveType = graveType;
		for (EnumFacing facing : FACING.getAllowedValues()) {
			this.collisions.put(facing, Lists.newArrayList());
		}
		loadCollisions();
		if (!isDecorative) {
			setCreativeTab(null);
			setBlockUnbreakable();
			setResistance(18000000.0f);
			setLightLevel(0.5f);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		super.randomDisplayTick(state, world, pos, rand);
		/** add particle to show soul */
		if (state.getValue(HAS_SOUL)) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleShowItemOver(world, Main.soul, pos.getX(), pos.getY(), pos.getZ()));
		/** update has_soul property */
		} else if (world.isThundering() && world.thunderingStrength > 0.5f && this.canHoldSoul() && Helper.getRandom(1, ConfigurationHandler.chanceSoul) <= 1) {
			PacketHandler.INSTANCE.sendToServer(new UpdateSoulMessage(pos, true));
		}
		/** fog particle */
		if (!ConfigurationHandler.showFog) { return; }
		if (isDecorative && ((double)world.getCelestialAngle(0.0f) < 0.245d || (double)world.getCelestialAngle(0.0f) > 0.755d)) { return; }
		Main.proxy.produceTombstoneParticles(pos);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) { return true; }
		if (isDecorative) {
			if (state.getValue(HAS_SOUL)) {
				ItemStack stack = player.getHeldItemMainhand();
				if (!stack.isEmpty() && stack.getItem() instanceof ISoulConsumption) {
					ISoulConsumption item = ((ISoulConsumption)stack.getItem());
					if (!item.isEnchanted(stack)) {
						if (item.setEnchant(world, pos, player, stack)) {
							world.setBlockState(pos, state.withProperty(HAS_SOUL, false), 3);
							SoundHandler.playSoundAllAround("magic_use01", world, player.getPosition(), 10d);
							Helper.sendMessage("message.enchant_item.success", player, true);
						} else {
							Helper.sendMessage("message.enchant_item.cant_enchant", player, true);
						}
					} else {
						Helper.sendMessage("message.enchant_item.already_enchanted", player, true);
					}
				}
			}
			return true;
		} else {
			TileEntityTombstone tile = (TileEntityTombstone) getTileEntity(world, pos);
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
	}
	
	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		if (isDecorative) {
			super.onBlockExploded(world, pos, explosion);
		}
	}
	
	public boolean canHoldSoul() {
		return isDecorative;
	}
	
	@Override
	public T createTileEntity(World world, IBlockState state) {
		return (T) (isDecorative ? new TileEntityWritableGrave() : new TileEntityTombstone());
	}
	
	public GraveType getGraveType() {
		return graveType;
	}
	
	public boolean isDecorative() {
		return isDecorative;
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		EnumFacing facing = state.getValue(FACING);
		List<AxisAlignedBB> collisionList = collisions.get(facing);
		for (AxisAlignedBB collision : collisionList) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, collision);
		}
	}
	
	private void loadCollisions() {
		switch(graveType) {
		case GRAVE_CROSS:
			/** cross center */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.40625d, 0.125d, 0.6875d, 0.59375d, 0.9375d, 0.875d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.40625d, 0.125d, 0.125d, 0.59375d, 0.9375d, 0.3125d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.6875d, 0.125d, 0.40625d, 0.875d, 0.9375d, 0.59375d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.125d, 0.125d, 0.40625d, 0.3125d, 0.9375d, 0.59375d));
			/** cross border */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.21875d, 0.5625d, 0.6875d, 0.78125d, 0.75d, 0.875d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.21875d, 0.5625d, 0.125d, 0.78125d, 0.75d, 0.3125d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.6875d, 0.5625d, 0.21875d, 0.875d, 0.75d, 0.78125d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.125d, 0.5625d, 0.21875d, 0.3125d, 0.75d, 0.78125d));
			/** cross base down */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.28125d, 0.0625d, 0.5625d, 0.71875d, 0.125d, 1.0d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.28125d, 0.0625d, 0.0d, 0.71875d, 0.125d, 0.4375d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.5625d, 0.0625d, 0.28125d, 1.0d, 0.125d, 0.71875d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.0d, 0.0625d, 0.28125d, 0.4375d, 0.125d, 0.71875d));
			/** cross base top */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.34375d, 0.125d, 0.625d, 0.65625d, 0.1875d, 0.9375d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.34375d, 0.125d, 0.0625d, 0.65625d, 0.1875d, 0.375d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.625d, 0.125d, 0.34375d, 0.9375d, 0.1875d, 0.65625d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.0625d, 0.125d, 0.34375d, 0.375d, 0.1875d, 0.65625d));
			break;
		case GRAVE_NORMAL:
			/** grave box */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.1875d, 0.0625d, 0.0d, 0.8125d, 0.28125d, 1.0d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.1875d, 0.0625d, 0.0d, 0.8125d, 0.28125d, 1.0d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.0d, 0.0625d, 0.1875d, 1.0d, 0.28125d, 0.8125d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.0d, 0.0625d, 0.1875d, 1.0d, 0.28125d, 0.8125d));
			/** grave side */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.1875d, 0.25d, 0.875d, 0.8125d, 0.875d, 1.0d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.1875d, 0.25d, 0.0d, 0.8125d, 0.875d, 0.125d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.875d, 0.25d, 0.1875d, 1.0d, 0.875d, 0.8125d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.0d, 0.25d, 0.1875d, 0.125d, 0.875d, 0.8125d));
			break;
		case GRAVE_SIMPLE:
			/** grave */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.1875d, 0.0625d, 0.8125d, 0.8125d, 0.875d, 1.0d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.1875d, 0.0625d, 0.0d, 0.8125d, 0.875d, 0.1875d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.8125d, 0.0625d, 0.1875d, 1.0d, 0.875d, 0.8125d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.0d, 0.0625d, 0.1875d, 0.1875d, 0.875d, 0.8125d));
			break;
		case TOMBSTONE:default:
			/** container */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.03125d, 0.0625d, 0.21875d, 0.96875d, 0.64375d, 0.96875d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.03125d, 0.0625d, 0.03125d, 0.96875d, 0.64375d, 0.78125d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.21875d, 0.0625d, 0.03125d, 0.96875d, 0.64375d, 0.96875d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.03125d, 0.0625d, 0.03125d, 0.78125d, 0.64375d, 0.96875d));
			/** stair */
			addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.0625d, 0.0625d, 0.0625d, 0.9375d, 0.09375d, 0.21875d));
			addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.0625d, 0.0625d, 0.78125d, 0.9375d, 0.09375d, 0.9375d));
			addCollision(EnumFacing.WEST, new AxisAlignedBB(0.0625d, 0.0625d, 0.0625d, 0.21875d, 0.09375d, 0.9375d));
			addCollision(EnumFacing.EAST, new AxisAlignedBB(0.78125d, 0.0625d, 0.0625d, 0.9375d, 0.09375d, 0.9375d));
			break;
		}
		
	}
	
	private void addCollision(EnumFacing facing, AxisAlignedBB bounds) {
		List<AxisAlignedBB> listCollisions = collisions.get(facing);
		listCollisions.add(bounds);
		collisions.put(facing, listCollisions);
	}

}
