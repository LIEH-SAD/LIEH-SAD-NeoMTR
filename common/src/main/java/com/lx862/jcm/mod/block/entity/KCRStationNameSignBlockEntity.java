package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class KCRStationNameSignBlockEntity extends BlockEntityMapper {
    public KCRStationNameSignBlockEntity(BlockPos blockPos, BlockState blockState, boolean stationColored) {
        super(stationColored ? BlockEntities.KCR_STATION_NAME_SIGN_STATION_COLOR.get() : BlockEntities.KCR_STATION_NAME_SIGN.get(), blockPos, blockState);
    }
}
