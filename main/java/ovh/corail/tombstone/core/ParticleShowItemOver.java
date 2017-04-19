package ovh.corail.tombstone.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ParticleShowItemOver extends Particle {
	
	public ParticleShowItemOver(World worldIn, Item item, double xCoordIn, double yCoordIn, double zCoordIn) {
		super(worldIn, xCoordIn + 0.5d, yCoordIn + 0.85f, zCoordIn + 0.5d, 0, 0, 0);
		particleTexture = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item);
		this.particleAlpha = 0.5f;
		this.particleScale = 0.5f;
		this.particleMaxAge = 100;
		this.canCollide = false;
		float f1 = 1F - (float)(Math.random() * 0.30000001192092896D);
        this.particleRed = f1;
        this.particleGreen = f1;
        this.particleBlue = f1;
	}
	
	@Override
	public int getFXLayer() {
        return 1;
    }
	
	@Override
	public void onUpdate() {
        if (particleAge++ >= particleMaxAge) {
            this.setExpired();
        }
	}
	
	@Override
	public boolean shouldDisableDepth() {
		return false;
	}

}
