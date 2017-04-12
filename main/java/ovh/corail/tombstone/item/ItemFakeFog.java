package ovh.corail.tombstone.item;

import net.minecraft.item.Item;

public class ItemFakeFog extends Item {
	private static final String name = "fake_fog";
	
	public ItemFakeFog() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(null);
		setMaxStackSize(1);
	}
}
