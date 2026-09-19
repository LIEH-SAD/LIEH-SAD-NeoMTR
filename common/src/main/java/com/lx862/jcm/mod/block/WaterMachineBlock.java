package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.Vertical2Block;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterMachineBlock extends Vertical2Block {
    public WaterMachineBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(2.5, 0, 2.5, 13.5, 16, 13.5, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(stack.is(Items.GLASS_BOTTLE)) {
            fillBottleForPlayer(player, hand);
            return InteractionResult.SUCCESS;
        }

        if(stack.is(Items.BUCKET)) {
            fillBucketForPlayer(player, hand);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private static void fillBottleForPlayer(Player player, InteractionHand hand) {
        ItemStack newWaterBottle = new ItemStack(Items.POTION);
        newWaterBottle.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
        offerOrDrop(player, hand, newWaterBottle);
        playSplashSoundToPlayer(player.level());
    }

    private static void fillBucketForPlayer(Player player, InteractionHand hand) {
        ItemStack newWaterBucket = new ItemStack(Items.POTION);
        offerOrDrop(player, hand, newWaterBucket);
        playSplashSoundToPlayer(player.level());
    }

    private static void offerOrDrop(Player player, InteractionHand hand, ItemStack stack) {
        ItemStack playerHolding = player.getItemInHand(hand);
        playerHolding.shrink(1);

        if(playerHolding.isEmpty()) {
            player.setItemInHand(hand, stack);
        } else {
            player.getInventory().add(stack);
        }
    }

    private static void playSplashSoundToPlayer(Level level) {
        level.playSound(null, 0, 0,0, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS);
    }
}