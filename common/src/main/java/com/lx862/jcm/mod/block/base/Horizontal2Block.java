package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.block.behavior.HorizontalDoubleBlockBehavior;
import mtr.block.IBlock;
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
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class Horizontal2Block extends DirectionalBlock implements HorizontalDoubleBlockBehavior {
    public Horizontal2Block(Properties settings) {
        super(settings);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return HorizontalDoubleBlockBehavior.canBePlaced(ctx) ? super.getStateForPlacement(ctx) : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        HorizontalDoubleBlockBehavior.onPlaced(world, state, pos);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos breakPos, BlockState breakState, Player player) {
        breakWithoutDropIfCreative(world, breakPos, breakState, player, this, HorizontalDoubleBlockBehavior::getLootDropPos);
        return super.playerWillDestroy(world, breakPos, breakState, player);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        boolean isLeft = IBlock.getStatePropertySafe(state, IS_LEFT);
        if(!HorizontalDoubleBlockBehavior.blockIsValid(pos, state, world, isLeft)) return Blocks.AIR.defaultBlockState();

        return super.updateShape(state, world, ticks, pos, direction, posFrom, newState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        HorizontalDoubleBlockBehavior.addProperties(builder);
    }

    @Override
    public BlockPos[] getAllPos(BlockState state, LevelReader world, BlockPos pos) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        boolean isLeft = IBlock.getStatePropertySafe(state, IS_LEFT);

        BlockPos secondaryPos = isLeft ? pos.relative(facing.getClockWise()) : pos.relative(facing.getCounterClockWise());
        return new BlockPos[]{ pos, secondaryPos };
    }
}
