package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.block.entity.StationNameStandingBlockEntity;
import com.lx862.jcm.mod.data.BlockProperties;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.render.RenderStationNameBase;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class StationNameStandingRenderer extends RenderStationNameBase<StationNameStandingBlockEntity, StationNameStandingRenderer.StandingRenderState> {

    private static final float WIDTH = 0.6875F;
    private static final float HEIGHT = 1;
    private static final float OFFSET_Y = 0.125F;

    public StationNameStandingRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public StandingRenderState createRenderState() {
        return new StandingRenderState();
    }

    @Override
    public void extractRenderState(StationNameStandingBlockEntity blockEntity, StandingRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.isMiddle = IBlock.getStatePropertySafe(blockEntity.getBlockState(), BlockProperties.VERTICAL_PART_3) == IBlock.EnumThird.MIDDLE;
    }

    @Override
    protected void drawStationName(StandingRenderState state, BlockPos pos, Direction facing, StoredMatrixTransformations storedMatrixTransformations, String stationName, int stationColor, int color, int light) {
        if (!state.isMiddle) return;

        RenderTrains.scheduleRender(ClientData.DATA_CACHE.getTallStationName(color, stationName, stationColor, WIDTH / HEIGHT).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (poseStack, vertexConsumer) -> {
            storedMatrixTransformations.transform(poseStack);
            IDrawing.drawTexture(poseStack.last(), vertexConsumer, -WIDTH / 2, -HEIGHT / 2 - OFFSET_Y, WIDTH, HEIGHT, 0, 0, 1, 1, facing, ARGB_WHITE, light);
            poseStack.popPose();
        });
    }

    public static class StandingRenderState extends RenderStationNameBase.StationNameRenderState {
        boolean isMiddle;
    }
}
