package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.JCMClient;
import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.render.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.util.UtilitiesClient;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class JCMBlockEntityRenderer<T extends BlockEntityMapper, S extends JCMBlockEntityRenderer.JCMRenderState> extends BlockEntityRendererMapper<T, S> implements RenderHelper {
    public JCMBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void extractRenderState(T blockEntity, S state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.shouldRender = true;
        final Level level = blockEntity.getLevel();
        if(level == null || level.getBlockState(blockEntity.getBlockPos()).isAir()) {
            state.shouldRender = false;
            return;
        }

        state.facing = IBlock.getStatePropertySafe(blockEntity.getBlockState(), BlockProperties.FACING);
    }

    @Override
    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if(JCMClient.getConfig().disableRendering || !state.shouldRender) return;

        renderCurated(state, poseStack, submitNodeCollector, camera);
    }

    /**
     * Same as the default block entity render method, but only called in safe condition and when rendering are not disabled
     */
    public abstract void renderCurated(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera);

    public void rotateToBlockDirection(PoseStack poseStack, Direction facing) {
        if(facing != null) {
            UtilitiesClient.rotateYDegrees(poseStack, -facing.toYRot());
        }
    }

    public static class JCMRenderState extends BlockEntityRenderState {
        public Direction facing;
        public boolean shouldRender = true;
    }
}
