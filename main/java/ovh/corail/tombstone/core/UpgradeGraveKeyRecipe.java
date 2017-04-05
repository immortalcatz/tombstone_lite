package ovh.corail.tombstone.core;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

public class UpgradeGraveKeyRecipe extends ShapelessRecipes {

	public UpgradeGraveKeyRecipe(ItemStack res, List<ItemStack> inputList) {
		super(res, inputList);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack key = ItemStack.EMPTY;
		/** search for the key */
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() == Main.grave_key) {
				key = inv.getStackInSlot(i);
				break;
			}
		}
		/** if empty or already enchant */
		if (key.isEmpty() || (key.hasTagCompound() && key.getTagCompound().getBoolean("enchant"))) {
			return ItemStack.EMPTY;
		}
		ItemStack res = key.copy();
		res.getTagCompound().setBoolean("enchant", true);
		return res;
	}
}
