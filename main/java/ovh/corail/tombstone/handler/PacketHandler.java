package ovh.corail.tombstone.handler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.tombstone.core.ModProps;
import ovh.corail.tombstone.packet.TeleportMessage;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ModProps.MOD_ID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(TeleportMessage.Handler.class, TeleportMessage.class, id++, Side.CLIENT);
		//INSTANCE.registerMessage(ClientMessage.Handler.class, ClientMessage.class, id++, Side.CLIENT);
	}
}
