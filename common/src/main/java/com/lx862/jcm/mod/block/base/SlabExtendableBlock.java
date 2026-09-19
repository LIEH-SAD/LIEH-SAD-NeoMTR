package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.data.BlockProperties;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;

public abstract class SlabExtendableBlock extends DirectionalBlock {
    public static final BooleanProperty IS_SLAB = BlockProperties.IS_SLAB;

    public SlabExtendableBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(IS_SLAB, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if(state == null) return null;

        return state.setValue(IS_SLAB, shouldExtendForSlab(ctx.getLevel(), ctx.getClickedPos()));
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        return super.updateShape(state, world, ticks, pos, direction, posFrom, newState, random).setValue(IS_SLAB, shouldExtendForSlab(world, pos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_SLAB);
    }

    public static boolean shouldExtendForSlab(LevelReader world, BlockPos pos) {
        BlockState blockTop = world.getBlockState(pos.above());

        if (blockTop.getBlock() instanceof SlabBlock) {
            return IBlock.getStatePropertySafe(blockTop, SlabBlock.TYPE) == SlabType.TOP;
        } else {
            return false;
        }
    }
}
