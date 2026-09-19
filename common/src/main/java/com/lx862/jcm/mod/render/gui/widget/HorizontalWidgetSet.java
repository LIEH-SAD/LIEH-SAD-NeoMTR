package com.lx862.jcm.mod.render.gui.widget;

import com.lx862.jcm.mod.render.RenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a set of widget within a defined area.
 * Able to tile the widgets horizontally.<br>
 * Unlike {@link WidgetSet}, this widget does not automatically distribute the widget evenly and you can control the width of each element.
 */
public class HorizontalWidgetSet extends AbstractWidget implements WidgetWithChildren, RenderHelper {
    public final int widgetXMargin;
    private final List<AbstractWidget> widgets = new ArrayList<>();

    public HorizontalWidgetSet(int widgetXMargin) {
        super(0, 0, 0, 20, Component.empty());
        this.widgetXMargin = widgetXMargin;
    }
    public HorizontalWidgetSet() {
        this(5);
    }

    /**
     * Set the X, Y, Width and Height of the widget. This is required to be called as the default width is 0
     */
    public void setXYSize(int x, int y, int width, int height) {
        setPosition(x, y);
        setSize(width, height);
        positionWidgets();
    }

    public void addWidget(AbstractWidget widget) {
        widgets.add(widget);
    }

    @Override
    public int getWidth() {
        int total = 0;
        for(AbstractWidget widget : widgets) {
            total += widget.getWidth();
            total += widgetXMargin;
        }
        if(total > 1) {
            total -= widgetXMargin;
        }
        return total;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        for(AbstractWidget widget : widgets) {
            widget.updateNarration(narrationElementOutput);
        }
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        for(AbstractWidget mappedWidget : widgets) {
            mappedWidget.extractRenderState(guiGraphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        for(AbstractWidget widget : widgets) {
            widget.visible = visible;
        }
        this.visible = visible;
    }

    @Override
    public void setX(int newX) {
        super.setX(newX);
        positionWidgets();
    }

    @Override
    public void setY(int newY) {
        super.setY(newY);
        positionWidgets();
    }

    private void positionWidgets() {
        int accX = 0;

        for (AbstractWidget widget : widgets) {
            widget.setX(getX() + accX);
            widget.setY(getY());
            accX += widget.getWidth();
            accX += widgetXMargin;
//            int clampedWidth = Math.min(width - accX, widget.getWidth());
//            widget.setWidth(clampedWidth);
//            accX += clampedWidth + widgetXMargin;
        }
    }
}
