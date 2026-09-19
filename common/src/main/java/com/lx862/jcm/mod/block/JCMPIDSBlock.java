package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.Horizontal2MirroredBlock;
import com.lx862.jcm.mod.block.entity.PIDSBlockEntity;
import com.lx862.jcm.mod.render.gui.screen.ScreenType;
import mtr.mappings.EntityBlockMapper;
import mtr.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class JCMPIDSBlock extends Horizontal2MirroredBlock implements EntityBlockMapper {
    public JCMPIDSBlock(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult interactionResult = player.isHolding(Items.BRUSH.get()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        if(player.isHolding(Items.BRUSH.get())) {
            if(level.isClientSide() && level.getBlockEntity(pos) instanceof PIDSBlockEntity be) {
                ScreenType.BLOCK_CONFIG_PIDS.open(be);
            }
        }
        return interactionResult;
    }
}
