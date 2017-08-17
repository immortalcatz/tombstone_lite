package ovh.corail.tombstone.handler;

import java.util.HashMap;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import ovh.corail.tombstone.core.ModProps;
import ovh.corail.tombstone.packet.SoundMessage;

public class SoundHandler {
	private static HashMap<String, SoundEvent> sounds = new HashMap<String, SoundEvent>(); 

	private SoundHandler() {}
	
	public static void registerSounds() { 
		registerSound("magic_use01");
	}
	
	private static void registerSound(String soundName) {
		final ResourceLocation soundLoc = new ResourceLocation(ModProps.MOD_ID, soundName);
		SoundEvent currentSound = new SoundEvent(soundLoc).setRegistryName(soundLoc);
		ForgeRegistries.SOUND_EVENTS.register(currentSound);
		sounds.put(soundName, currentSound);
	}
	
	public static SoundEvent getSound(String soundName) {
		SoundEvent currentSound = sounds.get(soundName);
		return currentSound != null ? currentSound : SoundEvent.REGISTRY.getObject(new ResourceLocation("minecraft", soundName));
	}
	
	public static boolean playSoundAt(String soundName, WorldClient world, BlockPos currentPos) {
		if (!world.isRemote) { return false; }
		SoundEvent currentSound = getSound(soundName);
		if (currentSound == null) { return false; }
		world.playSound(currentPos, currentSound, SoundCategory.PLAYERS, 1.0f, 1.0f, true);
		return true;
	}
	
	public static boolean playSoundAllAround(String name, World world, BlockPos currentPos, double range) {
		if (world.isRemote) { return false; }
		PacketHandler.INSTANCE.sendToAllAround(new SoundMessage(name, currentPos), 
				new TargetPoint(world.provider.getDimension(), currentPos.getX(), currentPos.getY(), currentPos.getZ(), range));
		return true;
	}
}
