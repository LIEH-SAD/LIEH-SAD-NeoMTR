package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.block.base.WallAttachedBlock;
import com.lx862.jcm.mod.block.entity.SubsidyMachineBlockEntity;
import com.lx862.jcm.mod.data.JCMServerStats;
import com.lx862.jcm.mod.render.gui.screen.ScreenType;
import com.lx862.jcm.mod.util.JCMUtil;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import mtr.block.IBlock;
import mtr.data.TicketSystem;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.UUID;

public class SubsidyMachineBlock extends WallAttachedBlock implements EntityBlockMapper {
    private static final Object2LongOpenHashMap<UUID> cooldownMap = new Object2LongOpenHashMap<>();
    public SubsidyMachineBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(4, 1.25, 0, 12, 14.75, 2, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if(level.getBlockEntity(pos) instanceof SubsidyMachineBlockEntity be) {
            if(JCMUtil.playerHoldingBrush(player) && level.isClientSide()) {
                ScreenType.BLOCK_CONFIG_SUBSIDY_MACHINE.open(be);
            } else if(!level.isClientSide()) {
                if(cooldownExpired(player, be.getCooldown())) {
                    updateCooldown(player);
                    int finalBalance = addMTRBalanceToPlayer(level, player, be.getSubsidyAmount());
                    player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "subsidy_machine.success", be.getSubsidyAmount(), finalBalance));
                } else {
                    int remainingSec = Math.round(be.getCooldown() - getCooldown(player));
                    player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "subsidy_machine.fail", remainingSec).withStyle(ChatFormatting.RED));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SubsidyMachineBlockEntity(blockPos, blockState);
    }

    private static int addMTRBalanceToPlayer(Level world, Player player, int amount) {
        return TicketSystem.getPlayerScore(world, player, TicketSystem.BALANCE_OBJECTIVE).add(amount);
    }

    private static boolean cooldownExpired(Player player, int cooldown) {
        return getCooldown(player) >= cooldown;
    }

    private static long getCooldown(Player player) {
        return (JCMServerStats.getGameTick() - cooldownMap.getOrDefault(player.getUUID(), 0)) / Constants.MC_TICK_PER_SECOND;
    }

    private static void updateCooldown(Player player) {
        cooldownMap.put(player.getUUID(), JCMServerStats.getGameTick());
    }
}