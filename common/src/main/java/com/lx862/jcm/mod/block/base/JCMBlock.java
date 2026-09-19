package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

public abstract class JCMBlock extends Block {
    public JCMBlock(Properties settings) {
        super(settings.forceSolidOn());
    }

    /**
     * Loop through each block that should be associated with this block (i.e. Multiblock structure)<br>
     * The callback can be called multiple times on, for example a multi-block, and that multiple block entity should be updated to create a consistent state.<br>
     */
    public void loopStructure(BlockState state, Level level, BlockPos sourcePos, BiConsumer<BlockState, BlockEntity> callback) {
        for(BlockPos bPos : getAllPos(state, level, sourcePos)) {
            BlockState bs = level.getBlockState(sourcePos);
            BlockEntity be = BlockUtil.getBlockEntityOrNull(level, bPos);
            if(be != null) callback.accept(bs, be);
        }
    }

    protected void breakWithoutDropIfCreative(Level world, BlockPos pos, BlockState state, Player player, Block blockInstance, GetLootDropPositionCallback getLootPos) {
        if(player.isCreative()) {
            BlockPos dropPos = getLootPos.get(state, pos);
            if(world.getBlockState(dropPos).getBlock().equals(blockInstance)) {
                world.destroyBlock(dropPos, false);
            }
        }
    }

    /* Get all pos of the entire block structure */
    public BlockPos[] getAllPos(BlockState state, LevelReader world, BlockPos sourcePos) {
        return new BlockPos[]{sourcePos};
    }

    @FunctionalInterface
    public interface GetLootDropPositionCallback {
        BlockPos get(BlockState state, BlockPos pos);
    }
}
