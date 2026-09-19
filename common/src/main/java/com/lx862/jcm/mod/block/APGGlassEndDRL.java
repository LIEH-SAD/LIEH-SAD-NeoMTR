package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.registry.Items;
import mtr.block.BlockPSDAPGGlassEndBase;
import net.minecraft.world.item.Item;

public class APGGlassEndDRL extends BlockPSDAPGGlassEndBase {
    public APGGlassEndDRL(Properties properties) {
        super(properties);
    }

    @Override
    public Item asItem() {
        return Items.APG_GLASS_END_DRL.get();
    }

    @Override
    public boolean isAPG() {
        return true;
    }
}
