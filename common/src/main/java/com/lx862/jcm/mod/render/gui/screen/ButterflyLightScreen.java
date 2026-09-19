package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.block.entity.ButterflyLightBlockEntity;
import com.lx862.jcm.mod.network.block.ButterflyLightUpdatePacket;
import com.lx862.jcm.mod.registry.Blocks;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.render.gui.screen.base.BlockConfigListScreen;
import com.lx862.jcm.mod.render.gui.widget.IntegerTextField;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;

public class ButterflyLightScreen extends BlockConfigListScreen {
    private final IntegerTextField startBlinkingSecondsTextField;
    private final ButterflyLightBlockEntity blockEntity;

    public ButterflyLightScreen(ButterflyLightBlockEntity blockEntity) {
        super(Blocks.BUTTERFLY_LIGHT.get().getName(), blockEntity.getBlockPos());
        this.blockEntity = blockEntity;
        this.startBlinkingSecondsTextField = new IntegerTextField(0, 0, 60, 20, 0, 100000, 10);
        this.startBlinkingSecondsTextField.setValue(blockEntity.getStartBlinkingSeconds());
    }

    @Override
    public void addConfigEntries() {
        addWidget(startBlinkingSecondsTextField);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "butterfly_light.countdown"), startBlinkingSecondsTextField);
    }

    @Override
    public void onSave() {
        Networking.sendPacketToServer(new ButterflyLightUpdatePacket(blockPos, (int) startBlinkingSecondsTextField.getNumber()));
    }
}
