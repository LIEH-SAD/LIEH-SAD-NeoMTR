package com.lx862.jcm.mod.render.gui.widget;

import com.lx862.jcm.mod.render.RenderHelper;
import com.lx862.jcm.mod.util.JCMLogger;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.screen.WidgetBetterTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.MutableComponent;

/**
 * Text Field Widget that is specifically designed for entering number only
 */
public class IntegerTextField extends WidgetBetterTextField implements RenderHelper {
    private final long min;
    private final long max;
    private final String prefix;
    private final long defaultValue;

    public IntegerTextField(int x, int y, int width, int height, long min, long max, long defaultValue, String prefix) {
        super(TextFieldFilter.INTEGER, String.valueOf(defaultValue), 16);
        setPosition(x, y);
        setSize(width, height);
        this.min = min;
        this.max = max;
        this.prefix = prefix;
        this.defaultValue = defaultValue;
    }

    public IntegerTextField(int x, int y, int width, int height, int min, int max, int defaultValue, MutableComponent prefix) {
        this(x, y, width, height, min, max, defaultValue, prefix.getString());
    }

    public IntegerTextField(int x, int y, int width, int height, int min, int max, int defaultValue) {
        this(x, y, width, height, min, max, defaultValue, (String)null);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        String prevValue = getValue();
        boolean bl = super.charTyped(event);

        try {
            String newString = getValue();
            int val = Integer.parseInt(newString);
            if(val < min || val > max) {
                JCMLogger.debug("NumericTextField: Value too large or small");
                setValue(prevValue);
                return false;
            }
        } catch (Exception e) {
            setValue(prevValue);
            return false;
        }

        return bl;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, tickDelta);
        final Font font = Minecraft.getInstance().font;

        if(prefix != null) {
            drawPrefix(guiGraphics, font);
        }

        drawUpDownButton(guiGraphics, font);
    }

    protected void drawPrefix(GuiGraphicsExtractor guiGraphics, Font font) {
        int prefixWidth = font.width(prefix);
        int prefixX = getX() - prefixWidth;
        int prefixY = getY() + (getHeight() / 2) - (9 / 2);

        guiGraphics.text(font, prefix, prefixX, prefixY, 0xFFFFFFFF, true);
    }

    protected void drawUpDownButton(GuiGraphicsExtractor guiGraphics, Font font) {
        MutableComponent upArrow = TextUtil.translatable(TextCategory.GUI, "widget.numeric_text_field.increment");
        MutableComponent dnArrow = TextUtil.translatable(TextCategory.GUI, "widget.numeric_text_field.decrement");
        int fontHeight = 9;
        int startY = (height - (fontHeight * 2));
        int upWidth = font.width(upArrow);
        int dnWidth = font.width(dnArrow);
        guiGraphics.text(font, upArrow, getX() + width - upWidth - 2, getY() + startY, 0xFFFFFFFF, false);
        guiGraphics.text(font, dnArrow, getX() + width - dnWidth - 2, getY() + startY + fontHeight, 0xFFFFFFFF, false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(visible && active && isFocused()) {
            if(scrollY > 0) {
                increment();
            } else {
                decrement();
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        MutableComponent upArrow = TextUtil.translatable(TextCategory.GUI, "widget.numeric_text_field.increment");
        MutableComponent dnArrow = TextUtil.translatable(TextCategory.GUI, "widget.numeric_text_field.decrement");
        int fontHeight = 9;
        int startY = getY() + (height - (fontHeight * 2)) / 2;
        int upWidth = Minecraft.getInstance().font.width(upArrow.getString());
        int dnWidth = Minecraft.getInstance().font.width(dnArrow.getString());

        if(inRectangle(event.x(), event.y(), getX() + width - upWidth - 2, startY, upWidth, fontHeight)) {
            increment();
        }

        if(inRectangle(event.x(), event.y(), getX() + width - dnWidth - 2, startY + fontHeight, dnWidth, fontHeight)) {
            decrement();
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    public long getNumber() {
        try {
            return Integer.parseInt(getValue());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void setValue(long value) {
        if(value < min || value > max) return;
        setValue(String.valueOf(value));
    }

    private void increment() {
        try {
            setValue(Integer.parseInt(getValue())+1);
        } catch (Exception e) {
            setValue(defaultValue);
        }
    }

    private void decrement() {
        try {
            setValue(Integer.parseInt(getValue())-1);
        } catch (Exception e) {
            setValue(defaultValue);
        }
    }
}
