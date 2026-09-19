package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.PIDS1ABlockEntity;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PIDS1ABlock extends JCMPIDSBlock {
    public PIDS1ABlock(Properties properties) {
        super(properties.lightLevel((state) -> 5).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape1 = IBlock.getVoxelShapeByDirection(6, 0, 0, 10, 11, 16, IBlock.getStatePropertySafe(state, FACING));
        VoxelShape shape2 = IBlock.getVoxelShapeByDirection(7.5, 11, 12.5, 8.5, 16, 13.5, IBlock.getStatePropertySafe(state, FACING));
        return Shapes.or(shape1, shape2);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PIDS1ABlockEntity(blockPos, blockState);
    }
}
