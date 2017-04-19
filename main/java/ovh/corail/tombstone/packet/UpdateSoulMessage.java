package ovh.corail.tombstone.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateSoulMessage implements IMessage {
	private BlockPos currentPos; 
	private boolean hasSoul;

	public UpdateSoulMessage() {
	}

	public UpdateSoulMessage(BlockPos currentPos, boolean hasSoul) {
		this.currentPos = currentPos;
		this.hasSoul = hasSoul;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
		this.hasSoul = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.currentPos.toLong());
		buf.writeBoolean(this.hasSoul);
	}
	
	public static class Handler implements IMessageHandler<UpdateSoulMessage, IMessage> {
		@Override
		public IMessage onMessage(final UpdateSoulMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = ctx.getServerHandler().player.world;
					IBlockState state = world.getBlockState(message.currentPos);
					world.addWeatherEffect( new EntityLightningBolt(world, message.currentPos.getX(), message.currentPos.getY(), message.currentPos.getZ(), true));
					world.setBlockState(message.currentPos, state.withProperty(PropertyBool.create("has_soul"), message.hasSoul), 2);
 				}
			});
			return null;
		}
	}
}
