package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.SignalBlockInvertedEntityRedBelow;
import mtr.block.BlockSignalLightBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InvertedSignalBlockRedBelow extends BlockSignalLightBase {
    public InvertedSignalBlockRedBelow(Properties settings) {
        super(settings, 2, 14);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SignalBlockInvertedEntityRedBelow(blockPos, blockState);
    }
}
