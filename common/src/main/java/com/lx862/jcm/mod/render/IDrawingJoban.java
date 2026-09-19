package com.lx862.jcm.mod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mtr.client.Config;
import mtr.data.IGui;
import mtr.mappings.Text;
import mtr.util.UtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;


/**
 * Copied from MTR's IDrawing, but just for rendering with a specific font other than the hardcoded mtr one
 */
public interface IDrawingJoban {
    static void drawStringWithFont(PoseStack matrices, Font font, MultiBufferSource.BufferSource immediate, String text, Identifier fontId, float x, float y, int light) {
        drawStringWithFont(matrices, font, immediate, text, fontId, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, x, y, -1, -1, 1, IGui.ARGB_WHITE, true, light, null);
    }

    static void drawStringWithFont(PoseStack matrices, Font font, MultiBufferSource.BufferSource immediate, String text, Identifier fontId, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, float x, float y, float maxWidth, float maxHeight, float scale, int textColor, boolean shadow, int light, DrawingCallback drawingCallback) {
        drawStringWithFont(matrices, font, immediate, text, fontId, horizontalAlignment, verticalAlignment, horizontalAlignment, x, y, maxWidth, maxHeight, scale, textColor, shadow, light, drawingCallback);
    }

    static void drawStringWithFont(PoseStack matrices, Font font, MultiBufferSource.BufferSource immediate, String text, Identifier fontId, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, IGui.HorizontalAlignment xAlignment, float x, float y, float maxWidth, float maxHeight, float scale, int textColor, boolean shadow, int light, DrawingCallback drawingCallback) {
        drawStringWithFont(matrices, font, immediate, text, fontId, horizontalAlignment, verticalAlignment, xAlignment, x, y, maxWidth, maxHeight, scale, textColor, textColor, 2, shadow, light, drawingCallback);
    }

    static void drawStringWithFont(PoseStack pose, Font font, MultiBufferSource.BufferSource immediate, String text, Identifier fontId, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, IGui.HorizontalAlignment xAlignment, float x, float y, float maxWidth, float maxHeight, float scale, int textColorCjk, int textColor, float fontSizeRatio, boolean shadow, int light, DrawingCallback drawingCallback) {
        final Font mcFont = Minecraft.getInstance().font;
        final Style style = Config.useMTRFont() ? Style.EMPTY.withFont(new FontDescription.Resource(fontId)) : Style.EMPTY;

        while (text.contains("||")) {
            text = text.replace("||", "|");
        }
        final String[] stringSplit = text.split("\\|");

        final BooleanArrayList isCJKList = new BooleanArrayList();
        final ObjectArrayList<FormattedCharSequence> orderedTexts = new ObjectArrayList<>();
        int totalHeight = 0, totalWidth = 0;
        for (final String stringSplitPart : stringSplit) {
            final boolean isCJK = IGui.isCjk(stringSplitPart);
            isCJKList.add(isCJK);

            final FormattedCharSequence orderedText = Text.literal(stringSplitPart).setStyle(style).getVisualOrderText();
            orderedTexts.add(orderedText);

            totalHeight += Math.round(IGui.LINE_HEIGHT * (isCJK ? fontSizeRatio : 1));
            final int width = (int) Math.ceil(font.width(orderedText) * (isCJK ? fontSizeRatio : 1));
            if (width > totalWidth) {
                totalWidth = width;
            }
        }

        if (maxHeight >= 0 && totalHeight / scale > maxHeight) {
            scale = totalHeight / maxHeight;
        }

        pose.pushPose();

        final float totalWidthScaled;
        final float scaleX;
        if (maxWidth >= 0 && totalWidth > maxWidth * scale) {
            totalWidthScaled = maxWidth * scale;
            scaleX = totalWidth / maxWidth;
        } else {
            totalWidthScaled = totalWidth;
            scaleX = scale;
        }
        pose.scale(1 / scaleX, 1 / scale, 1 / scale);

        float offset = verticalAlignment.getOffset(y * scale, totalHeight);
        for (int i = 0; i < orderedTexts.size(); i++) {
            final boolean isCJK = isCJKList.getBoolean(i);
            final float extraScale = isCJK ? fontSizeRatio : 1;
            if (isCJK) {
                pose.pushPose();
                pose.scale(extraScale, extraScale, 1);
            }

            final float xOffset = horizontalAlignment.getOffset(xAlignment.getOffset(x * scaleX, totalWidth), mcFont.width(orderedTexts.get(i)) * extraScale - totalWidth);

            final float shade = light == IGui.MAX_LIGHT_GLOWING ? 1 : Math.min(LightCoordsUtil.block(light) / 16F * 0.1F + 0.7F, 1);
            final int a = ((isCJK ? textColorCjk : textColor) >> 24) & 0xFF;
            final int r = (int) ((((isCJK ? textColorCjk : textColor) >> 16) & 0xFF) * shade);
            final int g = (int) ((((isCJK ? textColorCjk : textColor) >> 8) & 0xFF) * shade);
            final int b = (int) (((isCJK ? textColorCjk : textColor) & 0xFF) * shade);

            UtilitiesClient.drawInBatch(font, orderedTexts.get(i), xOffset / extraScale, offset / extraScale, (a << 24) + (r << 16) + (g << 8) + b, shadow, pose.last().pose(), immediate, 0, light);

            if (isCJK) {
                pose.popPose();
            }

            offset += IGui.LINE_HEIGHT * extraScale;
        }

        pose.popPose();

        if (drawingCallback != null) {
            final float x1 = xAlignment.getOffset(x, totalWidthScaled / scale);
            final float y1 = verticalAlignment.getOffset(y, totalHeight / scale);
            drawingCallback.drawingCallback(x1, y1, x1 + totalWidthScaled / scale, y1 + totalHeight / scale);
        }
    }

    @FunctionalInterface
    interface DrawingCallback {
        void drawingCallback(float x1, float y1, float x2, float y2);
    }
}
