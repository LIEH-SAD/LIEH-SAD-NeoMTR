package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.RVPIDSSIL1BlockEntity;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RVPIDSSIL1Block extends JCMPIDSBlock {

    public RVPIDSSIL1Block(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        VoxelShape vx1 = IBlock.getVoxelShapeByDirection(0, -2, 0, 16, 9, 16, IBlock.getStatePropertySafe(state, FACING));
        VoxelShape vx2 = IBlock.getVoxelShapeByDirection(7.5, 9, 8.5, 8.5, 16, 9.5, IBlock.getStatePropertySafe(state, FACING));
        return Shapes.or(vx1, vx2);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RVPIDSSIL1BlockEntity(blockPos, blockState);
    }
}