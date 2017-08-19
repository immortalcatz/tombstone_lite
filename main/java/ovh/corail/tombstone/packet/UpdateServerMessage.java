package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.tombstone.block.BlockGrave.GraveType;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.handler.DeathHandler;
import ovh.corail.tombstone.handler.PacketHandler;

public class UpdateServerMessage implements IMessage {
	private String graveType;
	private boolean update_client;

	public UpdateServerMessage() {
	}

	public UpdateServerMessage(String graveType, boolean update_client) {
		this.graveType = graveType;
		this.update_client = update_client;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		graveType = ByteBufUtils.readUTF8String(buf);
		update_client = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, graveType);
		buf.writeBoolean(update_client);
	}
	
	public static class Handler implements IMessageHandler<UpdateServerMessage, IMessage> {
		@Override
		public IMessage onMessage(final UpdateServerMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayerMP player = ctx.getServerHandler().player;
					DeathHandler.getInstance().setFavoriteGrave(player.getUniqueID(), GraveType.valueOf(message.graveType));		
					if (!message.update_client) {
						/** advancement choose_grave_type on config changed */
						Helper.grantAdvancement(player, "tutorial/choose_grave_type");
					} else {
						/** spawn protection datas to client */
						if (player.getServer().isDedicatedServer()) {
							DedicatedServer server = (DedicatedServer)player.getServer();
							BlockPos spawnPos = server.getWorld(0).getSpawnPoint();
							int range = server.getSpawnProtectionSize();
							PacketHandler.INSTANCE.sendTo(new UpdateClientMessage(spawnPos, range), player);
						}
					}
 				}
			});
			return null;
		}
	}
}
