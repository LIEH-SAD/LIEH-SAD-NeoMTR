package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.block.entity.FareSaverBlockEntity;
import com.lx862.jcm.mod.network.block.FareSaverUpdatePacket;
import com.lx862.jcm.mod.registry.Blocks;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.render.gui.screen.base.BlockConfigListScreen;
import com.lx862.jcm.mod.render.gui.widget.IntegerTextField;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.screen.WidgetBetterTextField;

public class FareSaverScreen extends BlockConfigListScreen {
    private final IntegerTextField discountTextField;
    private final WidgetBetterTextField prefixTextField;

    public FareSaverScreen(FareSaverBlockEntity blockEntity) {
        super(Blocks.FARE_SAVER.get().getName(), blockEntity.getBlockPos());
        this.prefixTextField = new WidgetBetterTextField("$", 4);
        this.prefixTextField.setSize(60, 20);
        this.prefixTextField.setValue(blockEntity.getPrefix());
        this.discountTextField = new IntegerTextField(0, 0, 60, 20, 0, 1000000, 2);
        this.discountTextField.setValue(blockEntity.getDiscount());
    }

    @Override
    public void addConfigEntries() {
        addWidget(prefixTextField);
        addWidget(discountTextField);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "faresaver.prefix"), prefixTextField);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "faresaver.discount"), discountTextField);
    }
    @Override
    public void onSave() {
        Networking.sendPacketToServer(new FareSaverUpdatePacket(blockPos, prefixTextField.getValue(), (int)discountTextField.getNumber()));
    }
}
