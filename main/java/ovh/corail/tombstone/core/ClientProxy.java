package ovh.corail.tombstone.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
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
	
	@Override
    public WorldServer getWorldServer(int dimId) {
		return Minecraft.getMinecraft().getIntegratedServer().worldServerForDimension(dimId);
    }
	
	@Override
	public void produceTombstoneParticles(BlockPos currentPos) {
		if (Main.proxy.getSide() != Side.CLIENT) { return; }
		double motionX = 0.0D;
		double motionY = 0.01D;
		double motionZ = 0.0D;
		WorldClient world = Minecraft.getMinecraft().world;
		for (double i = 0.2d; i <= 0.8d; i += 0.3d) {
			for (double j = 0.2d; j <= 0.8d; j += 0.3d) {		
				if (i == 0.5d && j == 0.5d) {
					continue;
				}
				ParticleGrave particle = new ParticleGrave(world, currentPos.getX()+i, currentPos.getY(), currentPos.getZ()+j, motionX, motionY, motionZ);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
		}
	}
	
}
