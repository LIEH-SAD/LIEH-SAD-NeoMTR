package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DepartureTimerBlockEntity extends BlockEntityMapper {
    public DepartureTimerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.DEPARTURE_TIMER.get(), blockPos, blockState);
    }
}
