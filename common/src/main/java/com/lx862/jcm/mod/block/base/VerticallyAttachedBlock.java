package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.data.BlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;

public abstract class VerticallyAttachedBlock extends JCMBlock {
    public static final BooleanProperty TOP = BlockProperties.TOP;

    public VerticallyAttachedBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(TOP, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState superState = super.getStateForPlacement(ctx);
        BlockState blockAbove = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
        if(superState == null) return null;
        if(!com.lx862.jcm.mod.block.behavior.VerticallyAttachedBlock.canPlace(true, true, ctx)) return null;

        return superState.setValue(TOP, !blockAbove.isAir());
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        BlockState attachedBlock = state.getValue(TOP) ? world.getBlockState(pos.above()) : world.getBlockState(pos.below());

        if (shouldBreakOnBlockUpdate() && attachedBlock.isAir()) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, world, ticks, pos, direction, posFrom, newState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TOP);
    }

    protected boolean shouldBreakOnBlockUpdate() {
        return true;
    }
}
