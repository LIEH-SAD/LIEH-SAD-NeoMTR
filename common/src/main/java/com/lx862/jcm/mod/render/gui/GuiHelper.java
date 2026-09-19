package com.lx862.jcm.mod.render.gui;

import com.lx862.jcm.mod.JCMClient;
import com.lx862.jcm.mod.data.Pair;
import com.lx862.jcm.mod.render.gui.widget.WidgetWithChildren;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public interface GuiHelper {
    int MAX_CONTENT_WIDTH = 400;
    int BOTTOM_ROW_MARGIN = 6;
    int MAX_BUTTON_WIDTH = 375;

    static void drawTexture(GuiGraphicsExtractor guiGraphics, Identifier identifier, double x, double y, double width, double height) {
        Pair<Float, Float> uv = JCMClient.getMcMetaManager().getUV(identifier);
        drawTexture(guiGraphics, identifier, x, y, width, height, 0, uv.getLeft(), 1, uv.getRight());
    }

    static void drawTexture(GuiGraphicsExtractor guiGraphics, Identifier location, double x, double y, double width, double height, float u1, float v1, float u2, float v2) {
        guiGraphics.blit(location, (int)x, (int)y, (int)(x + width), (int)(y + height), u1, u2, v1, v2);
    }

    static void drawRectangle(GuiGraphicsExtractor guiGraphics, double x, double y, double width, double height, int color) {
        guiGraphics.fill((int)x, (int)y, (int)(x + width), (int)(y + height), color);
    }

    /**
     * Draw text that would shift back and fourth if there's not enough space to display
     * Similar to the scrollable text added in Minecraft 1.19.4
     * @param guiGraphics Graphics holder
     * @param text The text to display
     * @param elapsed The time elapsed, this would dictate the scrolling animation speed
     * @param startX The start X where your text should be clipped. (Measure from the left edge of your window)
     * @param textX The text X that would be rendered
     * @param textY The text Y that would be rendered
     * @param maxWidth The maximum width allowed for your text
     * @param color Color of the text
     * @param shadow Whether text should be rendered with shadow
     */
    static void drawScrollableText(GuiGraphicsExtractor guiGraphics, MutableComponent text, double elapsed, int startX, int textX, int textY, int maxWidth, int color, boolean shadow) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        Matrix3x2fStack poseStack = guiGraphics.pose();

        if(textWidth > maxWidth) {
            double slideProgress = ((Math.sin(elapsed / 2)) / 2) + 0.5;
            poseStack.pushMatrix();
            poseStack.translate((float)(-slideProgress * (textWidth - maxWidth)), 0);
            guiGraphics.enableScissor(startX, 0, startX + maxWidth, guiGraphics.guiHeight());
            guiGraphics.text(font, text, textX, textY, color, shadow);
            guiGraphics.disableScissor();
            poseStack.popMatrix();
        } else {
            guiGraphics.text(font, text, textX, textY, color, shadow);
        }
    }

    /**
     * This clamps the inputted button width to {@link GuiHelper#MAX_BUTTON_WIDTH } to prevent button texture glitch on 1.16 if the button is too wide
     * @param width The desired button width
     * @return The clamped button width
     */
    static int getButtonWidth(double width) {
        return (int)Math.min(MAX_BUTTON_WIDTH, width);
    }

    static void setWidgetVisibility(AbstractWidget widget, boolean bl) {
        if(widget instanceof WidgetWithChildren widgetWithChildren) {
            widgetWithChildren.setVisible(bl);
        } else {
            widget.visible = bl;
        }
    }
}
