package com.lx862.jcm.mod.block;

import mtr.block.BlockTicketBarrier;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThalesTicketBarrier extends BlockTicketBarrier {
    public ThalesTicketBarrier(Properties properties, boolean isEntrance) {
        super(properties, isEntrance);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(12, 0, 0, 16, 16, 16, facing);
    }
}
