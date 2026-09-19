package com.lx862.jcm.mod.render;

import com.lx862.jcm.mod.JCMClient;
import com.lx862.jcm.mod.data.Pair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

public interface RenderHelper {
    int MAX_RENDER_LIGHT = 0xF000F0;
    int ARGB_BLACK = 0xFF000000;
    int ARGB_WHITE = 0xFFFFFFFF;
    int ARGB_RED = 0xFF0000FF;
    int MAX_ALPHA = 0xFF000000;
    int lineHeight = 9;

    static void drawText(GuiGraphicsExtractor guiGraphics, MutableComponent text, int x, int y, int textColor) {
        final Font font = Minecraft.getInstance().font;
        guiGraphics.text(font, text, x, y, textColor, false);
    }

    static void drawCenteredText(GuiGraphicsExtractor guiGraphics, MutableComponent text, int x, int y, int textColor) {
        final Font font = Minecraft.getInstance().font;
        int w = font.width(text);
        guiGraphics.text(font, text, x - (w / 2), y, textColor, false);
    }

    static void drawLine(PoseStack poseStack, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        vertexConsumer.addVertex(poseStack.last(), x1, y1, z1).setColor(color).setNormal(poseStack.last(), 0, 1, 0);
        vertexConsumer.addVertex(poseStack.last(), x2, y2, z2).setColor(color).setNormal(poseStack.last(), 0, 1, 0);
    }

    default boolean inRectangle(double targetX, double targetY, int rectX, int rectY, int rectW, int rectH) {
        return (targetX > rectX && targetX <= rectX + rectW) && (targetY > rectY && targetY <= rectY + rectH);
    }

    static void scaleToFit(PoseStack poseStack, int targetW, double maxW, boolean keepAspectRatio) {
        scaleToFit(poseStack, targetW, maxW, keepAspectRatio, 0);
    }

    static void scaleToFit(PoseStack poseStack, double targetW, double maxW, boolean keepAspectRatio, double height) {
        height = height / 2;
        double scaleX = Math.min(1, maxW / targetW);
        if (scaleX < 1) {
            if (keepAspectRatio) {
                poseStack.translate(0, height / 2.0, 0);
                poseStack.scale((float) scaleX, (float) scaleX, (float) scaleX);
                poseStack.translate(0, -height / 2.0, 0);
            } else {
                poseStack.scale((float) scaleX, 1, 1);
            }
        }
    }

    static void scaleToFit(Matrix3x2fStack pose, int targetW, double maxW, boolean keepAspectRatio) {
        scaleToFit(pose, targetW, maxW, keepAspectRatio, 0);
    }

    static void scaleToFit(Matrix3x2fStack pose, double targetW, double maxW, boolean keepAspectRatio, double height) {
        height = height / 2;
        double scaleX = Math.min(1, maxW / targetW);
        if (scaleX < 1) {
            if (keepAspectRatio) {
                pose.translate(0, (float) (height / 2.0));
                pose.scale((float) scaleX, (float) scaleX);
                pose.translate(0, (float) (-height / 2.0));
            } else {
                pose.scale((float) scaleX, 1);
            }
        }
    }

    static void drawTexture(PoseStack poseStack, VertexConsumer vertexConsumer, Identifier textureId, float x, float y, float z, float width, float height, Direction facing, int color, int light) {
        drawTexture(poseStack.last(), vertexConsumer, textureId, x, y, z, width, height, facing, color, light);
    }

    static void drawTexture(PoseStack poseStack, VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, float u1, float v1, float u2, float v2, Direction facing, int color, int light) {
        drawTexture(poseStack.last(), vertexConsumer, x, y, z, width, height, u1, v1, u2, v2, facing, color, light);
    }

    static void drawTexture(PoseStack poseStack, VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, Direction facing, int color, int light) {
        drawTexture(poseStack.last(), vertexConsumer, x, y, z, width, height, facing, color, light);
    }

    /**
     * Draw a texture to the world, it will automatically look up for mcmeta data and set the appropriate UV.<br>
     * Use this method if you need support for drawing animated McMeta textures
     */
    static void drawTexture(PoseStack.Pose pose, VertexConsumer vertexConsumer, Identifier textureId, float x, float y, float z, float width, float height, Direction facing, int color, int light) {
        Pair<Float, Float> uv = JCMClient.getMcMetaManager().getUV(textureId);
        drawTextureRaw(pose, vertexConsumer, x, y, z, x + width, y + height, z, 0, uv.getLeft(), 1, uv.getRight(), facing, color, light);
    }

    static void drawTexture(PoseStack.Pose pose, VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, float u1, float v1, float u2, float v2, Direction facing, int color, int light) {
        drawTextureRaw(pose, vertexConsumer, x, y, z, x + width, y + height, z, u1, v1, u2, v2, facing, color, light);
    }

    static void drawTexture(PoseStack.Pose pose, VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, Direction facing, int color, int light) {
        drawTextureRaw(pose, vertexConsumer, x, y, z, x + width, y + height, z, 0, 0, 1, 1, facing, color, light);
    }

    static void drawTextureRaw(PoseStack.Pose pose, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, float u1, float v1, float u2, float v2, Direction facing, int color, int light) {
        drawTextureRaw(pose, vertexConsumer, x1, y2, z1, x2, y2, z2, x2, y1, z2, x1, y1, z1, u1, v1, u2, v2, facing, color, light);
    }

    static void drawTextureRaw(PoseStack.Pose pose, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, Direction facing, int color, int light) {
        Matrix4f matrix4f = pose.pose();
        Vec3i vector3i = facing.getUnitVec3i();
        final int normalX = vector3i.getX();
        final int normalY = vector3i.getY();
        final int normalZ = vector3i.getZ();

        vertexConsumer.addVertex(matrix4f, x1, y1, z1).setColor(color).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, normalX, normalY, normalZ);
        vertexConsumer.addVertex(matrix4f, x2, y2, z2).setColor(color).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, normalX, normalY, normalZ);
        vertexConsumer.addVertex(matrix4f, x3, y3, z3).setColor(color).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, normalX, normalY, normalZ);
        vertexConsumer.addVertex(matrix4f, x4, y4, z4).setColor(color).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, normalX, normalY, normalZ);
    }
}
