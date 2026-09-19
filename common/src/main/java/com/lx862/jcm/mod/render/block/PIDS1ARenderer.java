package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.block.entity.PIDS1ABlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

public class PIDS1ARenderer extends PIDSRenderer<PIDS1ABlockEntity, PIDSRenderer.PIDSRenderState> {
    public PIDS1ARenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public PIDSRenderer.PIDSRenderState createRenderState() {
        return new PIDSRenderer.PIDSRenderState();
    }

    @Override
    public void renderPIDS(PIDSRenderer.PIDSRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        poseStack.translate(-0.47, -0.155, -0.130);
        poseStack.scale(1/96F, 1/96F, 1/96F);

        final MultiBufferSource.BufferSource bufferSource = getImmediateBufferSource();
        state.preset.render(state.blockEntity, poseStack, bufferSource, state.world, state.blockEntity.getBlockPos(), state.facing, state.arrivals, state.rowHidden, state.tickDelta, 0, 0, 186, 60);
        bufferSource.endBatch();
    }
}
