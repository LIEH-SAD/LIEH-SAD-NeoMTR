package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class OperatorButtonBlockEntity extends JCMBlockEntity {
    public OperatorButtonBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.OPERATOR_BUTTON.get(), blockPos, blockState);
    }
}
