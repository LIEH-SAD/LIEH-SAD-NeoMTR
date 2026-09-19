package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.WallAttachedBlock;
import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KCREmergencyStopSign extends WallAttachedBlock {
    public static final BooleanProperty POINT_TO_LEFT = BlockProperties.POINT_TO_LEFT;
    public KCREmergencyStopSign(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(0, 0, 6.5, 26, 7, 9.5, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx) == null ? null : super.getStateForPlacement(ctx).setValue(FACING, Direction.from2DDataValue(ctx.getHorizontalDirection().get2DDataValue()));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            world.setBlockAndUpdate(pos, state.cycle(POINT_TO_LEFT));
            player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "kcr_emg_stop_sign.success"));
        });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POINT_TO_LEFT);
    }

    @Override
    public Direction getWallDirection(Direction defaultDirection) {
        return defaultDirection.getCounterClockWise();
    }
}
