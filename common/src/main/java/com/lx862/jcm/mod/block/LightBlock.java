package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.JCMBlock;
import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.block.IBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightBlock extends JCMBlock {
    public static final IntegerProperty LIGHT_LEVEL = BlockProperties.LIGHT_LEVEL;

    public LightBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(LIGHT_LEVEL, 15));
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null && player.isHolding(this.asItem())) {
            //TODO: world.addParticle, no particle in mappings yet
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext CollisionContext) {
        return Shapes.empty();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(world.isClientSide()) return InteractionResult.SUCCESS;

        if(player.isHolding(this.asItem())) {
            BlockState newState = state.cycle(LIGHT_LEVEL);
            world.setBlockAndUpdate(pos, newState);
            player.sendOverlayMessage(TextUtil.translatable(TextCategory.HUD, "light_block.success", IBlock.getStatePropertySafe(newState, LIGHT_LEVEL)));
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIGHT_LEVEL);
    }
}
