package ovh.corail.tombstone.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.tombstone.render.RenderTombstone;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		/** render blocks and items */
		Helper.render();
		/** render tileentities */
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTombstone.class, new RenderTombstone());
		/** register key event */
		MinecraftForge.EVENT_BUS.register(Main.grave_key);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
}
