package cn.zbx1425.mtrsteamloco.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class SimpleButtonListEntry extends AbstractConfigListEntry<Object> {

    private final Button button;
    private final Button resetButton;

    public SimpleButtonListEntry(Component fieldName, Component buttonText, Button.OnPress onPress, Component resetButtonKey, Button.OnPress resetPress, Component tooltip) {
        super(fieldName, false);
        this.button = Button.builder(buttonText, onPress).bounds(0, 0, 150, 20).build();
        this.resetButton = Button.builder(resetButtonKey, resetPress)
                .bounds(0, 0, Math.max(20, Minecraft.getInstance().font.width(resetButtonKey) + 6), 20).build();
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public Optional<Component> getError() {
        return Optional.empty();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        int totalWidth = this.button.getWidth() + this.resetButton.getWidth() + 2;
        int startX = x + (entryWidth - totalWidth) / 2;
        this.button.setX(startX);
        this.button.setY(y + (getItemHeight() - this.button.getHeight()) / 2);
        this.resetButton.setX(startX + this.button.getWidth() + 2);
        this.resetButton.setY(y + (getItemHeight() - this.resetButton.getHeight()) / 2);
        this.button.active = isEditable();
        this.resetButton.active = isEditable();
        this.button.extractRenderState(graphics, mouseX, mouseY, delta);
        this.resetButton.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(button, resetButton);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(button, resetButton);
    }
}