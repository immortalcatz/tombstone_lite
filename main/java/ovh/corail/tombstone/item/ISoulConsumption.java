package ovh.corail.tombstone.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISoulConsumption {
	public boolean isEnchanted(ItemStack stack);
	public boolean setEnchant(World world, BlockPos gravePos, EntityPlayer player, ItemStack stack);
}
