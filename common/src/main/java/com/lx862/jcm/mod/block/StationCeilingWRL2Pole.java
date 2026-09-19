package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.PoleBlock;
import com.lx862.jcm.mod.data.BlockProperties;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StationCeilingWRL2Pole extends PoleBlock {
    public static final BooleanProperty PART = BlockProperties.HORIZONTAL_IS_LEFT;

    public StationCeilingWRL2Pole(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        if(IBlock.getStatePropertySafe(state, PART)) {
            return IBlock.getVoxelShapeByDirection(5.5, 0, 7.5, 6.5, 16, 8.5, IBlock.getStatePropertySafe(state, FACING));
        } else {
            return IBlock.getVoxelShapeByDirection(10.5, 0, 7.5, 11.5, 16, 8.5, IBlock.getStatePropertySafe(state, FACING));
        }
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState superState = super.getStateForPlacement(ctx);
        if(superState == null) return null;

        BlockState blockBelow = ctx.getLevel().getBlockState(ctx.getClickedPos().below());
        return superState
                .setValue(PART, IBlock.getStatePropertySafe(blockBelow, PART))
                .setValue(FACING, IBlock.getStatePropertySafe(blockBelow, FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    public boolean blockIsAllowed(Block block) {
        return block instanceof StationCeilingWRL2Block || block instanceof StationCeilingWRL2Pole;
    }
}
