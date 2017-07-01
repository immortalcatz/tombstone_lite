package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.ModProps;
public class TriggerAdvancementMessage implements IMessage {
	private String domain, name;
	
	public TriggerAdvancementMessage() {
	}
	
	public TriggerAdvancementMessage(String name) {
		this(ModProps.MOD_ID, name);
		
	}

	public TriggerAdvancementMessage(String domain, String name) {
		this.domain = domain;
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.domain = ByteBufUtils.readUTF8String(buf);
		this.name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, domain);
		ByteBufUtils.writeUTF8String(buf, name);
	}
	
	public static class Handler implements IMessageHandler<TriggerAdvancementMessage, IMessage> {
		@Override
		public IMessage onMessage(final TriggerAdvancementMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayerMP player = ctx.getServerHandler().player;
					Helper.triggerAdvancement(player, message.domain, message.name);
 				}
			});
			return null;
		}
	}
}

