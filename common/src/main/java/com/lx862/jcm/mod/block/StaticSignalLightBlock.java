package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.StaticSignalLightBlockEntity;
import mtr.block.BlockSignalLightBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class StaticSignalLightBlock extends BlockSignalLightBase {
    public StaticSignalLightBlock(Properties settings, int shapeX, int shapeHeight) {
        super(settings, shapeX, shapeHeight);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public abstract StaticSignalLightBlockEntity.SignalType getSignalType();

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new StaticSignalLightBlockEntity(getSignalType(), blockPos, blockState);
    }
}
