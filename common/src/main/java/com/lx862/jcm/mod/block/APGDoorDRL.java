package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.APGDoorDRLBlockEntity;
import com.lx862.jcm.mod.registry.Items;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class APGDoorDRL extends BlockPSDAPGDoorBase implements EntityBlockMapper {
    public APGDoorDRL(Properties properties) {
        super(properties);
    }

    @Override
    public Item asItem() {
        return Items.APG_DOOR_DRL.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new APGDoorDRLBlockEntity(blockPos, blockState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter BlockGetter, BlockPos pos, CollisionContext CollisionContext) {
        int height = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER ? 2 : 16;
        return IBlock.getVoxelShapeByDirection(0.0, 0.0, 0.0, 16.0, height, 4.0, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter BlockGetter, BlockPos pos, CollisionContext CollisionContext) {
        final BlockEntity blockEntity = BlockGetter.getBlockEntity(pos);
        final Level world = blockEntity == null ? null : blockEntity.getLevel();
        if(world == null || !world.isClientSide()) return Shapes.empty();

        if(!((TileEntityPSDAPGDoorBase) blockEntity).isOpen()) {
            int height = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER ? 9 : 16;
            return IBlock.getVoxelShapeByDirection(0.0, 0.0, 0.0, 16.0, height, 4.0, IBlock.getStatePropertySafe(state, FACING));
        }
        return Shapes.empty();
    }
}
