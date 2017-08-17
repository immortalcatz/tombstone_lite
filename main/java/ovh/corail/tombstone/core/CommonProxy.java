package ovh.corail.tombstone.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {

	}

	public void init(FMLInitializationEvent event) {
		
	}

	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public Side getSide() {
		return Side.SERVER;
	}

	public void produceTombstoneParticles(BlockPos currentPos) {
		
	}
	
	public void useMagic(World world, BlockPos currentPos) {
		
	}
}
