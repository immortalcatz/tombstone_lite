package ovh.corail.tombstone.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAdvancement003 extends Item {
	private static final String name = "advancement_003";

	/** constructor */
	public ItemAdvancement003() {
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
