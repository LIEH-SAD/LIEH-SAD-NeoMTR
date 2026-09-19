package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SignalBlockInvertedEntityRedBelow extends BlockEntityMapper {
    public SignalBlockInvertedEntityRedBelow(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.SIGNAL_LIGHT_INVERTED_RED_BELOW.get(), blockPos, blockState);
    }
}
