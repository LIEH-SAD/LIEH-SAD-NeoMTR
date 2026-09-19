package com.lx862.jcm.mod.render.text;

import com.lx862.jcm.mod.render.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.MutableComponent;

/**
 * Wrapper around vanilla text rendering
 */
public class VanillaTextRenderer implements RenderHelper {
    public static void draw(PoseStack poseStack, MultiBufferSource bufferSource, TextInfo text, double x, double y) {
        drawInternal(poseStack, bufferSource, Minecraft.getInstance().font, text, x, y);
    }

    private static void drawInternal(PoseStack poseStack, MultiBufferSource bufferSource, Font font, TextInfo text, double x, double y) {
        int textWidth = getTextWidth(font, text);
        double finalX = text.getTextAlignment().getX(x, textWidth);

        if(text.isForScrollingText()) {
        } else {
            MutableComponent finalText = text.toMutableComponent();
            font.drawInBatch(finalText, (int)finalX, (int)y, text.getTextColor(), false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL,  0, MAX_RENDER_LIGHT);
        }
    }

    public static int getTextWidth(Font font, TextInfo text) {
        return font.width(text.toMutableComponent());
    }
}
