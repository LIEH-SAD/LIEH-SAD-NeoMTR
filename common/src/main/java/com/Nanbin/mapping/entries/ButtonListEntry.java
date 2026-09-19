package com.Nanbin.mapping.entries;

import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A config entry that is nothing but a button: no field name on the left, no reset
 * button and no value that gets saved when the config is saved. Pressing it runs an
 * action right away, which makes it a good fit for entries that open another screen.
 *
 * <p>The entry keeps an ordinary row height so it lines up with the other entries.
 */
public class ButtonListEntry extends TooltipListEntry<Object> {

    public static final int DEFAULT_BUTTON_WIDTH = 150;
    public static final int DEFAULT_ENTRY_HEIGHT = 24;
    public static final int BUTTON_HEIGHT = 20;

    /** Horizontal placement of the button inside the entry row. */
    public enum Alignment {
        /** Button of {@code preferredWidth}, aligned to the left edge of the row. */
        LEFT,
        /** Button of {@code preferredWidth}, centred in the row. */
        CENTER,
        /** Button of {@code preferredWidth}, aligned to the right edge of the row. */
        RIGHT,
        /** Button stretched over the whole row. */
        FILL
    }

    private final Button button;
    private final Alignment alignment;
    private final int preferredWidth;
    private final int entryHeight;

    public ButtonListEntry(Component fieldName, Component buttonText, Button.OnPress onPress) {
        this(fieldName, buttonText, onPress, null);
    }

    public ButtonListEntry(Component fieldName, Component buttonText, Button.OnPress onPress,
                           @Nullable Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, buttonText, onPress, tooltipSupplier, Alignment.CENTER,
                DEFAULT_BUTTON_WIDTH, DEFAULT_ENTRY_HEIGHT, false);
    }

    public ButtonListEntry(Component fieldName, Component buttonText, Button.OnPress onPress,
                           @Nullable Supplier<Optional<Component[]>> tooltipSupplier, Alignment alignment,
                           int preferredWidth, int entryHeight, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.button = Button.builder(buttonText, onPress).bounds(0, 0, DEFAULT_BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.alignment = alignment;
        this.preferredWidth = Math.max(1, preferredWidth);
        this.entryHeight = Math.max(BUTTON_HEIGHT + 2, entryHeight);
    }

    public Button getButton() {
        return button;
    }

    public void setButtonText(Component buttonText) {
        button.setMessage(buttonText);
    }

    @Override
    public int getItemHeight() {
        return entryHeight;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth, int entryHeight,
                                   int mouseX, int mouseY, boolean isHovered, float delta) {
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        int buttonWidth = alignment == Alignment.FILL ? entryWidth : Math.min(preferredWidth, Math.max(1, entryWidth - 4));
        int buttonX;
        if (alignment == Alignment.LEFT) {
            buttonX = x + 2;
        } else if (alignment == Alignment.RIGHT) {
            buttonX = x + entryWidth - buttonWidth - 2;
        } else {
            buttonX = x + (entryWidth - buttonWidth) / 2;
        }
        this.button.setX(buttonX);
        this.button.setY(y + (getItemHeight() - BUTTON_HEIGHT) / 2);
        this.button.setWidth(buttonWidth);
        this.button.active = isEditable() && isEnabled();
        this.button.extractRenderState(graphics, mouseX, mouseY, delta);
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
    public List<? extends NarratableEntry> narratables() {
        return List.of(button);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(button);
    }
}
