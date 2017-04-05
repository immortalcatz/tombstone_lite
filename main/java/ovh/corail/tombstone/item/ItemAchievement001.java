package ovh.corail.tombstone.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAchievement001 extends Item {
	private static final String name = "achievement_001";

	/** constructor */
	public ItemAchievement001() {
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
