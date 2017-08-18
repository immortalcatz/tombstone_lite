package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.tombstone.block.BlockTombstone;
import ovh.corail.tombstone.block.IPlayerGrave;

public class TombstoneActivatedMessage implements IMessage {
	private BlockPos currentPos; 

	public TombstoneActivatedMessage() {
	}

	public TombstoneActivatedMessage(BlockPos currentPos) {
		this.currentPos = currentPos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.currentPos.toLong());
	}
	
	public static class Handler implements IMessageHandler<TombstoneActivatedMessage, IMessage> {
		@Override
		public IMessage onMessage(final TombstoneActivatedMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayerMP player = ctx.getServerHandler().player;
					World world = player.world;
					IBlockState state = world.getBlockState(message.currentPos);
					Block block = state.getBlock();
					if (block instanceof IPlayerGrave) {
						if (player.getServer().isDedicatedServer()) {
							DedicatedServer server = (DedicatedServer)player.getServer();
							if (server.isBlockProtected(world, message.currentPos, player)) {
				            	block.onBlockActivated(world, message.currentPos, state, player, EnumHand.MAIN_HAND, player.getHorizontalFacing(), 0.5f, 0.5f, 0.5f);
				            }
						}
					}
 				}
			});
			return null;
		}
	}
}
