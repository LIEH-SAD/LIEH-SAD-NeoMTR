package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.block.entity.PIDSProjectorBlockEntity;
import com.lx862.jcm.mod.render.RenderHelper;
import com.lx862.jcm.mod.util.JCMUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.render.MoreRenderLayers;
import mtr.util.UtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PIDSProjectorRenderer extends PIDSRenderer<PIDSProjectorBlockEntity, PIDSProjectorRenderer.ProjectorRenderState> {
    private static final float LINE_WIDTH = 2.0f;

    public PIDSProjectorRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public ProjectorRenderState createRenderState() {
        return new ProjectorRenderState();
    }

    @Override
    public void extractRenderState(PIDSProjectorBlockEntity blockEntity, ProjectorRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.scale = (float)blockEntity.getScale();
        state.offsetX = blockEntity.getOffsetX();
        state.offsetY = blockEntity.getOffsetY();
        state.offsetZ = blockEntity.getOffsetZ();
        state.rotateX = (float)blockEntity.getRotateX();
        state.rotateY = (float)blockEntity.getRotateY();
        state.rotateZ = (float)blockEntity.getRotateZ();
        state.showOutline = JCMUtil.playerHoldingBrush(Minecraft.getInstance().player);
    }

    @Override
    public void renderPIDS(ProjectorRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        final float scale = state.scale;
        final boolean showOutline = state.showOutline;

        UtilitiesClient.rotateYDegrees(poseStack, 90);
        poseStack.translate(-0.5 + state.offsetX, -0.5 - state.offsetY, 0.5 + state.offsetZ);

        UtilitiesClient.rotateXDegrees(poseStack, state.rotateX);
        UtilitiesClient.rotateYDegrees(poseStack, state.rotateY);
        UtilitiesClient.rotateZDegrees(poseStack, state.rotateZ);

        // Draw projection effect
        if(showOutline && state.rotateX == 0 && state.rotateY == 0 && state.rotateZ == 0) {
            final float offsetX = (float)(0.5 - state.offsetX);
            final float offsetY = (float)(0.5 + state.offsetY);
            final float offsetZ = (float)(-0.5 - state.offsetZ);

            poseStack.pushPose();
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, lineConsumer) -> {
                drawLine(pose, lineConsumer, offsetX, offsetY, offsetZ, 0, 0, 0, 0xFFFF0000);
                drawLine(pose, lineConsumer, offsetX, offsetY, offsetZ, 0 + (1.785f * scale), 0, 0, 0xFFFF0000);
                drawLine(pose, lineConsumer, offsetX, offsetY, offsetZ, 0, 0 + (1 * scale), 0, 0xFFFF0000);
                drawLine(pose, lineConsumer, offsetX, offsetY, offsetZ, 0 + (1.785f * scale), 0 + (1 * scale), 0, 0xFFFF0000);
            });
            poseStack.popPose();
        }

        poseStack.scale(1/76F, 1/76F, 1/76F);
        poseStack.scale(scale, scale, scale);

        final MultiBufferSource.BufferSource bufferSource = getImmediateBufferSource();
        state.preset.render(state.blockEntity, poseStack, bufferSource, state.world, state.blockEntity.getBlockPos(), state.facing, state.arrivals, state.rowHidden, state.tickDelta, 0, 0, 136, 76);
        bufferSource.endBatch();

        // Border
        if(showOutline) {
            final Direction facing = state.facing;
            submitNodeCollector.submitCustomGeometry(poseStack, MoreRenderLayers.getLight(Constants.id("textures/block/light_1.png"), false), (pose, vertexConsumer) -> {
                RenderHelper.drawTexture(pose, vertexConsumer, -8, -1, 0.1f, 138, 78, facing, 0xFFFF0000, MAX_RENDER_LIGHT);
            });
        }
    }

    private static void drawLine(PoseStack.Pose pose, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        vertexConsumer.addVertex(pose, x1, y1, z1).setLineWidth(LINE_WIDTH).setColor(color).setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(pose, x2, y2, z2).setLineWidth(LINE_WIDTH).setColor(color).setNormal(pose, 0, 1, 0);
    }

    public static class ProjectorRenderState extends PIDSRenderer.PIDSRenderState {
        float scale;
        double offsetX;
        double offsetY;
        double offsetZ;
        float rotateX;
        float rotateY;
        float rotateZ;
        boolean showOutline;
    }
}