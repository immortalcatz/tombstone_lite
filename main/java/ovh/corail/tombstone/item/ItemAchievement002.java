package ovh.corail.tombstone.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAchievement002 extends Item {
	private static final String name = "achievement_002";

	/** constructor */
	public ItemAchievement002() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(null);
		setMaxStackSize(1);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
}
