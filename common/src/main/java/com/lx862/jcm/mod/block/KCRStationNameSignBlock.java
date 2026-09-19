package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.CeilingAttachedDirectionalBlock;
import com.lx862.jcm.mod.block.entity.KCRStationNameSignBlockEntity;
import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KCRStationNameSignBlock extends CeilingAttachedDirectionalBlock implements EntityBlockMapper {
    public static final BooleanProperty EXIT_ON_LEFT = BlockProperties.EXIT_ON_LEFT;
    private final boolean stationColored;
    public KCRStationNameSignBlock(Properties settings, boolean stationColored) {
        super(settings, true);
        this.stationColored = stationColored;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(-7, 2, 5.5, 23, 16, 10.5, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new KCRStationNameSignBlockEntity(blockPos, blockState, stationColored);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            world.setBlockAndUpdate(pos, state.cycle(EXIT_ON_LEFT));
            player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "kcr_name_sign.success"));
        });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXIT_ON_LEFT);
    }
}
