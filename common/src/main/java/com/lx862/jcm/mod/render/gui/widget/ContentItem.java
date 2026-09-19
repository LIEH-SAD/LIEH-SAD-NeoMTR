package com.lx862.jcm.mod.render.gui.widget;

import com.lx862.jcm.mod.render.gui.GuiHelper;
import com.lx862.jcm.mod.render.RenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.Objects;

import static com.lx862.jcm.mod.render.gui.widget.ListViewWidget.ENTRY_PADDING;

/**
 * Represent a row in {@link ListViewWidget}
 */
public class ContentItem extends AbstractListItem {
    public DrawIconCallback drawIconCallback = null;
    public final MutableComponent title;
    public final AbstractWidget widget;

    public ContentItem(MutableComponent title, AbstractWidget widget, int height) {
        super(height);
        this.title = title;
        this.widget = widget;
    }

    /**
     * Create a new content item
     * @param title The main text that will appear on the left side
     * @param widget The widget to be placed on the right side. Note that it shall not be added as a renderable widget, as this list content will manually render the widget.
     */
    public ContentItem(MutableComponent title, AbstractWidget widget) {
        this(title, widget, 22);
    }

    public ContentItem setIcon(Identifier textureId) {
        this.drawIconCallback = (guiGraphics, startX, startY, width, height) -> GuiHelper.drawTexture(guiGraphics, textureId, startX, startY, width, height);
        return this;
    }

    public ContentItem setIconCallback(DrawIconCallback drawIconCallback) {
        this.drawIconCallback = drawIconCallback;
        return this;
    }

    public boolean hasIcon() {
        return this.drawIconCallback != null;
    }

    @FunctionalInterface
    public interface DrawIconCallback extends RenderHelper {
        void accept(GuiGraphicsExtractor guiGraphics, int startX, int startY, int width, int height);
    }

    /* */
    public void draw(GuiGraphicsExtractor guiGraphics, int entryX, int entryY, int width, int height, int mouseX, int mouseY, boolean widgetVisible, double elapsed, float tickDelta) {
        super.draw(guiGraphics, entryX, entryY, width, height, mouseX, mouseY, widgetVisible, elapsed, tickDelta);
        drawListEntry(guiGraphics, entryX, entryY, width, mouseX, mouseY, widgetVisible, elapsed, tickDelta);
    }

    @Override
    public boolean matchQuery(String searchTerm) {
        return Objects.equals(searchTerm, "") || (title != null && title.getString().contains(searchTerm));
    }

    @Override
    public void positionChanged(int entryX, int entryY) {
        if(widget != null) {
            int offsetY = (height - widget.getHeight()) / 2;
            widget.setX(entryX - widget.getWidth());
            widget.setY(entryY + offsetY);
        }
    }

    @Override
    public void hidden() {
        if(widget != null) {
            GuiHelper.setWidgetVisibility(widget, false);
        }
    }

    @Override
    public void shown() {
        if(widget != null) {
            GuiHelper.setWidgetVisibility(widget, true);
        }
    }

    private void drawListEntry(GuiGraphicsExtractor guiGraphics, int entryX, int entryY, int width, int mouseX, int mouseY, boolean widgetVisible, double elapsed, float tickDelta) {
        if(title != null) drawListEntryDescription(guiGraphics, entryX, entryY, width, elapsed);

        if(widget != null) {
            GuiHelper.setWidgetVisibility(widget, widgetVisible);
            // Manually draw the widget to ensure it conforms to our clip area
            widget.extractRenderState(guiGraphics, mouseX, mouseY, tickDelta);
        }
    }

    private void drawListEntryDescription(GuiGraphicsExtractor guiGraphics, int entryX, int entryY, int width, double elapsed) {
        int textHeight = 9;
        int iconSize = hasIcon() ? height - ENTRY_PADDING : 0;
        int widgetWidth = widget == null ? 0 : widget.getWidth();
        int availableTextWidth = width - widgetWidth - ENTRY_PADDING - iconSize;
        int textY = (height / 2) - (textHeight / 2);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(entryX, entryY);
        guiGraphics.pose().translate(ENTRY_PADDING, 0);

        if(hasIcon()) {
            drawIconCallback.accept(guiGraphics, 0, ((height - iconSize) / 2), iconSize, iconSize);
            guiGraphics.pose().translate(iconSize + ENTRY_PADDING, 0); // Shift the text to the right
        }

        GuiHelper.drawScrollableText(guiGraphics, title, elapsed, entryX + ENTRY_PADDING + iconSize, 0, textY, availableTextWidth - iconSize - ENTRY_PADDING - ENTRY_PADDING - ENTRY_PADDING, 0xFFDDDDDD, true);
        guiGraphics.pose().popMatrix();
    }
}
