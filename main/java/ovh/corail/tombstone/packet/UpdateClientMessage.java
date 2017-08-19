package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.tombstone.handler.SpawnProtectionHandler;

public class UpdateClientMessage implements IMessage {
	private BlockPos spawnPos;
	private int range;

	public UpdateClientMessage() {
	}

	public UpdateClientMessage(BlockPos spawnPos, int range) {
		this.spawnPos = spawnPos;
		this.range = range;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		spawnPos = BlockPos.fromLong(buf.readLong());
		range = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(spawnPos.toLong());
		buf.writeInt(range);
	}
	
	public static class Handler implements IMessageHandler<UpdateClientMessage, IMessage> {
		@Override
		public IMessage onMessage(final UpdateClientMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					SpawnProtectionHandler.getInstance().setSpawnProtection(message.spawnPos, message.range);
 				}
			});
			return null;
		}
	}
}
