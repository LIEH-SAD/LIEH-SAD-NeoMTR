package cn.zbx1425.mtrsteamloco.gui.entries;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// A combined slider + text field entry. Mode 0 shows a draggable slider,
// mode 1 shows a text field for precise input.
public class SliderOrTextFieldListEntry extends AbstractConfigListEntry<Float> {

    private final float min, max;
    private final Function<Float, Component> valueFormatter;
    private final Consumer<Float> saveConsumer;
    private final Function<String, Optional<Float>> parseFunction;
    private final Consumer<Integer> modeSaveConsumer;

    private float value;
    private int mode;

    private final SliderWidget slider;
    private final EditBox textField;
    private final Button modeButton;
    private boolean textFieldFocused;

    public SliderOrTextFieldListEntry(
            Component fieldName,
            Supplier<Component> resetButtonKey,
            float value,
            float min,
            float max,
            float step,
            Function<Float, Component> valueFormatter,
            Consumer<Float> saveConsumer,
            Function<String, Optional<Float>> parseFunction,
            int mode,
            Consumer<Integer> modeSaveConsumer
    ) {
        super(fieldName, false);
        this.min = min;
        this.max = max;
        this.valueFormatter = valueFormatter;
        this.saveConsumer = saveConsumer;
        this.parseFunction = parseFunction;
        this.mode = mode;
        this.modeSaveConsumer = modeSaveConsumer;
        this.saveCallback = saveConsumer;
        this.value = value;

        this.slider = new SliderWidget(0, 0, 150, 20, progress(value));
        this.slider.updateMessage();
        this.textField = new EditBox(Minecraft.getInstance().font, 0, 0, 150, 20, Component.empty());
        this.textField.setMaxLength(16);
        this.textField.setValue(valueFormatter.apply(value).getString());
        this.modeButton = Button.builder(Component.empty(), btn -> setMode(1 - this.mode)).bounds(0, 0, 20, 20).build();
        updateModeButtonMessage();
    }

    private float progress(float value) {
        if (max <= min) return 0;
        return Mth.clamp((value - min) / (max - min), 0, 1);
    }

    private float valueFromProgress(double progress) {
        return (float) (min + (max - min) * progress);
    }

    private void updateModeButtonMessage() {
        modeButton.setMessage(Component.translatable(mode == 0
                ? "gui.mtrsteamloco.eye_candy.mode_input"
                : "gui.mtrsteamloco.eye_candy.mode_slider"));
    }

    public void setMode(int newMode) {
        this.mode = newMode;
        updateModeButtonMessage();
        if (modeSaveConsumer != null) {
            modeSaveConsumer.accept(newMode);
        }
    }

    public void setValue(float newValue) {
        this.value = newValue;
        this.slider.setValue(progress(newValue));
        this.slider.updateMessage();
        if (!this.textField.isFocused()) {
            this.textField.setValue(valueFormatter.apply(newValue).getString());
        }
    }

    private void applyValue(float newValue) {
        newValue = Mth.clamp(newValue, min, max);
        if (Math.abs(this.value - newValue) < 1e-5f) return;
        this.value = newValue;
        this.slider.setValue(progress(newValue));
        this.slider.updateMessage();
        this.textField.setValue(valueFormatter.apply(newValue).getString());
        if (saveConsumer != null) {
            saveConsumer.accept(newValue);
        }
    }

    private void commitText() {
        parseFunction.apply(textField.getValue()).ifPresent(this::applyValue);
    }

    @Override
    public void tick() {
        super.tick();
        if (textFieldFocused && !textField.isFocused()) {
            commitText();
        }
        textFieldFocused = textField.isFocused();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (mode == 1 && textField.isFocused() && (event.key() == 257 || event.key() == 335)) {
            commitText();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public Optional<Float> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Component displayedFieldName = getDisplayedFieldName();
        graphics.text(Minecraft.getInstance().font, displayedFieldName, x + 2, y + 6, getPreferredTextColor());
        int nameWidth = Minecraft.getInstance().font.width(displayedFieldName);
        int widgetX = x + 2 + nameWidth + 6;
        int widgetWidth = Math.max(60, x + entryWidth - widgetX - 24);
        this.modeButton.setX(x + entryWidth - 22);
        this.modeButton.setY(y + (getItemHeight() - 20) / 2);
        this.modeButton.active = isEditable();
        if (this.mode == 0) {
            this.slider.active = isEditable();
            this.slider.setX(widgetX);
            this.slider.setY(y + (getItemHeight() - 20) / 2);
            this.slider.setWidth(widgetWidth);
            this.slider.extractRenderState(graphics, mouseX, mouseY, delta);
        } else {
            this.textField.setEditable(isEditable());
            this.textField.setX(widgetX);
            this.textField.setY(y + (getItemHeight() - 20) / 2);
            this.textField.setWidth(widgetWidth);
            this.textField.extractRenderState(graphics, mouseX, mouseY, delta);
        }
        this.modeButton.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        List<GuiEventListener> list = new ArrayList<>();
        list.add(modeButton);
        list.add(mode == 0 ? slider : textField);
        return list;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        List<NarratableEntry> list = new ArrayList<>();
        list.add(modeButton);
        list.add(mode == 0 ? slider : textField);
        return list;
    }

    private class SliderWidget extends AbstractSliderButton {

        protected SliderWidget(int x, int y, int width, int height, double value) {
            super(x, y, width, height, Component.empty(), value);
        }

        @Override
        public void updateMessage() {
            setMessage(valueFormatter.apply(SliderOrTextFieldListEntry.this.value));
        }

        @Override
        protected void applyValue() {
            SliderOrTextFieldListEntry.this.applyValue(valueFromProgress(this.value));
        }

        @Override
        public void setValue(double value) {
            super.setValue(value);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (!isEditable()) return false;
            return super.keyPressed(event);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
            if (!isEditable()) return false;
            return super.mouseDragged(event, deltaX, deltaY);
        }
    }
}