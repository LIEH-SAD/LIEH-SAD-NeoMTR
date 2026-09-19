package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.Vertical3Block;
import com.lx862.jcm.mod.block.entity.StationNameStandingBlockEntity;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StationNameStandingBlock extends Vertical3Block implements EntityBlockMapper {
    public StationNameStandingBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        switch (IBlock.getStatePropertySafe(state, THIRD)) {
            case LOWER:
                VoxelShape vx1 = IBlock.getVoxelShapeByDirection(1, 0, 0, 2, 16, 1, IBlock.getStatePropertySafe(state, FACING));
                VoxelShape vx2 = IBlock.getVoxelShapeByDirection(14, 0, 0, 15, 16, 1, IBlock.getStatePropertySafe(state, FACING));
                return Shapes.or(vx1, vx2);
            case MIDDLE:
                return IBlock.getVoxelShapeByDirection(1, 0, 0, 15, 16, 1, IBlock.getStatePropertySafe(state, FACING));
            case UPPER:
                return IBlock.getVoxelShapeByDirection(1, 0, 0, 15, 6, 1, IBlock.getStatePropertySafe(state, FACING));
            default:
                return Shapes.empty();
        }
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new StationNameStandingBlockEntity(blockPos, blockState);
    }
}
