package cn.zbx1425.mtrsteamloco.gui.entries;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import mtr.mappings.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A list entry with a button cycling through a list of translation keys.
 */
public class ButtonCycleListEntry extends TooltipListEntry<Integer> implements ContainerEventHandler {

    private final AtomicInteger index = new AtomicInteger();
    private final List<String> list;
    private final Integer original;
    private final Supplier<Integer> defaultValue;
    protected final Consumer<Integer> saveCallback;

    private final Button buttonWidget;
    private final Button resetButton;
    private final List<AbstractWidget> widgets;

    public ButtonCycleListEntry(Component fieldName, int index, List<String> list, Component resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.list = list;
        this.index.set(index);
        this.original = index;
        this.defaultValue = defaultValue;
        this.buttonWidget = Button.builder(Text.literal(""), btn -> this.index.set((this.index.get() + 1) % list.size())).bounds(0, 0, 150, 20).build();
        this.resetButton = Button.builder(resetButtonKey, widget -> this.index.set(original))
                .bounds(0, 0, Math.max(20, Minecraft.getInstance().font.width(resetButtonKey) + 6), 20).build();
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
        this.saveCallback = saveConsumer;
    }

    @Override
    public boolean isEdited() {
        return super.isEdited() || original != index.get();
    }

    @Override
    public Integer getValue() {
        return index.get();
    }

    @Override
    public Optional<Integer> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != this.index.get();
        this.resetButton.setY(y + 1);
        this.buttonWidget.active = isEditable();
        this.buttonWidget.setY(y + 1);
        this.buttonWidget.setMessage(Text.translatable(list.get(Math.min(this.index.get(), list.size() - 1))));
        final Component displayedFieldName = getDisplayedFieldName();
        graphics.text(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x + 4, y + 6, getPreferredTextColor());
        this.resetButton.setX(x + entryWidth - resetButton.getWidth() - 2);
        this.buttonWidget.setX(x + entryWidth - 150);
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        this.resetButton.extractRenderState(graphics, mouseX, mouseY, delta);
        this.buttonWidget.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void save() {
        saveCallback.accept(index.get());
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return widgets;
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {
    }

    @Override
    public GuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(GuiEventListener focused) {
    }
}
