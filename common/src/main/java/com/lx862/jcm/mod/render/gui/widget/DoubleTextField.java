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

import java.math.BigDecimal;

/**
 * Text Field Widget that is specifically designed for entering number only
 */
public class DoubleTextField extends WidgetBetterTextField implements RenderHelper {
    private final double min;
    private final double max;
    private final String prefix;
    private final double defaultValue;

    public DoubleTextField(int x, int y, int width, int height, double min, double max, double defaultValue, String prefix) {
        super(String.valueOf(defaultValue), 16);
        setPosition(x, y);
        setSize(width, height);
        this.min = min;
        this.max = max;
        this.prefix = prefix;
        this.defaultValue = defaultValue;
    }

    public DoubleTextField(int x, int y, int width, int height, double min, double max, double defaultValue, MutableComponent prefix) {
        this(x, y, width, height, min, max, defaultValue, prefix.getString());
    }

    public DoubleTextField(int x, int y, int width, int height, double min, double max, double defaultValue) {
        this(x, y, width, height, min, max, defaultValue, (String)null);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        String prevValue = getValue();
        boolean bl = super.charTyped(event);

        try {
            String newString = getValue();
            double val = Double.parseDouble(newString);
            if(val < min || val > max) {
                JCMLogger.debug("DoubleTextField: Value too large or small");
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

    public double getNumber() {
        try {
            return Double.parseDouble(getValue());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void setValue(double value) {
        if(value < min || value > max) return;
        setValue(String.valueOf(value));
    }

    private void increment() {
        try {
            BigDecimal result = new BigDecimal(getValue()).add(new BigDecimal("0.1"));
            setValue(result.doubleValue());
        } catch (Exception e) {
            setValue(defaultValue);
        }
    }

    private void decrement() {
        try {
            BigDecimal result = new BigDecimal(getValue()).subtract(new BigDecimal("0.1"));
            setValue(result.doubleValue());
        } catch (Exception e) {
            setValue(defaultValue);
        }
    }
}
