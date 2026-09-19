package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.block.entity.FareSaverBlockEntity;
import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.render.RenderHelper;
import com.lx862.jcm.mod.render.text.TextAlignment;
import com.lx862.jcm.mod.render.text.TextInfo;
import com.lx862.jcm.mod.render.text.TextRenderingManager;
import com.lx862.jcm.mod.util.TextUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.IBlock;
import mtr.util.UtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FareSaverRenderer extends JCMBlockEntityRenderer<FareSaverBlockEntity, FareSaverRenderer.FareSaverRenderState> {
    public static final int TEXT_TILT_ANGLE = -10;

    public FareSaverRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public FareSaverRenderState createRenderState() {
        return new FareSaverRenderState();
    }

    @Override
    public void extractRenderState(FareSaverBlockEntity blockEntity, FareSaverRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        if(!state.shouldRender) return;

        state.isUpper = IBlock.getStatePropertySafe(blockEntity.getBlockState(), BlockProperties.VERTICAL_PART_3) == IBlock.EnumThird.UPPER;
        state.discountText = TextUtil.literal(blockEntity.getPrefix() + blockEntity.getDiscount());
    }

    @Override
    public void renderCurated(FareSaverRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if(!state.isUpper || state.discountText == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(0.011F, 0.011F, 0.011F);
        rotateToBlockDirection(poseStack, state.facing);
        UtilitiesClient.rotateZDegrees(poseStack,180);
        UtilitiesClient.rotateZDegrees(poseStack,TEXT_TILT_ANGLE);
        poseStack.translate(5.8, 14, -9.15);

        poseStack.pushPose();
        TextInfo textInfo = new TextInfo(state.discountText)
                .withFont("mtr:mtr")
                .withColor(ARGB_WHITE)
                .withTextAlignment(TextAlignment.CENTER);
        RenderHelper.scaleToFit(poseStack, TextRenderingManager.getTextWidth(textInfo), 12, true, 12);
        final MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        TextRenderingManager.draw(poseStack, immediate, textInfo, 0, 0);
        immediate.endBatch();
        poseStack.popPose();
        poseStack.popPose();
    }

    public static class FareSaverRenderState extends JCMBlockEntityRenderer.JCMRenderState {
        boolean isUpper;
        MutableComponent discountText;
    }
}
