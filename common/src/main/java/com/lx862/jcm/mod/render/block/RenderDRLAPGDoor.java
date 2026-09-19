package com.lx862.jcm.mod.render.block;

import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.block.entity.APGDoorDRLBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.MTR;
import mtr.MTRClient;
import mtr.block.BlockAPGGlass;
import mtr.block.BlockAPGGlassEnd;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.ModelDataWrapper;
import mtr.mappings.ModelMapper;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import mtr.util.UtilitiesClient;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Copied from MTR, PSD/APG is a pain
 */
public class RenderDRLAPGDoor<T extends APGDoorDRLBlockEntity> extends BlockEntityRendererMapper<T, RenderDRLAPGDoor.DRLAPGDoorRenderState> implements IGui, IBlock {
    private final int type;
    private static final ModelSingleCube MODEL_APG_TOP = new ModelSingleCube(34, 9, 0, 15, 1, 16, 1, 1);
    private static final ModelAPGDoorBottom MODEL_APG_BOTTOM = new ModelAPGDoorBottom();
    private static final ModelAPGDoorLight MODEL_APG_LIGHT = new ModelAPGDoorLight();
    private static final ModelSingleCube MODEL_APG_DOOR_LOCKED = new ModelSingleCube(6, 6, 5, 17, 1, 6, 6, 0);

    public RenderDRLAPGDoor(BlockEntityRenderDispatcher dispatcher, int type) {
        super(dispatcher);
        this.type = type;
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public DRLAPGDoorRenderState createRenderState() {
        return new DRLAPGDoorRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, DRLAPGDoorRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.shouldRender = false;
        final Level level = blockEntity.getLevel();
        final BlockPos blockPos = blockEntity.getBlockPos();
        if (level == null) {
            return;
        }

        state.shouldRender = true;
        state.facing = IBlock.getStatePropertySafe(level, blockPos, BlockPSDAPGDoorBase.FACING);
        state.side = IBlock.getStatePropertySafe(level, blockPos, BlockPSDAPGDoorBase.SIDE) == EnumSide.RIGHT;
        state.half = IBlock.getStatePropertySafe(level, blockPos, BlockPSDAPGDoorBase.HALF) == DoubleBlockHalf.UPPER;
        state.end = IBlock.getStatePropertySafe(level, blockPos, BlockPSDAPGDoorBase.END);
        state.unlocked = IBlock.getStatePropertySafe(level, blockPos, BlockPSDAPGDoorBase.UNLOCKED);
        state.open = Math.min(blockEntity.getOpen(MTRClient.getLastFrameDuration()), type >= 3 ? 0.75F : 1);

        final Block nearbyBlock = level.getBlockState(blockPos.relative(state.side ? state.facing.getClockWise() : state.facing.getCounterClockWise())).getBlock();
        state.nearbyBlockIsAPG = nearbyBlock instanceof BlockAPGGlass || nearbyBlock instanceof BlockAPGGlassEnd;
    }

    @Override
    public void submit(DRLAPGDoorRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.shouldRender) return;

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + state.blockPos.getX(), state.blockPos.getY(), 0.5 + state.blockPos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -state.facing.toYRot());
            UtilitiesClient.rotateXDegrees(matricesNew, 180);
        });
        final StoredMatrixTransformations storedMatrixTransformationsLight = storedMatrixTransformations.copy();

        switch (type) {
            case 2:
                if (state.half && state.nearbyBlockIsAPG) {
                    final Identifier lightLocation = MTR.id(String.format("textures/block/apg_door_light_%s.png", state.open > 0 ? "on" : "off"));
                    RenderTrains.scheduleRender(lightLocation, false, state.open > 0 ? RenderTrains.QueuedRenderLayer.LIGHT_TRANSLUCENT : RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                        storedMatrixTransformationsLight.transform(matricesNew);
                        matricesNew.translate(state.side ? -0.515625 : 0.515625, 0, 0);
                        matricesNew.scale(0.5F, 1, 1);
                        MODEL_APG_LIGHT.renderToBuffer(matricesNew, vertexConsumer, state.lightCoords, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                        matricesNew.popPose();
                    });
                }
                break;
        }

        storedMatrixTransformations.add(matricesNew -> matricesNew.translate(state.open * (state.side ? -1 : 1), 0, 0));

        switch (type) {
            case 2:
                final boolean half = state.half;
                final boolean side = state.side;
                RenderTrains.scheduleRender(Constants.id(String.format("textures/entity/drl_apg_door/apg_door_%s_%s.png", half ? "top" : "bottom", side ? "right" : "left")), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                    storedMatrixTransformations.transform(matricesNew);
                    (half ? MODEL_APG_TOP : MODEL_APG_BOTTOM).renderToBuffer(matricesNew, vertexConsumer, state.lightCoords, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                    matricesNew.popPose();
                });
                if (half && !state.unlocked) {
                    RenderTrains.scheduleRender(MTR.id("textures/block/sign/door_not_in_use.png"), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                        storedMatrixTransformations.transform(matricesNew);
                        MODEL_APG_DOOR_LOCKED.renderToBuffer(matricesNew, vertexConsumer, state.lightCoords, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                        matricesNew.popPose();
                    });
                }
                break;
        }
    }

    public static class DRLAPGDoorRenderState extends BlockEntityRenderState {
        Direction facing;
        boolean side;
        boolean half;
        boolean end;
        boolean unlocked;
        float open;
        boolean nearbyBlockIsAPG;
        boolean shouldRender;
    }

    private static class ModelSingleCube extends EntityModel<EntityRenderState> {

        private ModelSingleCube(int textureWidth, int textureHeight, int x, int y, int z, int length, int height, int depth) {
            final ModelDataWrapper modelDataWrapper = new ModelDataWrapper();
            final ModelMapper cube = new ModelMapper(modelDataWrapper);
            cube.texOffs(0, 0).addBox(x - 8, y - 16, z - 8, length, height, depth, 0, false);
            modelDataWrapper.setModelPart(textureWidth, textureHeight);
            cube.setModelPart();
            super(modelDataWrapper.modelPart);
        }
    }

    private static class ModelAPGDoorBottom extends EntityModel<EntityRenderState> {

        private ModelAPGDoorBottom() {
            final int textureWidth = 34;
            final int textureHeight = 27;

            final ModelDataWrapper modelDataWrapper = new ModelDataWrapper();

            final ModelMapper bone = new ModelMapper(modelDataWrapper);
            bone.texOffs(0, 0).addBox(-8, -16, -7, 16, 16, 1, 0, false);
            bone.texOffs(0, 17).addBox(-8, -6, -8, 16, 6, 1, 0, false);

            final ModelMapper cube_r1 = new ModelMapper(modelDataWrapper);
            cube_r1.setPos(0, -6, -8);
            bone.addChild(cube_r1);
            cube_r1.setRotationAngle(-0.7854F, 0, 0);
            cube_r1.texOffs(0, 24).addBox(-8, -2, 0, 16, 2, 1, 0, false);

            modelDataWrapper.setModelPart(textureWidth, textureHeight);
            bone.setModelPart();
            super(modelDataWrapper.modelPart);
        }
    }

    private static class ModelAPGDoorLight extends EntityModel<EntityRenderState> {

        private ModelAPGDoorLight() {
            final ModelDataWrapper modelDataWrapper = new ModelDataWrapper();

            final ModelMapper bone = new ModelMapper(modelDataWrapper);
            bone.texOffs(0, 4).addBox(-0.5F, -2, -7, 1, 1, 3, 0.05F, false);

            final ModelMapper cube_r1 = new ModelMapper(modelDataWrapper);
            cube_r1.setPos(0, -2.05F, -4.95F);
            bone.addChild(cube_r1);
            cube_r1.setRotationAngle(0.3927F, 0, 0);
            cube_r1.texOffs(0, 0).addBox(-0.5F, 0.05F, -3.05F, 1, 1, 3, 0.05F, false);

            modelDataWrapper.setModelPart(8, 8);
            bone.setModelPart();
            super(modelDataWrapper.modelPart);
        }
    }
}