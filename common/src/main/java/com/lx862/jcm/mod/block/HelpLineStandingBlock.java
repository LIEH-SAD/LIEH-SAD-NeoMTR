package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.Vertical3Block;
import com.lx862.jcm.mod.block.behavior.PowerableBlockBehavior;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HelpLineStandingBlock extends Vertical3Block implements PowerableBlockBehavior {
    public HelpLineStandingBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        switch (IBlock.getStatePropertySafe(state, THIRD)) {
            case LOWER:
            case UPPER:
                return IBlock.getVoxelShapeByDirection(4, 0, 7.5, 12, 16, 8.5, IBlock.getStatePropertySafe(state, FACING));
            case MIDDLE:
                VoxelShape vx1 = IBlock.getVoxelShapeByDirection(4, 0, 7.5, 12, 16, 8.5, IBlock.getStatePropertySafe(state, FACING));
                VoxelShape vx2 = IBlock.getVoxelShapeByDirection(5.5, 0, 8.5, 10.5, 16, 10.5, IBlock.getStatePropertySafe(state, FACING));
                return Shapes.or(vx1, vx2);
            default:
                return Shapes.empty();
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        for(BlockPos bPos : getAllPos(state, world, pos)) {
            BlockState blockState = world.getBlockState(bPos);
            world.setBlockAndUpdate(bPos, blockState.setValue(UNPOWERED, false));
            updateAllRedstone(world, bPos, this, blockState);
            world.scheduleTick(bPos, this, 20);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        world.setBlockAndUpdate(pos, state.setValue(UNPOWERED, true));
        updateAllRedstone(world, pos, this, state);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return IBlock.getStatePropertySafe(state, UNPOWERED) ? 0 : 15;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return IBlock.getStatePropertySafe(state, UNPOWERED) ? 0 : 15;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNPOWERED);
    }
}