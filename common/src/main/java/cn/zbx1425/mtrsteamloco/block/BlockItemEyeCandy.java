package cn.zbx1425.mtrsteamloco.block;

import cn.zbx1425.mtrsteamloco.gui.EyeCandyScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BlockItemEyeCandy extends BlockItem {

    public BlockItemEyeCandy(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(EyeCandyScreen.createScreen(hand, Minecraft.getInstance().screen));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}