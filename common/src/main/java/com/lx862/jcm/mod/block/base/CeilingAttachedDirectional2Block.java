package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.block.behavior.HorizontalDoubleBlockBehavior;
import com.lx862.jcm.mod.block.behavior.VerticallyAttachedBlock;
import com.lx862.jcm.mod.data.BlockProperties;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class CeilingAttachedDirectional2Block extends CeilingAttachedDirectionalBlock implements HorizontalDoubleBlockBehavior {
    public static final int width = 2;
    public static final BooleanProperty IS_LEFT = BlockProperties.HORIZONTAL_IS_LEFT;
    private final boolean canAttachTop;
    private final boolean canAttachBottom;

    public CeilingAttachedDirectional2Block(Properties settings, boolean canAttachTop, boolean canAttachBottom, boolean enforceLogicalPattern) {
        super(settings, enforceLogicalPattern);
        this.canAttachTop = canAttachTop;
        this.canAttachBottom = canAttachBottom;
    }

    public boolean canPlace(BlockState state, BlockPos pos, BlockPlaceContext ctx) {
        boolean canPlaceHorizontally = HorizontalDoubleBlockBehavior.canBePlaced(ctx);
        boolean canPlaceVertically = VerticallyAttachedBlock.canPlace(canAttachTop, canAttachBottom, ctx) && VerticallyAttachedBlock.canPlace(canAttachTop, canAttachBottom, pos.relative(IBlock.getStatePropertySafe(state, FACING).getClockWise()), ctx);
        return this.enforceLogicalPattern ? canPlaceHorizontally && canPlaceVertically : canPlaceHorizontally;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return canPlace(super.getStateForPlacement(ctx), ctx.getClickedPos(), ctx) ? super.getStateForPlacement(ctx) : null;
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        HorizontalDoubleBlockBehavior.placeBlock(world, pos, state, IS_LEFT, state.getValue(FACING).getClockWise(), width);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos breakPos, BlockState breakState, Player player) {
        breakWithoutDropIfCreative(world, breakPos, breakState, player, this, HorizontalDoubleBlockBehavior::getLootDropPos);
        return super.playerWillDestroy(world, breakPos, breakState, player);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        boolean isLeft = IBlock.getStatePropertySafe(state, IS_LEFT);

        if(!HorizontalDoubleBlockBehavior.blockIsValid(pos, state, world, isLeft)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, world, ticks, pos, direction, posFrom, newState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_LEFT);
    }
}