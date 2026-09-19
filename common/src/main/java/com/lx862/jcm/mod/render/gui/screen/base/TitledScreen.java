package com.lx862.jcm.mod.render.gui.screen.base;

import com.lx862.jcm.mod.render.gui.GuiHelper;
import com.lx862.jcm.mod.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

public abstract class TitledScreen extends AnimatedScreen {
    public static final int TEXT_PADDING = 10;
    public static final int TITLE_SCALE = 2;
    protected double elapsed = 0;

    public TitledScreen(Component title, boolean animatable) {
        super(title, animatable);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        drawCustomBackground(guiGraphics, mouseX, mouseY, tickDelta);
        drawTitle(guiGraphics);
        drawSubtitle(guiGraphics);

        // Draw darkened header for non-animated screen.
        if(!shouldAnimate) {
            guiGraphics.fill(0, 0, width, getStartY(), 0x66000000);
            GuiHelper.drawTexture(guiGraphics, HEADER_SEPARATOR, 0, getStartY(), width, 2);
        }

        elapsed += Minecraft.getInstance().getFrameTimeNs() / 1_000_000_000.0;
    }

    public void drawCustomBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        super.extractBackground(guiGraphics, mouseX, mouseY, tickDelta);
    }

    private void drawTitle(GuiGraphicsExtractor guiGraphics) {
        int titleHeight = (RenderHelper.lineHeight * TITLE_SCALE);
        final Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(width / 2f, TEXT_PADDING);
        poseStack.translate(0, (float)(-((titleHeight + TEXT_PADDING) * (1 - animationProgress))));
        poseStack.scale(TITLE_SCALE, TITLE_SCALE);
        RenderHelper.scaleToFit(poseStack, font.width(getTitle()), width / (float)TITLE_SCALE, true);
        guiGraphics.centeredText(font, getTitle(), 0, 0, 0xFFFFFFFF);
        poseStack.popMatrix();
    }

    private void drawSubtitle(GuiGraphicsExtractor guiGraphics) {
        double titleHeight = (RenderHelper.lineHeight * TITLE_SCALE);
        Component subtitleText = getScreenSubtitle();
        final Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(width / 2f, (float)(titleHeight * animationProgress));
        poseStack.translate(0, TEXT_PADDING * 1.5f);
        RenderHelper.scaleToFit(poseStack, font.width(subtitleText), width, true);
        guiGraphics.centeredText(font, subtitleText, 0, 0, 0xFFFFFFFF);
        poseStack.popMatrix();
    }

    /**
     * @return Return the Y coordinate that is below the title and subtitle
     */
    protected int getStartY() {
        double titleHeight = RenderHelper.lineHeight * TITLE_SCALE;
        double subtitleHeight = font.lineHeight + (TEXT_PADDING);
        return TEXT_PADDING + (int)(titleHeight + subtitleHeight);
    }

    public abstract Component getScreenSubtitle();
}
