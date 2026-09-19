package com.lx862.jcm.mod.render.gui.screen.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Generic GUI Screen, use {@link ScreenBase#withPreviousScreen} to reference the previously opened screen that would be opened again after closing.
 */
public abstract class ScreenBase extends Screen {
    private Screen previousScreen = null;
    
    public ScreenBase(Component title) {
        super(title);
    }
    
    public ScreenBase withPreviousScreen(Screen screen) {
        this.previousScreen = screen;
        return this;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(previousScreen);
    }
}