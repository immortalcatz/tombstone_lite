package ovh.corail.tombstone.item;

import net.minecraft.item.Item;

public class ItemSoul extends Item {
	private static final String name = "soul";
	
	public ItemSoul() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(null);
		setMaxStackSize(1);
	}
	
}
