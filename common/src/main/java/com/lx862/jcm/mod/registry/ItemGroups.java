package com.lx862.jcm.mod.registry;

import com.lx862.jcm.loader.JCMRegistry;
import com.lx862.jcm.mod.Constants;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ItemGroups {
    public static Wrapper MAIN = new Wrapper(Constants.id("main"), () -> new ItemStack(Blocks.MTR_STAIRS.get()));
    public static Wrapper PIDS = new Wrapper(Constants.id("pids"), () -> new ItemStack(Blocks.RV_PIDS.get()));

    public static class Wrapper {
        public final Identifier resourceLocation;
        private final Supplier<CreativeModeTab> creativeModeTabSupplier;
        private CreativeModeTab creativeModeTab;

        public Wrapper(Identifier resourceLocation, Supplier<ItemStack> itemSupplier) {
            this.resourceLocation = resourceLocation;
            this.creativeModeTabSupplier = JCMRegistry.registerCreativeModeTab(resourceLocation, itemSupplier);
        }

        public CreativeModeTab get() {
            if (this.creativeModeTab == null) {
                this.creativeModeTab = this.creativeModeTabSupplier.get();
            }

            return this.creativeModeTab;
        }

        public Wrapper() {
            this.resourceLocation = Identifier.parse("");
            this.creativeModeTabSupplier = net.minecraft.world.item.CreativeModeTabs::getDefaultTab;
        }
    }
}
