package ovh.corail.tombstone.handler;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SpawnProtectionHandler {
	private static final SpawnProtectionHandler instance = new SpawnProtectionHandler();
	private boolean isActive = false;
	private BlockPos spawnPos;
	private int range;
	
	private SpawnProtectionHandler() {
	}
	
	public static SpawnProtectionHandler getInstance() {
		return instance;
	}
	
	public void setSpawnProtection(BlockPos spawnPos, int range) {
		isActive = range > 0 ? true : false;
		this.spawnPos = spawnPos;
		this.range = range;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public boolean isBlockProtected(int dimension, BlockPos currentPos) {
		if (!isActive || dimension != 0) { return false; }
        int i = MathHelper.abs(currentPos.getX() - spawnPos.getX());
        int j = MathHelper.abs(currentPos.getZ() - spawnPos.getZ());
        int k = Math.max(i, j);
        return k <= range;
	}
	
}
