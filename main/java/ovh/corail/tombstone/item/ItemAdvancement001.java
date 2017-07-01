package ovh.corail.tombstone.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAdvancement001 extends Item {
	private static final String name = "advancement_001";

	/** constructor */
	public ItemAdvancement001() {
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
