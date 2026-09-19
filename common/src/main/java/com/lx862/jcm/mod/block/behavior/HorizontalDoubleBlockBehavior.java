package com.lx862.jcm.mod.block.behavior;

import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.util.BlockUtil;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import static com.lx862.jcm.mod.block.base.DirectionalBlock.FACING;

public interface HorizontalDoubleBlockBehavior {
    BooleanProperty IS_LEFT = BlockProperties.HORIZONTAL_IS_LEFT;
    static boolean canBePlaced(BlockPlaceContext ctx) {
        return BlockUtil.isReplacable(ctx.getLevel(), ctx.getClickedPos(), ctx.getHorizontalDirection().getClockWise(), ctx, 2);
    }

    static void placeBlock(Level world, BlockPos pos, BlockState state, BooleanProperty isLeft, Direction directionToPlace, int length) {
        world.setBlockAndUpdate(pos.relative(directionToPlace), state.setValue(isLeft, false));
    }

    static boolean blockIsValid(BlockPos pos, BlockState state, LevelReader world, boolean thisBlockIsLeftPart) {
        Direction dir = IBlock.getStatePropertySafe(state, FACING);
        boolean checkLeft = !thisBlockIsLeftPart;

        BlockPos neighbourPos = checkLeft ? pos.relative(dir.getCounterClockWise()) : pos.relative(dir.getClockWise());
        BlockState neighbourState = world.getBlockState(neighbourPos);
        return neighbourState.is(state.getBlock());
    }

    static void onPlaced(Level world, BlockState state, BlockPos placedPos) {
        world.setBlockAndUpdate(placedPos.relative(IBlock.getStatePropertySafe(state, FACING).getClockWise()), state.setValue(IS_LEFT, false));
    }

    static BlockPos getLootDropPos(BlockState state, BlockPos pos) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        boolean isLeft = IBlock.getStatePropertySafe(state, IS_LEFT);
        return isLeft ? pos : pos.relative(facing.getCounterClockWise());
    }

    static void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_LEFT);
    }
}
