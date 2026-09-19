package com.lx862.jcm.mod.render.gui.widget;

import com.lx862.jcm.mod.render.RenderHelper;
import com.lx862.jcm.mod.util.JCMLogger;
import mtr.screen.WidgetBetterTextField;
import net.minecraft.client.input.CharacterEvent;

/**
 * Text Field Widget for entering coordinates (XYZ)
 */
public class CoordTextField extends WidgetBetterTextField implements RenderHelper {
    private final long min;
    private final long max;
    private final int defaultValue;

    public CoordTextField(int x, int y, int width, int height, long min, long max, int defaultValue) {
        super(String.valueOf(defaultValue));
        setPosition(x, y);
        setSize(width, height);
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        String prevValue = getValue();
        boolean bl = super.charTyped(event);
        try {
            String newString = new StringBuilder(getValue()).insert(getCursorPosition(), event.codepointAsString()).toString().trim();

            // Position
            String[] strSplit = newString.split("\\s+");
            if(strSplit.length > 1) {
                for (String s : strSplit) {
                    if(s.trim().isEmpty()) continue;
                    try {
                        Integer.parseInt(s.trim());
                    } catch (Exception e) {
                        return bl;
                    }
                }
            }

            // Number
            int val = Integer.parseInt(newString);
            if(val < min || val > max) {
                JCMLogger.debug("NumericTextField: Value too large or small");
                setValue(prevValue);
                return false;
            }
        } catch (Exception e) {
            JCMLogger.error("", e);
            return false;
        }

        return bl;
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

    public int getNumber() {
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
