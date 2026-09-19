package com.lx862.jcm.mod.render.gui.widget;

import com.lx862.jcm.mod.render.gui.GuiHelper;
import com.lx862.jcm.mod.render.RenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Represent a row in {@link ListViewWidget}
 */
public abstract class AbstractListItem implements RenderHelper, GuiHelper {
    public final int height;
    private double hoverOpacity = 0;

    public AbstractListItem(int height) {
        this.height = height;
    }

    public AbstractListItem() {
        this(22);
    }

    /* */
    public void draw(GuiGraphicsExtractor guiGraphics, int entryX, int entryY, int width, int height, int mouseX, int mouseY, boolean widgetVisible, double elapsed, float tickDelta) {
        drawBackground(guiGraphics, entryX, entryY, width, mouseX, mouseY, widgetVisible, elapsed, tickDelta);
    }

    public abstract boolean matchQuery(String searchTerm);
    public abstract void positionChanged(int entryX, int entryY);
    public abstract void hidden();
    public abstract void shown();

    private void drawBackground(GuiGraphicsExtractor guiGraphics, int entryX, int entryY, int width, int mouseX, int mouseY, boolean widgetVisible, double elapsed, float tickDelta) {
        double highlightFadeSpeed = (tickDelta / 4);
        boolean entryHovered = inRectangle(mouseX, mouseY, entryX, entryY, width, this.height);
        hoverOpacity = entryHovered ? Math.min(1, hoverOpacity + highlightFadeSpeed) : Math.max(0, hoverOpacity - highlightFadeSpeed);

        if(hoverOpacity > 0) drawListEntryHighlight(guiGraphics, entryX, entryY, width, height);
    }

    private void drawListEntryHighlight(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        int highlightAlpha = (int)(100 * hoverOpacity);
        int highlightColor = (highlightAlpha << 24) | (150 << 16) | (150 << 8) | 150;

        GuiHelper.drawRectangle(guiGraphics, x, y, width, height, highlightColor);
    }
}
