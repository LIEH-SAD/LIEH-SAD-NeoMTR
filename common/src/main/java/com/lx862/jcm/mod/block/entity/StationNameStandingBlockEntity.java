package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import mtr.block.BlockStationNameTallBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class StationNameStandingBlockEntity extends BlockStationNameTallBase.TileEntityStationNameTallBase {
    public StationNameStandingBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.STATION_NAME_STANDING.get(), blockPos, blockState, 0.07F, false);
    }
}
