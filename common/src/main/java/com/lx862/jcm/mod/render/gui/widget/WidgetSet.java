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
 * Able to tile the widgets horizontally and add new rows like a grid/table.
 */
public class WidgetSet extends AbstractWidget implements WidgetWithChildren, RenderHelper {
    private final List<List<AbstractWidget>> widgetRows = new ArrayList<>();
    private final int maxWidgetHeight;
    public final int widgetXMargin;

    public WidgetSet(int maxWidgetHeight, int widgetXMargin) {
        super(0, 0, 0, 0, Component.empty());
        this.maxWidgetHeight = maxWidgetHeight;
        this.widgetXMargin = widgetXMargin;
    }
    public WidgetSet(int maxWidgetHeight) {
        this(maxWidgetHeight, 10);
    }

    /**
     * Set the position and size of the widget and reposition all added widget.
     * Make sure to call this after all widget has been added.
     */
    public void setXYSize(int x, int y, int width, int height) {
        setPosition(x, y);
        setSize(width, height);
        positionWidgets();
    }

    public void addWidget(AbstractWidget widget) {
        if(widgetRows.isEmpty()) {
            insertRow();
        }

        int lastRowIndex = widgetRows.size() - 1;
        List<AbstractWidget> lastRow = widgetRows.get(lastRowIndex);
        lastRow.add(widget);
        widgetRows.set(lastRowIndex, lastRow);
    }

    public void insertRow() {
        widgetRows.add(new ArrayList<>());
    }

    public void reset() {
        this.widgetRows.clear();
    }

    private void positionWidgets() {
        int x = getX();
        int y = getY();
        int width = getWidth() + widgetXMargin;
        int widgetHeight = Math.min(maxWidgetHeight, (int)(maxWidgetHeight * ((double)getHeight() / (widgetRows.size() * maxWidgetHeight))));

        for(int i = 0; i < widgetRows.size(); i++) {
            List<AbstractWidget> rowWidgets = widgetRows.get(i);

            double perWidgetWidth = ((double)width / rowWidgets.size()) - widgetXMargin;
            int rowY = y + (i * widgetHeight);

            for (int j = 0; j < rowWidgets.size(); j++) {
                AbstractWidget widget = rowWidgets.get(j);
                double widgetStartX = x + (j * perWidgetWidth) + (j * widgetXMargin);

                widget.setX((int)Math.round(widgetStartX));
                widget.setY(rowY);
                widget.setWidth((int)Math.round(perWidgetWidth));
            }
        }
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        for(List<AbstractWidget> row : widgetRows) {
            for(AbstractWidget widget : row) {
                widget.extractRenderState(guiGraphics, mouseX, mouseY, delta);
            }
        }
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

    @Override
    public void setVisible(boolean visible) {
        for(List<AbstractWidget> row : widgetRows) {
            for(AbstractWidget widget : row) {
                widget.visible = visible;
            }
        }
        this.visible = visible;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // TODO
    }
}
