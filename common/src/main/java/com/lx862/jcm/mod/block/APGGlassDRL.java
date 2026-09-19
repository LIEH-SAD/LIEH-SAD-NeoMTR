package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.APGGlassDRLBlockEntity;
import com.lx862.jcm.mod.registry.Items;
import mtr.block.BlockAPGGlass;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class APGGlassDRL extends BlockAPGGlass {
    public APGGlassDRL(Properties properties) {
        super(properties);
    }

    @Override
    public Item asItem() {
        return Items.APG_GLASS_DRL.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter BlockGetter, BlockPos pos, CollisionContext CollisionContext) {
        int height = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER ? 2 : 16;
        return IBlock.getVoxelShapeByDirection(0.0, 0.0, 0.0, 16.0, height, 4.0, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter BlockGetter, BlockPos pos, CollisionContext CollisionContext) {
        int height = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER ? 9 : 16;
        return IBlock.getVoxelShapeByDirection(0.0, 0.0, 0.0, 16.0, height, 4.0, IBlock.getStatePropertySafe(state, FACING));
    }
}
