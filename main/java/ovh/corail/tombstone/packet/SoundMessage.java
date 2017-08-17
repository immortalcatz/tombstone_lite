package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.tombstone.handler.SoundHandler;

public class SoundMessage implements IMessage {
	private String soundName;
	private BlockPos currentPos;
	
	public SoundMessage() {	
	}
	
	public SoundMessage(String soundName, BlockPos currentPos) {	
		this.soundName = soundName;
		this.currentPos = currentPos;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		soundName = ByteBufUtils.readUTF8String(buf);
		currentPos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, soundName);
		buf.writeLong(currentPos.toLong());
	}

	public static class Handler implements IMessageHandler<SoundMessage, IMessage> {
		@Override
		public IMessage onMessage(final SoundMessage message, final MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					WorldClient world = Minecraft.getMinecraft().world;
					SoundHandler.playSoundAt(message.soundName, world, message.currentPos);
				}
			});
			return null;
		}
	}
}
