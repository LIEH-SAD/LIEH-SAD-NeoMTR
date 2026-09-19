package com.lx862.jcm.mod.scripting.jcm.pids;

import com.lx862.jcm.mod.util.TextUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.MTR;
import mtr.MTRClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.lx862.jcm.mod.render.RenderHelper.MAX_RENDER_LIGHT;

public class TextWrapper extends PIDSDrawCall {
    public String str;
    public Identifier fontId;
    public boolean shadow;
    public boolean styleItalic;
    public boolean styleBold;
    public double scale;
    public int overflowMode;
    public int alignment;
    public int color;

    protected TextWrapper() {
        super(100, 25);
        this.alignment = -1;
        this.scale = 1;
        this.fontId = MTR.id("mtr");
    }

    public static TextWrapper create() {
        return new TextWrapper();
    }

    public static TextWrapper create(String comment) {
        return create();
    }

    public TextWrapper text(String str) {
        this.str = str;
        return this;
    }

    public TextWrapper scale(double i) {
        this.scale = i;
        return this;
    }

    public TextWrapper leftAlign() {
        this.alignment = -1;
        return this;
    }

    public TextWrapper centerAlign() {
        this.alignment = 0;
        return this;
    }

    public TextWrapper rightAlign() {
        this.alignment = 1;
        return this;
    }

    public TextWrapper shadowed() {
        this.shadow = true;
        return this;
    }

    public TextWrapper stretchXY() {
        this.overflowMode = 1;
        return this;
    }

    public TextWrapper scaleXY() {
        this.overflowMode = 2;
        return this;
    }

    public TextWrapper wrapText() {
        this.overflowMode = 3;
        return this;
    }

    public TextWrapper marquee() {
        this.overflowMode = 4;
        return this;
    }

    public TextWrapper fontMC() {
        this.fontId = null;
        return this;
    }

    public TextWrapper font(String fontId) {
        return font(Identifier.parse(fontId));
    }

    public TextWrapper font(Identifier fontId) {
        this.fontId = fontId;
        return this;
    }

    public TextWrapper italic() {
        this.styleItalic = true;
        return this;
    }

    public TextWrapper bold() {
        this.styleBold = true;
        return this;
    }

    public TextWrapper color(int color) {
        this.color = color;
        return this;
    }

    @Override
    protected void validate() {
        if(str == null) throw new IllegalArgumentException("Text must be filled");
    }

    @Override
    protected void drawTransformed(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing) {
        poseStack.scale((float)scale, (float)scale, (float)scale);

        List<MutableComponent> texts = new ArrayList<>();
        final MutableComponent originalText = getFormattedText(str);

        int actualW = Minecraft.getInstance().font.width(originalText);
        int actualH = 9;

        if(overflowMode == 1) { // Stretch XY
            if(actualW > w) {
                poseStack.scale((float)(w / actualW), 1, 1);
            }
            if(actualH > h) {
                poseStack.scale(1, (float)(h / actualH), 1);
            }
        } else if(overflowMode == 2) { // Scale XY
            double minScale = Math.min(actualW > w ? w / actualW : 1, actualH > h ? h / actualH : 1);
            poseStack.translate(0, (h - (actualH * minScale)) / 2, 0); // Center it vertically
            poseStack.scale((float)minScale, (float)minScale, 0);
        }

        if(overflowMode == 3) { // Wrap Text
            StringBuilder curLine = new StringBuilder();
            int wSoFar = 0;
            for(int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                wSoFar += Minecraft.getInstance().font.width(String.valueOf(c));
                if(wSoFar > w) {
                    texts.add(getFormattedText(curLine.toString()));
                    curLine = new StringBuilder(String.valueOf(c));
                    wSoFar = 0;
                } else {
                    curLine.append(c);
                }
            }
            if(!curLine.isEmpty()) {
                texts.add(getFormattedText(curLine.toString()));
            }
        } else {
            texts.add(getFormattedText(str));
        }

        if(overflowMode == 4 && actualW > w) { // Marquee
            drawMarqueeText(texts.get(0).getString(), poseStack, bufferSource, color, shadow, MAX_RENDER_LIGHT);
        } else {
            int i = 0;
            for(MutableComponent text : texts) {
                drawText(text, poseStack, bufferSource, i*9, color, shadow, MAX_RENDER_LIGHT);
                i++;
            }
        }
    }

    private void drawText(MutableComponent text, PoseStack poseStack, MultiBufferSource bufferSource, int y, int color, boolean shadow, int light) {
        int startX = 0;
        int totalW = Minecraft.getInstance().font.width(text);
        if(alignment == 0) {
            startX -= totalW / 2;
        } else if(alignment == 1) {
            startX -= totalW;
        }

        Minecraft.getInstance().font.drawInBatch(text, startX, y, color, shadow, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
    }

    private void drawMarqueeText(String str, PoseStack poseStack, MultiBufferSource bufferSource, int color, boolean shadow, int light) {
        final MutableComponent text = getFormattedText(str);
        int fullWidth = Minecraft.getInstance().font.width(text);
        int cycleDuration = str.length() * 16;
        double marqueeProgress = ((MTRClient.getGameTick() % cycleDuration) - (cycleDuration/2.0)) / (cycleDuration/2.0);

        double wSoFar = fullWidth * -marqueeProgress;
        for(int i = 0; i < str.length(); i++) {
            String st = String.valueOf(str.charAt(i));
            final MutableComponent tx = getFormattedText(st);

            if(wSoFar >= 0 && wSoFar <= w) {
                poseStack.pushPose();
                poseStack.translate(wSoFar, 0, 0);
                Minecraft.getInstance().font.drawInBatch(tx, 0, 0, color, shadow, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                poseStack.popPose();
            }

            wSoFar += Minecraft.getInstance().font.width(tx);
        }
    }

    private MutableComponent getFormattedText(String str) {
        final Style fontStyle;
        if(fontId != null) {
            fontStyle = TextUtil.withFontStyle(fontId).withBold(styleBold).withItalic(styleItalic);
        } else {
            fontStyle = Style.EMPTY.withBold(styleBold).withItalic(styleItalic);
        }

        return Component.literal(str).withStyle(fontStyle);
    }
}
