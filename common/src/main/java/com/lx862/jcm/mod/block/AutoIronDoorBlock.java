package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.AutoIronDoorBlockEntity;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class AutoIronDoorBlock extends DoorBlock implements EntityBlockMapper {
    public AutoIronDoorBlock(Properties settings) {
        super(BlockSetType.IRON, settings);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AutoIronDoorBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(level.isClientSide()) return null;
        return AutoIronDoorBlockEntity::tick;
    }
}
