package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.WallAttachedBlock;
import com.lx862.jcm.mod.block.entity.OperatorButtonBlockEntity;
import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.block.IBlock;
import mtr.Items;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OperatorButtonBlock extends WallAttachedBlock implements EntityBlock {
    private final int poweredDuration;
    public static final BooleanProperty POWERED = BlockProperties.POWERED;

    public OperatorButtonBlock(Properties settings, int poweredDuration) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
        this.poweredDuration = poweredDuration;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(5, 5, 0, 11, 11.5, 0.2, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(world.isClientSide()) return InteractionResult.SUCCESS;

        return IBlock.checkHoldingItem(world, player, (item) -> {
            setPowered(world, state, pos, true);
            world.scheduleTick(pos, this, poweredDuration);
        }, () -> {
            player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "operator_button.fail").withStyle(ChatFormatting.RED));
        }, Items.DRIVER_KEY.get());
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED)) {
            setPowered(serverWorld, state, pos, false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return IBlock.getStatePropertySafe(state, POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return IBlock.getStatePropertySafe(state, POWERED) ? 15 : 0;
    }

    private void setPowered(Level world, BlockState blockState, BlockPos pos, boolean powered) {
        world.setBlockAndUpdate(pos, blockState.setValue(POWERED, powered));
        updateNearbyBlock(world, pos, blockState.getValue(FACING));
    }

    private void updateNearbyBlock(Level world, BlockPos pos, Direction blockFacing) {
        world.updateNeighborsAt(pos.relative(blockFacing), this);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OperatorButtonBlockEntity(pos, state);
    }
}
