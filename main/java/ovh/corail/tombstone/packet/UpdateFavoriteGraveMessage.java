package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.tombstone.block.BlockGrave.GraveType;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.handler.DeathHandler;

public class UpdateFavoriteGraveMessage implements IMessage {
	private String graveType;
	private boolean config_changed;

	public UpdateFavoriteGraveMessage() {
	}

	public UpdateFavoriteGraveMessage(String graveType, boolean config_changed) {
		this.graveType = graveType;
		this.config_changed = config_changed;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		graveType = ByteBufUtils.readUTF8String(buf);
		config_changed = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, graveType);
		buf.writeBoolean(config_changed);
	}
	
	public static class Handler implements IMessageHandler<UpdateFavoriteGraveMessage, IMessage> {
		@Override
		public IMessage onMessage(final UpdateFavoriteGraveMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayerMP player = ctx.getServerHandler().player;
					DeathHandler.getInstance().setFavoriteGrave(player.getUniqueID(), GraveType.valueOf(message.graveType));
					/** advancement choose_grave_type */
					if (message.config_changed) {
						Helper.grantAdvancement(player, "tutorial/choose_grave_type");
					}
 				}
			});
			return null;
		}
	}
}
