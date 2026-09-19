package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.PoleBlock;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RVPIDSPole extends PoleBlock {
    public RVPIDSPole(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(7.5, 0, 8.5, 8.5, 16, 9.5, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState superState = super.getStateForPlacement(ctx);
        if(superState == null) return null;

        BlockState blockBelow = ctx.getLevel().getBlockState(ctx.getClickedPos().below());
        return superState.setValue(FACING, IBlock.getStatePropertySafe(blockBelow, FACING));
    }

    @Override
    public boolean blockIsAllowed(Block block) {
        return block instanceof JCMPIDSBlock || block instanceof RVPIDSPole;
    }
}
