package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.block.behavior.HorizontalDoubleBlockBehavior;
import com.lx862.jcm.mod.block.behavior.WallAttachedBlockBehavior;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
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

public abstract class HorizontalWallAttached2Block extends DirectionalBlock implements HorizontalDoubleBlockBehavior {
    public HorizontalWallAttached2Block(Properties settings) {
        super(settings);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState superState = super.getStateForPlacement(ctx);
        if(superState == null) return null;

        boolean firstBlockAttachable = WallAttachedBlockBehavior.canBePlaced(ctx.getClickedPos(), ctx.getLevel(), IBlock.getStatePropertySafe(superState, FACING));
        boolean secondBlockAttachable = WallAttachedBlockBehavior.canBePlaced(ctx.getClickedPos().relative(IBlock.getStatePropertySafe(superState, FACING).getClockWise()), ctx.getLevel(), IBlock.getStatePropertySafe(superState, FACING));

        return firstBlockAttachable && secondBlockAttachable && HorizontalDoubleBlockBehavior.canBePlaced(ctx) ? super.getStateForPlacement(ctx) : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        HorizontalDoubleBlockBehavior.onPlaced(world, state, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        boolean isLeft = IBlock.getStatePropertySafe(state, IS_LEFT);

        boolean blockAttachable = WallAttachedBlockBehavior.canBePlaced(pos, world, IBlock.getStatePropertySafe(state, FACING));

        if(!HorizontalDoubleBlockBehavior.blockIsValid(pos, state, world, isLeft) || !blockAttachable) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, world, ticks, pos, direction, posFrom, newState, random);
    }

    @Override
    public BlockPos[] getAllPos(BlockState state, LevelReader world, BlockPos pos) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        BlockPos otherPos = pos.relative(facing);
        return new BlockPos[]{ pos, otherPos };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        HorizontalDoubleBlockBehavior.addProperties(builder);
    }
}
