package com.lx862.jcm.mod.render.gui.widget;

import com.lx862.jcm.mod.render.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;

/**
 * Represent a row in {@link ListViewWidget}
 */
public class CategoryItem extends AbstractListItem {
    public final MutableComponent title;

    public CategoryItem(MutableComponent title, int height) {
        super(height);
        this.title = title;
    }

    public CategoryItem(MutableComponent title) {
        super();
        this.title = title.withStyle(ChatFormatting.UNDERLINE);
    }

    /* */
    @Override
    public void draw(GuiGraphicsExtractor guiGraphics, int entryX, int entryY, int width, int height, int mouseX, int mouseY, boolean widgetVisible, double elapsed, float tickDelta) {
        GuiHelper.drawRectangle(guiGraphics, entryX, entryY, width, this.height, 0x77999999);
        guiGraphics.centeredText(Minecraft.getInstance().font, title, (entryX + width / 2), entryY - (8/2) + (this.height / 2), ARGB_WHITE);
    }

    @Override
    public boolean matchQuery(String searchTerm) {
        return Objects.equals(searchTerm, "") || title.getString().contains(searchTerm);
    }

    @Override
    public void positionChanged(int entryX, int entryY) {
    }

    @Override
    public void hidden() {
    }

    @Override
    public void shown() {
    }
}
