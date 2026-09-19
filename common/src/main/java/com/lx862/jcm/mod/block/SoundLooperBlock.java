package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.JCMBlock;
import com.lx862.jcm.mod.block.entity.SoundLooperBlockEntity;
import com.lx862.jcm.mod.render.gui.screen.ScreenType;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import mtr.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SoundLooperBlock extends JCMBlock implements EntityBlockMapper {
    public SoundLooperBlock(Properties settings) {
        super(settings);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SoundLooperBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult result = player.isHolding(Items.BRUSH.get()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        if(level.isClientSide() && level.getBlockEntity(pos) instanceof SoundLooperBlockEntity be) {
            ScreenType.BLOCK_CONFIG_SOUND_LOOPER.open(be);
        }

        return result;
    }
}
