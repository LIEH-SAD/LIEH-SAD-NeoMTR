package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.block.entity.PIDSBlockEntity;
import com.lx862.jcm.mod.data.pids.PIDSManager;
import com.lx862.jcm.mod.data.pids.preset.PIDSPresetBase;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongImmutableList;
import mtr.client.ClientData;
import mtr.data.RailwayData;
import mtr.data.ScheduleEntry;
import mtr.util.UtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public abstract class PIDSRenderer<T extends PIDSBlockEntity, S extends PIDSRenderer.PIDSRenderState> extends JCMBlockEntityRenderer<T, S> {
    public PIDSRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void extractRenderState(T blockEntity, S state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        if(!state.shouldRender) return;

        state.preset = null;

        PIDSPresetBase pidsPreset = getPreset(blockEntity);
        if(pidsPreset == null) {
            state.shouldRender = false;
            return;
        }

        boolean[] rowHidden = new boolean[blockEntity.getRowAmount()];
        boolean[] beRowHidden = blockEntity.getRowHidden();
        for(int i = 0; i < rowHidden.length; i++) {
            rowHidden[i] = pidsPreset.isRowHidden(i) || beRowHidden[i];
        }

        LongImmutableList platforms;
        if(!blockEntity.getPlatformIds().isEmpty()) {
            platforms = new LongImmutableList(blockEntity.getPlatformIds());
        } else {
            final long platformId = RailwayData.getClosePlatformId(ClientData.PLATFORMS, ClientData.DATA_CACHE, blockEntity.getBlockPos(), 5, 4, 4);
            if (platformId != 0) {
                platforms = new LongImmutableList(new long[]{platformId});
            } else {
                platforms = new LongImmutableList(new long[]{});
            }
        }

        List<ScheduleEntry> arrivals = new ArrayList<>();
        for(long platformId : platforms) {
            arrivals.addAll(ClientData.SCHEDULES_FOR_PLATFORM.getOrDefault(platformId, new HashSet<>()));
        }
        Collections.sort(arrivals);

        state.blockEntity = blockEntity;
        state.world = blockEntity.getLevel();
        state.preset = pidsPreset;
        state.arrivals = arrivals;
        state.rowHidden = rowHidden;
        state.tickDelta = partialTicks;
    }

    @Override
    public void renderCurated(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if(state.blockEntity == null || state.preset == null || state.arrivals == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        UtilitiesClient.rotateYDegrees(poseStack, 90 - state.facing.toYRot());
        UtilitiesClient.rotateZDegrees(poseStack, 180);
        renderPIDS(state, poseStack, submitNodeCollector);
        poseStack.popPose();
    }

    public abstract void renderPIDS(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector);

    /**
     * PIDS presets still draw through the immediate buffer source, so they need one to draw into.
     */
    protected static MultiBufferSource.BufferSource getImmediateBufferSource() {
        return Minecraft.getInstance().renderBuffers().bufferSource();
    }

    private PIDSPresetBase getPreset(PIDSBlockEntity blockEntity) {
        return PIDSManager.getPreset(blockEntity.getPresetId(), PIDSManager.getPreset(blockEntity.getDefaultPresetId()));
    }

    public static class PIDSRenderState extends JCMBlockEntityRenderer.JCMRenderState {
        public PIDSBlockEntity blockEntity;
        public Level world;
        public PIDSPresetBase preset;
        public List<ScheduleEntry> arrivals;
        public boolean[] rowHidden;
        public float tickDelta;
    }
}
