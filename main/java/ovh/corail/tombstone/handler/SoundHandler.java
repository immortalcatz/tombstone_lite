package ovh.corail.tombstone.handler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import ovh.corail.tombstone.core.ModProps;

public class SoundHandler {
	public static SoundEvent magic_use01;

	public static void registerSounds() {
		magic_use01 = registerSound("magic_use01");
	}

	private static SoundEvent registerSound(String soundName) {
		final ResourceLocation soundID = new ResourceLocation(ModProps.MOD_ID, soundName);
		SoundEvent sound = new SoundEvent(soundID).setRegistryName(soundID);
		ForgeRegistries.SOUND_EVENTS.register(sound);
		return sound;
	}
}
