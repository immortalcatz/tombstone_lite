package nightkosh.gravestone.api;

import nightkosh.gravestone.api.death_handler.ICatDeathHandler;
import nightkosh.gravestone.api.death_handler.ICustomEntityDeathHandler;
import nightkosh.gravestone.api.death_handler.IDogDeathHandler;
import nightkosh.gravestone.api.death_handler.IHorseDeathHandler;
import nightkosh.gravestone.api.death_handler.IPlayerDeathHandler;
import nightkosh.gravestone.api.death_handler.IVillagerDeathHandler;
import nightkosh.gravestone.api.grave_items.ICatItems;
import nightkosh.gravestone.api.grave_items.IDogItems;
import nightkosh.gravestone.api.grave_items.IHorseItems;
import nightkosh.gravestone.api.grave_items.IPlayerItems;
import nightkosh.gravestone.api.grave_items.IVillagerItems;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public interface IGraveGeneration {

    public void addPlayerDeathHandler(IPlayerDeathHandler playerDeathHandler);

    public void addVillagerDeathHandler(IVillagerDeathHandler villagerDeathHandler);

    public void addDogDeathHandler(IDogDeathHandler dogDeathHandler);

    public void addCatDeathHandler(ICatDeathHandler catDeathHandler);

    public void addHorseDeathHandler(IHorseDeathHandler horseDeathHandler);


    public void addPlayerItemsHandler(IPlayerItems playerItems);

    public void addVillagerItemsHandler(IVillagerItems villagerItems);

    public void addDogItemsHandler(IDogItems dogItems);

    public void addCatItemsHandler(ICatItems catItems);

    public void addHorseItemsHandler(IHorseItems horseItems);

    /**
     * It can be used to generate graves for your Entities
     */
    public void addCustomEntityDeathHandler(ICustomEntityDeathHandler customEntityDeathHandler);
}
