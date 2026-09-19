package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.block.behavior.WallAttachedBlockBehavior;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class WallAttachedBlock extends DirectionalBlock implements WallAttachedBlockBehavior {
    public WallAttachedBlock(Properties settings) {
        super(settings);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState superState = super.getStateForPlacement(ctx);
        if (superState == null) return null;

        Direction facing = IBlock.getStatePropertySafe(superState, FACING);
        boolean isAttached = WallAttachedBlockBehavior.canBePlaced(ctx.getClickedPos(), ctx.getLevel(), Direction.from2DDataValue(ctx.getClickedFace().get2DDataValue()).getOpposite());
        if(!isAttached) return null;

        return superState.setValue(FACING, Direction.from2DDataValue(ctx.getClickedFace().get2DDataValue()).getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        if (!WallAttachedBlockBehavior.canBePlaced(pos, world, getWallDirection(facing))) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, world, ticks, pos, direction, posFrom, newState, random);
    }
}
