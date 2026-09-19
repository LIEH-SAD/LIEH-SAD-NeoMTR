package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.render.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.MTR;
import mtr.client.IDrawing;
import mtr.mappings.BlockEntityMapper;
import mtr.util.UtilitiesClient;
import mtr.render.MoreRenderLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;

public class StaticSignalLightRenderer<T extends BlockEntityMapper> extends JCMBlockEntityRenderer<T, StaticSignalLightRenderer.SignalLightRenderState> {
    private final boolean drawOnTop;
    private final int color;

    public StaticSignalLightRenderer(BlockEntityRenderDispatcher dispatcher, int color, boolean drawOnTop) {
        super(dispatcher);
        this.color = color;
        this.drawOnTop = drawOnTop;
    }

    @Override
    public SignalLightRenderState createRenderState() {
        return new SignalLightRenderState();
    }

    @Override
    public void renderCurated(SignalLightRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        final Direction facing = state.facing;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        poseStack.pushPose();
        UtilitiesClient.rotateYDegrees(poseStack, -facing.toYRot());
        final float y = drawOnTop ? 0.4375F : 0.0625F;
        submitNodeCollector.submitCustomGeometry(poseStack, MoreRenderLayers.getLight(MTR.id("textures/block/white.png"), false), (pose, vertexConsumer) -> {
            IDrawing.drawTexture(pose, vertexConsumer, -0.125F, y, -0.19375F, 0.125F, y + 0.25F, -0.19375F, facing.getOpposite(), color, RenderHelper.MAX_RENDER_LIGHT);
        });
        poseStack.popPose();

        poseStack.popPose();
    }

    public static class SignalLightRenderState extends JCMBlockEntityRenderer.JCMRenderState {
    }
}
