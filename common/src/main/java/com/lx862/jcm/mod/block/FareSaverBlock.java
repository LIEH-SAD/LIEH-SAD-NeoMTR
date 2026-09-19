package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.Vertical3Block;
import com.lx862.jcm.mod.block.entity.FareSaverBlockEntity;
import com.lx862.jcm.mod.render.gui.screen.ScreenType;
import com.lx862.jcm.mod.util.JCMUtil;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.UUID;

public class FareSaverBlock extends Vertical3Block implements EntityBlockMapper {
    public static final HashMap<UUID, Integer> discountList = new HashMap<>();

    public FareSaverBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(3, 0, 6.5, 13, 16, 9.5, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        UUID playerUuid = player.getUUID();

        if(level.getBlockEntity(pos) instanceof FareSaverBlockEntity be) {
            if (JCMUtil.playerHoldingBrush(player)) {
                if(level.isClientSide()) ScreenType.BLOCK_CONFIG_FARESAVER.open(be);
                return InteractionResult.SUCCESS;
            }
            if(!level.isClientSide()) {
                int discount = be.getDiscount();
                if(discountList.containsKey(playerUuid)) {
                    player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "faresaver.fail", discountList.get(playerUuid)));
                } else {
                    discountList.put(playerUuid, discount);

                    if(discount > 0) {
                        player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "faresaver.success", discount));
                    } else {
                        player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "faresaver.success.sarcasm", discount));
                    }
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FareSaverBlockEntity(blockPos, blockState);
    }
}