package com.lx862.jcm.mod.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface WallAttachedBlockBehavior {
    static boolean canBePlaced(BlockPos pos, LevelReader world, Direction directionTowardsWall) {
        BlockPos wallBlockPos = pos.relative(directionTowardsWall);
        BlockState wallBlock = world.getBlockState(wallBlockPos);

        return wallBlock.isFaceSturdy(world, pos, directionTowardsWall);
    }

    default Direction getWallDirection(Direction defaultDirection) {
        return defaultDirection;
    }
}
