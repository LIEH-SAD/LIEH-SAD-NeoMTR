package com.lx862.jcm.mod.render.text;

import com.lx862.jcm.mod.JCMClient;
import com.lx862.jcm.mod.render.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

/**
 * <h2>Text Rendering Manager</h2>
 * <p>The text rendering manager for JCM serves as a wrapper for {@link VanillaTextRenderer} depending on whether fallback rendering is used.</p>
 * <p>All text rendering in the 3D world should be performed in this class.</p>
 */
public class TextRenderingManager implements RenderHelper {
    private Font font;
    /**
     * Initialize the texture atlas for TextureTextRenderer<br>
     * Nothing will be performed if fallback mode is enabled / using {@link VanillaTextRenderer}
     */
    public static void initialize() {
        //if(JCMClient.getConfig().useNewTextRenderer) {
//            FontManager.initialize();
//            TextureTextRenderer.initialize();
        //} else if(TextureTextRenderer.initialized()) {
//            TextureTextRenderer.close();
        //}
    }

    //public static void draw(GuiGraphicsExtractor guiGraphics, TextInfo text, double x, double y) {
//        drawInternal(graphicsHolder, guiDrawing, text, null, x, y);
    //}

    public static void draw(PoseStack poseStack, MultiBufferSource bufferSource, TextInfo text, double x, double y) {
        drawInternal(poseStack, bufferSource, text, x, y);
    }

    private static void drawInternal(PoseStack poseStack, MultiBufferSource bufferSource, TextInfo text, double x, double y) {
        if(text.getContent().isEmpty()) return;
        VanillaTextRenderer.draw(poseStack, bufferSource, text, x, y);
    }

    public static int getTextWidth(TextInfo text) {
        return VanillaTextRenderer.getTextWidth(Minecraft.getInstance().font, text);
    }
}
