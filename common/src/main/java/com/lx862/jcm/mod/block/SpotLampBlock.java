package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.VerticallyAttachedBlock;
import com.lx862.jcm.mod.data.BlockProperties;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpotLampBlock extends VerticallyAttachedBlock {
    public static final BooleanProperty LIT = BlockProperties.POWERED;

    public SpotLampBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        if (IBlock.getStatePropertySafe(state, TOP)) {
            return IBlock.getVoxelShapeByDirection(4, 15.75, 4, 12, 16, 12, Direction.NORTH);
        } else {
            return IBlock.getVoxelShapeByDirection(4, 0, 4, 12, 0.25, 12, Direction.NORTH);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            BlockState newState = state.cycle(LIT);
            world.setBlockAndUpdate(pos, newState);
        });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }
}
