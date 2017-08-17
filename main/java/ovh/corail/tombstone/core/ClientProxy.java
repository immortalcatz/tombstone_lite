package ovh.corail.tombstone.core;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.tombstone.render.RenderWritableGrave;
import ovh.corail.tombstone.tileentity.TileEntityWritableGrave;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		/** render blocks and items */
		Helper.render();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		/** render tileentities */
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWritableGrave.class, new RenderWritableGrave());
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
	public void produceTombstoneParticles(BlockPos currentPos) {
		double motionX = 0.01d;
		double motionY = 0.0d;
		double motionZ = 0.01d;
		double motion_null = 0.0d;
		List<List<Double>> list = Lists.newArrayList();
		list.add(Lists.newArrayList(0.2d, 0.2d, -motionX, motionY, -motionZ));
		list.add(Lists.newArrayList(0.2d, 0.8d, -motionX, motionY, motionZ));
		list.add(Lists.newArrayList(0.8d, 0.2d, motionX, motionY, -motionZ));
		list.add(Lists.newArrayList(0.8d, 0.8d, motionX, motionY, motionZ));
		list.add(Lists.newArrayList(0.2d, 0.5d, -motionX, motionY, motion_null));
		list.add(Lists.newArrayList(0.8d, 0.5d, motionX, motionY, motion_null));
		list.add(Lists.newArrayList(0.5d, 0.2d, motion_null, motionY, -motionZ));
		list.add(Lists.newArrayList(0.5d, 0.8d, motion_null, motionY, motionZ));
		list.add(Lists.newArrayList(0.5d, 0.5d, motionX, motionY, motionZ));
		
		WorldClient world = Minecraft.getMinecraft().world;
		for (List<Double> values : list) {
			ParticleGrave particle = new ParticleGrave(world, currentPos.getX()+values.get(0), currentPos.getY()+0.2d, currentPos.getZ()+values.get(1), values.get(2), values.get(3), values.get(4));
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
	}
	
	@Override
	public void useMagic(World world, BlockPos currentPos) {
		Random rand = new Random();
		for (double i=0.0d;i<4.0d;i+=1.0d) {		
			world.spawnParticle(EnumParticleTypes.SPELL_INSTANT, (double) currentPos.getX()+rand.nextFloat(), (double) currentPos.getY()+rand.nextFloat(), (double) currentPos.getZ()+rand.nextFloat(), 0.0d, 0.0d, 0.0d, new int[0]);
		}
	}
	
}
