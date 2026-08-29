package cn.zbx1425.mtrsteamloco.gui;

import mtr.DataComponentTypes;
import mtr.item.ItemRailModifier;
import mtr.render.RenderTrains;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class RailPreviewOverlay {

    private static final int OVERLAY_COLOR = 0xFFFFFFFF;
    private static final int OVERLAY_BG_COLOR = 0x80000000;

    /** Draw the XYZ offset between the selected node and the preview end (aimed node or the player). */
    public static void render(GuiGraphicsExtractor guiGraphics) {
        final Minecraft client = Minecraft.getInstance();
        final LocalPlayer player = client.player;
        if (player == null || client.screen != null) return;

        final ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ItemRailModifier)) return;
        final BlockPos posStart = stack.get(DataComponentTypes.START_POS.get());
        if (posStart == null) return;

        final BlockPos posEnd = RenderTrains.getRailPreviewEndPos(player, posStart);
        if (posEnd == null) return;

        final int dx = posEnd.getX() - posStart.getX();
        final int dy = posEnd.getY() - posStart.getY();
        final int dz = posEnd.getZ() - posStart.getZ();

        final Font font = client.font;
        final String text = String.format("X: %+d  Y: %+d  Z: %+d", dx, dy, dz);
        final int textWidth = font.width(text);
        final int windowWidth = client.getWindow().getGuiScaledWidth();
        final int x = (windowWidth - textWidth) / 2;
        final int y = client.getWindow().getGuiScaledHeight() - 64;

        // Dark background bar for readability
        guiGraphics.fill(x - 4, y - 2, x + textWidth + 4, y + font.lineHeight + 2, OVERLAY_BG_COLOR);
        guiGraphics.text(font, text, x, y, OVERLAY_COLOR, true);
    }
}
