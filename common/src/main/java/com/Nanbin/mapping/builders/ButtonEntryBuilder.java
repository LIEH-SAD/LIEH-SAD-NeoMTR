package com.Nanbin.mapping.builders;

import com.Nanbin.mapping.ClothConfigExtra;
import com.Nanbin.mapping.entries.ButtonListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The button counterpart of the builders handed out by
 * {@link me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl}: it builds a
 * {@link ButtonListEntry} instead of an entry that holds a value.
 *
 * <pre>{@code
 * category.addEntry(ClothConfigExtra.startScreenButton(builder.entryBuilder(),
 *         Text.translatable("config.open_another_screen"), parent -> new MyScreen(parent)).build());
 * }</pre>
 */
public class ButtonEntryBuilder extends FieldBuilder<Object, ButtonListEntry, ButtonEntryBuilder> {

    private Component buttonText;
    private Button.OnPress onPress = button -> {
    };
    @Nullable
    private Supplier<Optional<Component[]>> tooltipSupplier;
    private ButtonListEntry.Alignment alignment = ButtonListEntry.Alignment.CENTER;
    private int width = ButtonListEntry.DEFAULT_BUTTON_WIDTH;
    private int height = ButtonListEntry.DEFAULT_ENTRY_HEIGHT;

    public ButtonEntryBuilder(Component resetButtonKey, Component fieldNameKey) {
        super(resetButtonKey, fieldNameKey);
        this.buttonText = fieldNameKey;
    }

    /** Reuses the reset button key of the given entry builder, just like the vanilla builders do. */
    public static ButtonEntryBuilder create(ConfigEntryBuilder entryBuilder, Component fieldNameKey) {
        return new ButtonEntryBuilder(entryBuilder.getResetButtonKey(), fieldNameKey);
    }

    /** Text drawn on the button, in case it should differ from the field name used for searching. */
    public ButtonEntryBuilder setButtonText(Component buttonText) {
        this.buttonText = buttonText;
        return this;
    }

    /** Action that runs when the button is pressed. */
    public ButtonEntryBuilder setOnPress(Runnable onPress) {
        this.onPress = button -> onPress.run();
        return this;
    }

    /** Action that runs when the button is pressed, with access to the button widget itself. */
    public ButtonEntryBuilder setOnPress(Button.OnPress onPress) {
        this.onPress = onPress;
        return this;
    }

    public ButtonEntryBuilder setTooltip(Component... tooltip) {
        this.tooltipSupplier = () -> Optional.ofNullable(tooltip);
        return this;
    }

    public ButtonEntryBuilder setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = () -> tooltip;
        return this;
    }

    public ButtonEntryBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    public ButtonEntryBuilder setAlignment(ButtonListEntry.Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public ButtonEntryBuilder setCentered() {
        return setAlignment(ButtonListEntry.Alignment.CENTER);
    }

    public ButtonEntryBuilder setLeftAligned() {
        return setAlignment(ButtonListEntry.Alignment.LEFT);
    }

    public ButtonEntryBuilder setRightAligned() {
        return setAlignment(ButtonListEntry.Alignment.RIGHT);
    }

    /** Stretches the button over the whole row. */
    public ButtonEntryBuilder setFullWidth() {
        return setAlignment(ButtonListEntry.Alignment.FILL);
    }

    /** Preferred button width, ignored by {@link #setFullWidth()}. */
    public ButtonEntryBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    /** Height of the whole entry row. */
    public ButtonEntryBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    /**
     * Makes the button open another screen. The factory receives the screen the button was
     * pressed in (normally the config screen), so the new screen can return to it.
     */
    public ButtonEntryBuilder openScreen(Function<Screen, Screen> screenFactory) {
        return setOnPress(() -> ClothConfigExtra.openScreen(screenFactory));
    }

    @NotNull
    @Override
    public ButtonListEntry build() {
        ButtonListEntry entry = new ButtonListEntry(getFieldNameKey(), buttonText, onPress,
                tooltipSupplier, alignment, width, height, isRequireRestart());
        return finishBuilding(entry);
    }
}
