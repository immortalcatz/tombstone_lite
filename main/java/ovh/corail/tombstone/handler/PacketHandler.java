package ovh.corail.tombstone.handler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.tombstone.core.ModProps;
import ovh.corail.tombstone.packet.SoundMessage;
import ovh.corail.tombstone.packet.TombstoneActivatedMessage;
import ovh.corail.tombstone.packet.UpdateFavoriteGraveMessage;
import ovh.corail.tombstone.packet.UpdateSoulMessage;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ModProps.MOD_ID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(UpdateSoulMessage.Handler.class, UpdateSoulMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(TombstoneActivatedMessage.Handler.class, TombstoneActivatedMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(SoundMessage.Handler.class, SoundMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(UpdateFavoriteGraveMessage.Handler.class, UpdateFavoriteGraveMessage.class, id++, Side.SERVER);
	}
}
