package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.data.TransactionEntry;
import com.lx862.jcm.mod.render.gui.GuiHelper;
import com.lx862.jcm.mod.render.RenderHelper;
import com.lx862.jcm.mod.render.gui.screen.base.AnimatedScreen;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.data.IGui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.text.SimpleDateFormat;
import java.util.List;

public class EnquiryScreen extends AnimatedScreen {
    private static final int BACKGROUND_COLOR = 0xFF034AE2;
    private static final int CONTENT_WIDTH = 260;
    private static final int CONTENT_HEIGHT = 200;
    private final static Identifier fontId = Constants.id("pids_lcd");
    private final List<TransactionEntry> entries;
    private final Button doneButton;
    private final int remainingBalance;

    public EnquiryScreen(List<TransactionEntry> entries, int remainingBalance) {
        super(null, false);
        this.entries = entries;
        this.remainingBalance = remainingBalance;
        this.doneButton = Button.builder(TextUtil.translatable("gui.done"), btn -> onClose()).size(200, 20).build();
    }

    @Override
    protected void init() {
        this.doneButton.setX((width / 2) - (this.doneButton.getWidth() / 2));
        this.doneButton.setY(height - this.doneButton.getHeight() - 10);
        addRenderableWidget(doneButton);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        super.extractBackground(guiGraphics, mouseX, mouseY, tickDelta);

        int screenWidth = width;
        int screenHeight = height;

        int scaleWidth = 475;
        float scaleFactor = Math.min(1.75F, (float)(double)screenWidth / scaleWidth);
        int scaledEnquiryScreenWidth = (int)(CONTENT_WIDTH * scaleFactor);
        int scaledEnquiryScreenHeight = (int)(CONTENT_HEIGHT * scaleFactor);

        GuiHelper.drawRectangle(guiGraphics, (screenWidth - scaledEnquiryScreenWidth) / 2.0, (screenHeight - scaledEnquiryScreenHeight) / 2.0, scaledEnquiryScreenWidth, scaledEnquiryScreenHeight, BACKGROUND_COLOR);
        guiGraphics.centeredText(font, TextUtil.translatable(TextCategory.BLOCK, "mtr_enquiry_machine"), screenWidth / 2, ((screenHeight - scaledEnquiryScreenHeight) / 2) / 2, RenderHelper.ARGB_WHITE);

        int startX = 5;
        int startY = 5;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate((float)((screenWidth - scaledEnquiryScreenWidth) / 2.0), (float)((screenHeight - scaledEnquiryScreenHeight) / 2.0));
        guiGraphics.pose().scale(scaleFactor, scaleFactor);

        String balancePrefix = remainingBalance >= 0 ? "" : "-";
        guiGraphics.text(font, TextUtil.withFont(TextUtil.translatable(TextCategory.GUI, "enquiry_screen.balance", balancePrefix + "$" + Math.abs(remainingBalance)), fontId), startX, startY, RenderHelper.ARGB_WHITE, false);

        startY += 14;
        guiGraphics.text(font, TextUtil.withFont(TextUtil.translatable(TextCategory.GUI, "enquiry_screen.points", "0"), fontId), startX, startY, RenderHelper.ARGB_WHITE, false);
        startY += 14;

        for(int i = 0; i < 10; i++) {
            if(i >= entries.size()) break;
            TransactionEntry entry = entries.get(i);

            guiGraphics.text(font, TextUtil.withFont(TextUtil.literal(entry.getFormattedDate()), fontId), startX, startY + (i * 14), RenderHelper.ARGB_WHITE, false);
            guiGraphics.text(font, TextUtil.withFont(TextUtil.literal(IGui.formatStationName(entry.source)), fontId), startX + 90, startY + (i * 14), RenderHelper.ARGB_WHITE, false);

            MutableComponent balText = TextUtil.withFont(entry.amount < 0 ? TextUtil.literal("-$" + (double)-entry.amount) : entry.amount == 0 ?  TextUtil.literal("$" + (double)entry.amount) : TextUtil.literal("+$" + (double)entry.amount), fontId);
            int balTextWidth = font.width(balText);
            guiGraphics.text(font, balText, CONTENT_WIDTH - balTextWidth - 5, startY + (i * 14), RenderHelper.ARGB_WHITE, false);
        }

        long lastAddValueTime = 0;
        for (TransactionEntry transactionEntry : entries) {
            if (transactionEntry.amount > 0 && transactionEntry.time > lastAddValueTime) {
                lastAddValueTime = transactionEntry.time;
            }
        }

        if (lastAddValueTime != 0) {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(lastAddValueTime);
            guiGraphics.text(font, TextUtil.withFont(TextUtil.translatable(TextCategory.GUI, "enquiry_screen.add_balance", formattedDate), fontId), startX, CONTENT_HEIGHT - 15, RenderHelper.ARGB_WHITE, false);
        }

        guiGraphics.pose().popMatrix();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}