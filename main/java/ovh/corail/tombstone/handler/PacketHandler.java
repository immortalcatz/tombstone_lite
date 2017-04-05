package ovh.corail.tombstone.handler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.tombstone.core.ModProps;
import ovh.corail.tombstone.packet.TakeAllMessage;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ModProps.MOD_ID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(TakeAllMessage.Handler.class, TakeAllMessage.class, id++, Side.SERVER);
		//INSTANCE.registerMessage(ClientProgressHandler.class, ClientProgress.class, id++, Side.CLIENT);
	}
}
