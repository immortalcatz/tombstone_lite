package ovh.corail.tombstone.core;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class UpgradeGraveKeyRecipe extends ShapelessRecipes {

	public UpgradeGraveKeyRecipe(ResourceLocation group, ItemStack res, NonNullList<Ingredient> ingredients) {
		super(group.toString(), res, ingredients);
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
