package ovh.corail.tombstone.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAchievement003 extends Item {
	private static final String name = "achievement_003";

	/** constructor */
	public ItemAchievement003() {
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
