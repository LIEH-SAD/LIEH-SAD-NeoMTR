package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.JCMClient;
import com.lx862.jcm.mod.render.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.client.IDrawing;
import mtr.mappings.BlockEntityMapper;
import mtr.render.RenderSignalBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

public class SignalBlockInvertedRenderer<T extends BlockEntityMapper> extends mtr.render.RenderSignalLight2Aspect<T> {
    private final int proceedColor;
    private final boolean redOnTop;
    public SignalBlockInvertedRenderer(BlockEntityRenderDispatcher dispatcher, int proceedColor, boolean redOnTop) {
        super(dispatcher, true, redOnTop, proceedColor);
        this.proceedColor = proceedColor;
        this.redOnTop = redOnTop;
    }

    @Override
    protected void drawSignal(RenderSignalBase.SignalBaseRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, RenderType renderType, Direction facing, int occupiedAspect, boolean isBackSide) {
        if (JCMClient.getConfig().disableRendering) return;

        float y = (occupiedAspect > 0) == redOnTop ? 0.4375F : 0.0625F;
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
            IDrawing.drawTexture(pose, vertexConsumer, -0.125F, y, -0.19375F, 0.125F, y + 0.25F, -0.19375F, Direction.UP, occupiedAspect > 0 ? proceedColor : 0xFFFF0000, RenderHelper.MAX_RENDER_LIGHT);
        });
    }
}
