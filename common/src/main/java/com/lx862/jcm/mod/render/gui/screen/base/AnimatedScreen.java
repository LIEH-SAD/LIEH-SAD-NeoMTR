package com.lx862.jcm.mod.render.gui.screen.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public abstract class AnimatedScreen extends ScreenBase {
    protected double linearAnimationProgress = 0;
    protected double animationProgress;
    protected boolean closing = false;
    protected final boolean shouldAnimate;

    public AnimatedScreen(Component title, boolean animatable) {
        super(title == null ? Component.literal("") : title);
        this.shouldAnimate = animatable;
        this.animationProgress = animatable ? 0 : 1;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, tickDelta);
        double frameDelta = Minecraft.getInstance().getFrameTimeNs() / 1_000_000_000.0;
        if(!shouldAnimate) {
            linearAnimationProgress = 1;
        } else {
            linearAnimationProgress = closing ? Math.max(0, linearAnimationProgress - frameDelta) : Math.min(1, linearAnimationProgress + frameDelta);

            if(linearAnimationProgress <= 0 && closing) {
                onClose();
            }
        }
        animationProgress = getEaseAnimation();
    }

    @Override
    public void onClose() {
        if(closing || !shouldAnimate) {
            super.onClose();
        } else {
            closing = true;
        }
    }

    private double getEaseAnimation() {
        double x = linearAnimationProgress;
        return 1 - Math.pow(1 - x, 5);
    }
}
