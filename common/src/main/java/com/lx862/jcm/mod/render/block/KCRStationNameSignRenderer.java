package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.block.entity.KCRStationNameSignBlockEntity;
import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.render.IDrawingJoban;
import com.lx862.jcm.mod.render.RenderHelper;
import com.lx862.jcm.mod.util.TextUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.data.IGui;
import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.util.UtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class KCRStationNameSignRenderer extends JCMBlockEntityRenderer<KCRStationNameSignBlockEntity, KCRStationNameSignRenderer.KCRStationNameSignRenderState> {
    private static final Identifier KCR_FONT = Identifier.parse("jsblock:kcr_sign");

    public KCRStationNameSignRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public KCRStationNameSignRenderState createRenderState() {
        return new KCRStationNameSignRenderState();
    }

    @Override
    public void extractRenderState(KCRStationNameSignBlockEntity blockEntity, KCRStationNameSignRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        if(!state.shouldRender) return;

        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, blockEntity.getBlockPos());
        state.stationName = station == null ? TextUtil.translatable("gui.mtr.untitled").getString() : station.name;
        state.offsetForExitDirection = IBlock.getStatePropertySafe(blockEntity.getBlockState(), BlockProperties.EXIT_ON_LEFT) ? -0.225 : 0.225;
    }

    @Override
    public void renderCurated(KCRStationNameSignRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        rotateToBlockDirection(poseStack, state.facing);
        UtilitiesClient.rotateZDegrees(poseStack,180);
        UtilitiesClient.rotateYDegrees(poseStack,180);

        final MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();

        // Draw both sides
        for(int i = 0; i < 2; i++) {
            poseStack.pushPose();
            poseStack.translate(state.offsetForExitDirection, 0, 0);
            if(i == 1) UtilitiesClient.rotateYDegrees(poseStack, 180); // Other side
            poseStack.translate(0, -0.05, -0.175);
            poseStack.scale(0.021F, 0.021F, 0.021F);
            IDrawingJoban.drawStringWithFont(poseStack, Minecraft.getInstance().font, immediate, state.stationName, KCR_FONT, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, 0, 60, 32, 1, 0xEEEEEE, false, RenderHelper.MAX_RENDER_LIGHT, null);
            poseStack.popPose();
        }

        immediate.endBatch();
        poseStack.popPose();
    }

    public static class KCRStationNameSignRenderState extends JCMBlockEntityRenderer.JCMRenderState {
        String stationName = "";
        double offsetForExitDirection;
    }
}
