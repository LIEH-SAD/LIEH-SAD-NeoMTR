package com.lx862.jcm.mod.render.gui.widget;

import com.lx862.jcm.mod.render.gui.GuiHelper;
import com.lx862.jcm.mod.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class ListViewWidget extends AbstractScrollViewWidget implements RenderHelper, GuiHelper {
    public static final int ENTRY_PADDING = 5;
    private final List<AbstractListItem> displayedEntryList = new ArrayList<>();
    private final List<AbstractListItem> entryList = new ArrayList<>();
    private float elapsed;
    private String searchTerm = "";
    public ListViewWidget() {
        super(0, 0, 0, 0);
    }

    public void setXYSize(int x, int y, int width, int height) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        setScroll(currentScroll);
        positionWidgets(currentScroll);
        refreshDisplayedList();
    }

    public void add(MutableComponent text, AbstractWidget widget) {
        add(new ContentItem(text, widget));
    }

    public void add(AbstractListItem listItem) {
        entryList.add(listItem);
        refreshDisplayedList();
    }

    public void addCategory(MutableComponent text) {
        entryList.add(new CategoryItem(text));
        refreshDisplayedList();
    }

    public void reset() {
        this.entryList.clear();
        refreshDisplayedList();
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        refreshDisplayedList();
    }

    private void refreshDisplayedList() {
        displayedEntryList.clear();
        entryList.forEach(e -> {
            if(e.matchQuery(searchTerm)) {
                e.shown();
                displayedEntryList.add(e);
            } else {
                e.hidden();
            }
        });
        positionWidgets();

        // Update scrolling so that it will rubber-band back if scrolled area are beyond viewport
        setScroll(currentScroll);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        GuiHelper.drawRectangle(guiGraphics, getX(), getY(), width, height, 0x4F4C4C4C);
        guiGraphics.outline(getX()-1, getY()-1, getX()+getWidth()+1, getY()+getHeight()+1, 0x66FFFFFF);
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, tickDelta);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // TODO
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    @Override
    public void renderContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        elapsed += (float)(Minecraft.getInstance().getFrameTimeNs() / 1_000_000_000.0);

        int incY = 0;
        for (AbstractListItem abstractListItem : displayedEntryList) {
            int entryX = getX();
            int entryY = getY() + incY - (int) currentScroll;
            boolean widgetVisible = false;

            if (abstractListItem instanceof ContentItem) {
                ContentItem contentItem = (ContentItem) abstractListItem;
                boolean topLeftVisible = inRectangle(contentItem.widget.getX(), contentItem.widget.getY(), getX(), getY(), getWidth(), getHeight());
                boolean bottomRightVisible = inRectangle(contentItem.widget.getX() + contentItem.widget.getWidth(), contentItem.widget.getY() + contentItem.widget.getHeight(), getX(), getY(), getWidth(), getHeight());
                widgetVisible = topLeftVisible || bottomRightVisible;
            }

            abstractListItem.draw(guiGraphics, entryX, entryY, width - getScrollbarOffset(), height, mouseX, mouseY, widgetVisible, elapsed, tickDelta);
            incY += abstractListItem.height;
        }
    }

    @Override
    public void setScroll(double newScroll) {
        super.setScroll(newScroll);
        positionWidgets();
    }

    @Override
    protected int getContentHeight() {
        int entryHeight = 0;
        for(AbstractListItem entry : displayedEntryList) {
            entryHeight += entry.height;
        }
        return entryHeight;
    }

    public void positionWidgets() {
        positionWidgets(currentScroll);
    }

    private void positionWidgets(double scroll) {
        int startX = getX();
        int startY = getY();

        int incY = 0;
        for (AbstractListItem listItem : displayedEntryList) {
            int entryY = startY + incY;
            int x = (startX + width - getScrollbarOffset()) - ENTRY_PADDING;
            listItem.positionChanged(x, (int) -scroll + entryY);
            incY += listItem.height;
        }
    }
}
