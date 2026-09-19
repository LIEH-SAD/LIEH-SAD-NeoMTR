package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.RVPIDSBlockEntity;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RVPIDSBlock extends JCMPIDSBlock {

    public RVPIDSBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        VoxelShape vx1 = IBlock.getVoxelShapeByDirection(6, -3, 0, 10, 11, 12, IBlock.getStatePropertySafe(state, FACING));
        VoxelShape vx2 = IBlock.getVoxelShapeByDirection(7.5, 0, 8.5, 8.5, 16, 9.5, IBlock.getStatePropertySafe(state, FACING));
        return Shapes.or(vx1, vx2);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RVPIDSBlockEntity(blockPos, blockState);
    }
}