package com.lx862.jcm.mod.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MTRStairsBlock extends StairBlock {
    public MTRStairsBlock(Properties settings) {
        super(Blocks.SMOOTH_STONE.defaultBlockState(), settings);
    }
}