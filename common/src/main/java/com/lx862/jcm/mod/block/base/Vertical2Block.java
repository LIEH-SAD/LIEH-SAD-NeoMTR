package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.block.behavior.VerticalDoubleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import mtr.block.IBlock;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public abstract class Vertical2Block extends DirectionalBlock implements VerticalDoubleBlock {

    public Vertical2Block(Properties settings) {
        super(settings);
    }
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        VerticalDoubleBlock.placeBlock(world, pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState superState = super.getStateForPlacement(ctx);
        if(superState == null) return null;
        return VerticalDoubleBlock.canBePlaced(ctx.getLevel(), ctx.getClickedPos(), ctx) ? superState.setValue(HALF, DoubleBlockHalf.LOWER) : null;
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        if(VerticalDoubleBlock.blockNotValid(this, world, pos, state)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, world, ticks, pos, direction, posFrom, newState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos breakPos, BlockState breakState, Player player) {
        breakWithoutDropIfCreative(world, breakPos, breakState, player, this, VerticalDoubleBlock::getLootDropPos);
        return super.playerWillDestroy(world, breakPos, breakState, player);
    }

    @Override
    public BlockPos[] getAllPos(BlockState state, LevelReader world, BlockPos pos) {
        switch(IBlock.getStatePropertySafe(state, HALF)) {
            case LOWER:
                return new BlockPos[]{
                        pos,
                        pos.above()};
            case UPPER:
                return new BlockPos[]{
                        pos.below(),
                        pos};
            default:
                return super.getAllPos(state, world, pos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }
}
