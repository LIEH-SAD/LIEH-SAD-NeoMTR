package com.lx862.jcm.mod.block.entity;

import mtr.mappings.BlockEntityClientSerializableMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class JCMBlockEntity extends BlockEntityClientSerializableMapper {
    public JCMBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
    }
}
