package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.block.entity.ButterflyLightBlockEntity;
import com.lx862.jcm.mod.render.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.MTRClient;
import mtr.client.ClientData;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.data.ScheduleEntry;
import mtr.render.MoreRenderLayers;
import mtr.util.UtilitiesClient;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ButterflyLightRenderer extends JCMBlockEntityRenderer<ButterflyLightBlockEntity, ButterflyLightRenderer.ButterflyLightRenderState> {
    private static final Identifier BUTTERFLY_LIGHT_TEXTURE = Constants.id("textures/block/butterfly_light_dotmatrix.png");

    public ButterflyLightRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public ButterflyLightRenderState createRenderState() {
        return new ButterflyLightRenderState();
    }

    @Override
    public void extractRenderState(ButterflyLightBlockEntity blockEntity, ButterflyLightRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        if(!state.shouldRender) return;

        state.blinking = false;
        final int startBlinkingSeconds = blockEntity.getStartBlinkingSeconds();

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

        final long secondsLeft = dwellLeft / 1000;

        state.blinking = secondsLeft <= startBlinkingSeconds && MTRClient.getGameTick() % 40 > 20;
    }

    @Override
    public void renderCurated(ButterflyLightRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if(!state.blinking) return;

        final Direction facing = state.facing;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(1/16F, 1/16F, 1/16F);
        rotateToBlockDirection(poseStack, facing);
        UtilitiesClient.rotateZDegrees(poseStack, 180);
        poseStack.translate(-8, 3, -2.05);

        submitNodeCollector.submitCustomGeometry(poseStack, MoreRenderLayers.getLight(BUTTERFLY_LIGHT_TEXTURE, true), (pose, vertexConsumer) -> {
            RenderHelper.drawTexture(pose, vertexConsumer, 2, 0, 4, 12, 5, facing, ARGB_WHITE, MAX_RENDER_LIGHT);
        });
        poseStack.popPose();
    }

    public static class ButterflyLightRenderState extends JCMBlockEntityRenderer.JCMRenderState {
        boolean blinking;
    }
}
