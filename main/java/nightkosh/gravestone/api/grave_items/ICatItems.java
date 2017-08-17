package nightkosh.gravestone.api.grave_items;

import java.util.List;

import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public interface ICatItems {

    public List<ItemStack> addItems(EntityOcelot cat, DamageSource source);

}
