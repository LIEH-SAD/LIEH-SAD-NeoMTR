package com.lx862.jcm.mod.item;

import com.lx862.jcm.mod.registry.Blocks;
import mtr.block.BlockPSDAPGBase;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.block.ITripleBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;
import java.util.function.Consumer;

/**
 * Copied from MTR to replace hardcoded block/item reference to our DRL APG
 */
public class ItemDRLAPG extends Item implements IBlock {
    private final EnumPSDAPGItem item;
    private final EnumPSDAPGType type;

    public ItemDRLAPG(EnumPSDAPGItem item, EnumPSDAPGType type, Properties itemSettings) {
        super(itemSettings);
        this.item = item;
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final int horizontalBlocks = item.isDoor ? type.isOdd ? 3 : 2 : 1;
        if (blocksNotReplaceable(context, horizontalBlocks, type.isPSD ? 3 : 2, getBlockStateFromItem().getBlock())) {
            return InteractionResult.FAIL;
        }

        final Level world = context.getLevel();
        final Direction playerFacing = context.getHorizontalDirection();
        final BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        for (int x = 0; x < horizontalBlocks; x++) {
            final BlockPos newPos = pos.relative(playerFacing.getClockWise(), x);

            for (int y = 0; y < 2; y++) {
                final BlockState state = getBlockStateFromItem().setValue(BlockPSDAPGBase.FACING, playerFacing).setValue(HALF, y == 1 ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
                if (item.isDoor) {
                    BlockState neighborState = state.setValue(SIDE, x == 0 ? EnumSide.LEFT : EnumSide.RIGHT);
                    if (type.isOdd) {
                        neighborState = neighborState.setValue(ITripleBlock.ODD, x > 0 && x < horizontalBlocks - 1);
                    }
                    world.setBlockAndUpdate(newPos.above(y), neighborState);
                } else {
                    world.setBlockAndUpdate(newPos.above(y), state.setValue(SIDE_EXTENDED, EnumSide.SINGLE));
                }
            }

            if (type.isPSD) {
                world.setBlockAndUpdate(newPos.above(2), BlockPSDTop.getActualState(world, newPos.above(2)));
            }
        }

        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag options) {
        tooltipAdder.accept(Component.translatable(type.isLift ? type.isOdd ? "tooltip.mtr.railway_sign_odd" : "tooltip.mtr.railway_sign_even" : "tooltip.mtr." + item.getSerializedName()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    private BlockState getBlockStateFromItem() {
        switch (item) {
            case DOOR:
                return Blocks.APG_DOOR_DRL.get().defaultBlockState();
            case GLASS:
                return Blocks.APG_GLASS_DRL.get().defaultBlockState();
            case GLASS_END:
                return Blocks.APG_GLASS_END_DRL.get().defaultBlockState();
            default:
                return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
    }

    public static boolean blocksNotReplaceable(UseOnContext context, int width, int height, Block blacklistBlock) {
        final Direction facing = context.getHorizontalDirection();
        final Level world = context.getLevel();
        final BlockPos startingPos = context.getClickedPos().relative(context.getClickedFace());

        for (int x = 0; x < width; x++) {
            final BlockPos offsetPos = startingPos.relative(facing.getClockWise(), x);

            if (blacklistBlock != null) {
                final boolean isBlacklistedBelow = world.getBlockState(offsetPos.below()).is(blacklistBlock);
                final boolean isBlacklistedAbove = world.getBlockState(offsetPos.above(height)).is(blacklistBlock);
                if (isBlacklistedBelow || isBlacklistedAbove) {
                    return true;
                }
            }

            for (int y = 0; y < height; y++) {
                if (!world.getBlockState(offsetPos.above(y)).is(net.minecraft.world.level.block.Blocks.AIR)) {
                    return true;
                }
            }
        }

        return false;
    }

    public enum EnumPSDAPGType {
        APG(false, false, false);

        private final boolean isPSD;
        private final boolean isOdd;
        private final boolean isLift;

        EnumPSDAPGType(boolean isPSD, boolean isOdd, boolean isLift) {
            this.isPSD = isPSD;
            this.isOdd = isOdd;
            this.isLift = isLift;
        }
    }

    public enum EnumPSDAPGItem implements StringRepresentable {

        DOOR("psd_apg_door", true),
        GLASS("psd_apg_glass", false),
        GLASS_END("psd_apg_glass_end", false);

        private final String name;
        private final boolean isDoor;

        EnumPSDAPGItem(String name, boolean isDoor) {
            this.name = name;
            this.isDoor = isDoor;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
