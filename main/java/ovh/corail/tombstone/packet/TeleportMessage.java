package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TeleportMessage implements IMessage {

	public TeleportMessage() {
	}
	
	public static class Handler implements IMessageHandler<TeleportMessage, IMessage> {
		@Override
		public IMessage onMessage(final TeleportMessage message, final MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Minecraft.getMinecraft().ingameGUI.resetPlayersOverlayFooterHeader();
 				}
			});
			return null;
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {	
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}
}