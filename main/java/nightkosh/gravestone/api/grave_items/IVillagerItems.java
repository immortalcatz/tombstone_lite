package nightkosh.gravestone.api.grave_items;

import java.util.List;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public interface IVillagerItems {

    public List<ItemStack> addItems(EntityVillager villager, DamageSource source);

}
