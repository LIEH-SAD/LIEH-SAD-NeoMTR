package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.block.entity.DepartureTimerBlockEntity;
import com.lx862.jcm.mod.render.text.TextInfo;
import com.lx862.jcm.mod.render.text.TextRenderingManager;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.client.ClientData;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.data.ScheduleEntry;
import mtr.util.UtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DepartureTimerRenderer extends JCMBlockEntityRenderer<DepartureTimerBlockEntity, DepartureTimerRenderer.DepartureTimerRenderState> {
    public DepartureTimerRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public DepartureTimerRenderState createRenderState() {
        return new DepartureTimerRenderState();
    }

    @Override
    public void extractRenderState(DepartureTimerBlockEntity blockEntity, DepartureTimerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        if(!state.shouldRender) return;

        state.shouldDraw = false;

        final long platformId = RailwayData.getClosePlatformId(ClientData.PLATFORMS, ClientData.DATA_CACHE, blockEntity.getBlockPos(), 5, 3, 3);
        if (platformId == 0) {
            return;
        }

        final Set<ScheduleEntry> schedules = ClientData.SCHEDULES_FOR_PLATFORM.get(platformId);
        if (schedules == null || schedules.isEmpty()) {
            return;
        }
        final List<ScheduleEntry> scheduleList = new ArrayList<>(schedules);
        Collections.sort(scheduleList);
        final ScheduleEntry firstArrival = scheduleList.getFirst();

        final boolean arrived = firstArrival.arrivalMillis <= System.currentTimeMillis();
        if (!arrived) return;

        final int remainingSecond = (int) (firstArrival.arrivalMillis - System.currentTimeMillis()) / 1000;
        final Platform platform = ClientData.DATA_CACHE.platformIdMap.get(platformId);
        final long dwellLeft = (platform == null ? 0 : Math.abs((platform.getDwellTime() / 2) - Math.abs(remainingSecond))) * 1000L;

        final long seconds = dwellLeft / 1000;
        final long mins = seconds / 60;

        state.shouldDraw = true;
        state.mins = mins;
        state.seconds = seconds;
    }

    @Override
    public void renderCurated(DepartureTimerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if(!state.shouldDraw) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(0.018F, 0.018F, 0.018F);
        rotateToBlockDirection(poseStack, state.facing);
        UtilitiesClient.rotateZDegrees(poseStack, 180);
        poseStack.translate(-12.5, -2, -4.1);

        final MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        TextRenderingManager.draw(poseStack, immediate, new TextInfo(String.format("%d:%02d", state.mins % 10, state.seconds % 60)).withColor(0xFFEE2233).withFont("jsblock:deptimer"), 0, 0);
        immediate.endBatch();
        poseStack.popPose();
    }

    public static class DepartureTimerRenderState extends JCMBlockEntityRenderer.JCMRenderState {
        boolean shouldDraw;
        long mins;
        long seconds;
    }
}
