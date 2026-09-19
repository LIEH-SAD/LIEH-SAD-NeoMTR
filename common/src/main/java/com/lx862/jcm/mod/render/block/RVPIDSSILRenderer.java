package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.block.entity.PIDSBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.util.UtilitiesClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

public class RVPIDSSILRenderer<T extends PIDSBlockEntity> extends PIDSRenderer<T, PIDSRenderer.PIDSRenderState> {
    public RVPIDSSILRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public PIDSRenderer.PIDSRenderState createRenderState() {
        return new PIDSRenderer.PIDSRenderState();
    }

    @Override
    public void renderPIDS(PIDSRenderer.PIDSRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        poseStack.translate(-0.21, -0.155, -0.650);
        UtilitiesClient.rotateXDegrees(poseStack, 22.5f);
        poseStack.scale(1/96F, 1/96F, 1/96F);

        final MultiBufferSource.BufferSource bufferSource = getImmediateBufferSource();
        state.preset.render(state.blockEntity, poseStack, bufferSource, state.world, state.blockEntity.getBlockPos(), state.facing, state.arrivals, state.rowHidden, state.tickDelta, 0, 0, 136, 76);
        bufferSource.endBatch();
    }
}
