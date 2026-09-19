package com.lx862.jcm.mod.block.base;/*package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.block.behavior.WaterloggableBehavior;
import mtr.mapping.holder.*;
import mtr.mapping.tool.HolderBase;

import java.util.List;
*/
/**
 * A waterlogged block
 * Cannot find ways to migrate non-waterlogged block yet, so this is not in use.
 * (WATERLOGGED is a BooleanProperty, by default Minecraft will use the first value as it's default value, and somehow "true" comes first)
 */

/*
public abstract class WaterloggableBlock extends JCMBlock implements WaterloggableBehavior {
    public WaterloggableBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().with(new Property<>(WATERLOGGED), false));
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        super.addBlockProperties(properties);
        properties.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState2(BlockState state) {
        return getFluidState(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return getWaterloggedState(super.getStateForPlacement(ctx), ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        updateWaterState(state, world, pos);
        return super.updateShape(state, world, ticks, pos, direction, posFrom, newState, random);
    }
}
*/