package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RVPIDSSIL1BlockEntity extends PIDSBlockEntity {

    public RVPIDSSIL1BlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.RV_PIDS_SIL_1.get(), blockPos, blockState);
    }

    @Override
    public String getPIDSType() {
        return "rv_pids_sil_1";
    }

    @Override
    public String getDefaultPresetId() {
        return "rv_pids";
    }

    @Override
    public int getRowAmount() {
        return 4;
    }
}
