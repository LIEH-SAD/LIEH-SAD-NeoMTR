package com.lx862.jcm.mod.registry;

import com.lx862.jcm.loader.JCMRegistry;
import com.lx862.jcm.mod.item.ItemDRLAPG;
import com.lx862.jcm.mod.util.JCMLogger;
import mtr.BrandNewEpicRegistryObject;
import net.minecraft.world.item.Item;

public class Items {
    public static final BrandNewEpicRegistryObject<Item> APG_DOOR_DRL = new BrandNewEpicRegistryObject<>(resourceKey -> new ItemDRLAPG(ItemDRLAPG.EnumPSDAPGItem.DOOR, ItemDRLAPG.EnumPSDAPGType.APG, new Item.Properties().setId(resourceKey)));
    public static final BrandNewEpicRegistryObject<Item> APG_GLASS_DRL = new BrandNewEpicRegistryObject<>(resourceKey -> new ItemDRLAPG(ItemDRLAPG.EnumPSDAPGItem.GLASS, ItemDRLAPG.EnumPSDAPGType.APG, new Item.Properties().setId(resourceKey)));
    public static final BrandNewEpicRegistryObject<Item> APG_GLASS_END_DRL = new BrandNewEpicRegistryObject<>(resourceKey -> new ItemDRLAPG(ItemDRLAPG.EnumPSDAPGItem.GLASS_END, ItemDRLAPG.EnumPSDAPGType.APG, new Item.Properties().setId(resourceKey)));

    public static void register() {
        // We just load the class and it will be registered, nothing else
        JCMLogger.debug("Registering items...");
        JCMRegistry.registerItem("apg_door_drl", APG_DOOR_DRL, ItemGroups.MAIN);
        JCMRegistry.registerItem("apg_glass_drl", APG_GLASS_DRL, ItemGroups.MAIN);
        JCMRegistry.registerItem("apg_glass_end_drl", APG_GLASS_END_DRL, ItemGroups.MAIN);
    }
}
