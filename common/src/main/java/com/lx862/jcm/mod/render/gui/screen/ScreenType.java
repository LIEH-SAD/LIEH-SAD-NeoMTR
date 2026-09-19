package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.block.entity.*;
import com.lx862.jcm.mod.data.TransactionEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.Consumer;

public enum ScreenType {
    BLOCK_CONFIG_BUTTERFLY(be -> Minecraft.getInstance().setScreen(new ButterflyLightScreen((ButterflyLightBlockEntity) be))),
    BLOCK_CONFIG_FARESAVER(be -> Minecraft.getInstance().setScreen(new FareSaverScreen((FareSaverBlockEntity) be))),
    BLOCK_CONFIG_PIDS(be -> Minecraft.getInstance().setScreen(new PIDSScreen((PIDSBlockEntity) be))),
    BLOCK_CONFIG_SOUND_LOOPER(be -> Minecraft.getInstance().setScreen(new SoundLooperScreen((SoundLooperBlockEntity) be))),
    BLOCK_CONFIG_SUBSIDY_MACHINE(be -> Minecraft.getInstance().setScreen(new SubsidyMachineScreen((SubsidyMachineBlockEntity)be))),
    BLOCK_CONFIG_PIDS_PROJECTOR(be -> Minecraft.getInstance().setScreen(new PIDSProjectorScreen((PIDSProjectorBlockEntity) be))),
    ENQUIRY_GUI_CLASSIC(null),
    ENQUIRY_GUI_RV(null);


    private final Consumer<BlockEntity> onRun;
    ScreenType(Consumer<BlockEntity> onRun) {
        this.onRun = onRun;
    }

    public void open(BlockEntity be) {
        onRun.accept(be);
    }

    public static void openEnquiryScreen(ScreenType type, BlockPos pos, List<TransactionEntry> entries, int remainingBalance) {
        if(type == ENQUIRY_GUI_RV) {
            Minecraft.getInstance().setScreen(new RVEnquiryScreen(pos, entries, remainingBalance));
        }
        if(type == ENQUIRY_GUI_CLASSIC) {
            Minecraft.getInstance().setScreen(new EnquiryScreen(entries, remainingBalance));
        }
    }
}
