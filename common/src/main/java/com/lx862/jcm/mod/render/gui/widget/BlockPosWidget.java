package com.lx862.jcm.mod.render.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class BlockPosWidget extends AbstractWidget implements WidgetWithChildren {
    private final CoordTextField posXTextField;
    private final CoordTextField posYTextField;
    private final CoordTextField posZTextField;
    public BlockPosWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.posXTextField = new CoordTextField(0, 0, width / 3, height, -29999999, 29999999, 0);
        this.posYTextField = new CoordTextField(width / 3, 0, width / 3, height, -29999999, 29999999, 0);
        this.posZTextField = new CoordTextField((width / 3) * 2, 0, width / 3, height, -29999999, 29999999, 0);

        this.posXTextField.setResponder(this::setPosition);
        this.posYTextField.setResponder(this::setPosition);
        this.posZTextField.setResponder(this::setPosition);
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

    public void setActive(boolean active) {
        this.posXTextField.setEditable(active);
        this.posYTextField.setEditable(active);
        this.posZTextField.setEditable(active);
        this.posXTextField.active = active;
        this.posYTextField.active = active;
        this.posZTextField.active = active;
        this.active = active;
    }

    @Override
    public void setVisible(boolean visible) {
        this.posXTextField.setVisible(visible);
        this.posYTextField.setVisible(visible);
        this.posZTextField.setVisible(visible);
        this.visible = visible;
    }

    public void positionWidgets() {
        int perWidth = getWidth() / 3;
        this.posXTextField.setX(getX());
        this.posYTextField.setX(getX() + perWidth);
        this.posZTextField.setX(getX() + perWidth + perWidth);
        this.posXTextField.setY(getY());
        this.posYTextField.setY(getY());
        this.posZTextField.setY(getY());
        this.posXTextField.setWidth(perWidth);
        this.posYTextField.setWidth(perWidth);
        this.posZTextField.setWidth(perWidth);
    }

    public void addWidget(Consumer<AbstractWidget> callback) {
        callback.accept(posXTextField);
        callback.accept(posYTextField);
        callback.accept(posZTextField);
    }

    public void setBlockPos(BlockPos newPos) {
        this.posXTextField.setValue(newPos.getX());
        this.posYTextField.setValue(newPos.getY());
        this.posZTextField.setValue(newPos.getZ());
    }

    public BlockPos getBlockPos() {
        return new BlockPos(posXTextField.getNumber(), posYTextField.getNumber(), posZTextField.getNumber());
    }

    private void setPosition(String str) {
        str = str.trim();
        String[] strSplit = str.split("\\s+");

        if(!str.isEmpty() && strSplit.length > 1) {
            for(int i = 0; i < strSplit.length; i++) {
                CoordTextField field = null;
                if(i == 0) field = posXTextField;
                if(i == 1) field = posYTextField;
                if(i == 2) field = posZTextField;

                try {
                    int parsed = Integer.parseInt(strSplit[i]);
                    if(field != null) field.setValue(parsed);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.posXTextField.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.posYTextField.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.posZTextField.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
