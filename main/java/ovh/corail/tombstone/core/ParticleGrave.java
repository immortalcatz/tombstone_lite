package ovh.corail.tombstone.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleGrave extends Particle {
	public ParticleGrave(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,	double ySpeedIn, double zSpeedIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		particleTexture = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Main.fake_fog);
		this.particleAlpha = 0.2f;
		float f = 2.5F;
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        // TODO modify color this.setRBGColorF(21f/255f, 2f/255f, 24f/255f);
        float f1 = 1F - (float)(Math.random() * 0.30000001192092896D);
        this.particleRed = f1;
        this.particleGreen = f1;
        this.particleBlue = f1;
        this.particleScale *= 2F;
        this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
        this.particleMaxAge = (int)((float)this.particleMaxAge * 2.5F);
        this.canCollide = true;
	}
	
	public ParticleGrave(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
	}
	
	public int getFXLayer() {
        return 1;
    }
	
	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

}
