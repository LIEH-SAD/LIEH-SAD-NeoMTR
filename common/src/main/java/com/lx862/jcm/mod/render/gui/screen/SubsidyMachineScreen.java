package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.block.entity.SubsidyMachineBlockEntity;
import com.lx862.jcm.mod.network.block.SubsidyMachineUpdatePacket;
import com.lx862.jcm.mod.registry.Blocks;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.render.gui.screen.base.BlockConfigListScreen;
import com.lx862.jcm.mod.render.gui.widget.IntegerTextField;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;

public class SubsidyMachineScreen extends BlockConfigListScreen {
    private final IntegerTextField priceTextField;
    private final IntegerTextField cooldownTextField;

    public SubsidyMachineScreen(SubsidyMachineBlockEntity blockEntity) {
        super(Blocks.SUBSIDY_MACHINE.get().getName(), blockEntity.getBlockPos());
        this.priceTextField = new IntegerTextField(0, 0, 60, 20, 0, 50000, 10, TextUtil.translatable(TextCategory.GUI, "subsidy_machine.currency"));
        this.cooldownTextField = new IntegerTextField(0, 0, 60, 20, 0, 1200, 0);

        this.priceTextField.setValue(blockEntity.getSubsidyAmount());
        this.cooldownTextField.setValue(blockEntity.getCooldown());
    }

    @Override
    public void addConfigEntries() {
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "subsidy_machine.price"), priceTextField);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "subsidy_machine.cooldown"), cooldownTextField);

        addWidget(priceTextField);
        addWidget(cooldownTextField);
    }

    @Override
    public void onSave() {
        Networking.sendPacketToServer(new SubsidyMachineUpdatePacket(blockPos, (int)priceTextField.getNumber(), (int)cooldownTextField.getNumber()));
    }
}
