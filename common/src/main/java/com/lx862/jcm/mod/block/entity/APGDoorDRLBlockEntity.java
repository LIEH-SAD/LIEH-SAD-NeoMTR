package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import mtr.block.BlockPSDAPGDoorBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class APGDoorDRLBlockEntity extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase {
    public APGDoorDRLBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.APG_DOOR_DRL.get(), pos, state);
    }
}