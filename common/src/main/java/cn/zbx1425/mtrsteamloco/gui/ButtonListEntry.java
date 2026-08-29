package cn.zbx1425.mtrsteamloco.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ButtonListEntry extends AbstractConfigListEntry<Object> {

    private final Button button;

    public ButtonListEntry(Component fieldName, Component resetButtonKey, Supplier<Object> defaultValue, Consumer<Object> saveConsumer) {
        this(fieldName, widget -> {
        });
    }

    public static ButtonListEntry createCenteredInstance(Component text, Button.OnPress onPress) {
        return new ButtonListEntry(text, onPress);
    }

    private ButtonListEntry(Component fieldName, Button.OnPress onPress) {
        super(fieldName, false);
        this.button = Button.builder(fieldName, onPress).bounds(0, 0, 150, 20).build();
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
        int buttonWidth = Math.min(150, entryWidth - 8);
        this.button.setX(x + (entryWidth - buttonWidth) / 2);
        this.button.setY(y + (getItemHeight() - this.button.getHeight()) / 2);
        this.button.setWidth(buttonWidth);
        this.button.active = isEditable();
        this.button.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(button);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(button);
    }
}