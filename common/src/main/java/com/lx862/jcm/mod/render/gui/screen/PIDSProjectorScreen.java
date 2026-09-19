package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.block.entity.PIDSProjectorBlockEntity;
import com.lx862.jcm.mod.network.block.PIDSProjectorUpdatePacket;
import com.lx862.jcm.mod.registry.Blocks;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.render.gui.widget.CategoryItem;
import com.lx862.jcm.mod.render.gui.widget.DoubleTextField;
import com.lx862.jcm.mod.render.gui.widget.WidgetSet;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;

public class PIDSProjectorScreen extends PIDSScreen {

    private final DoubleTextField xField;
    private final DoubleTextField yField;
    private final DoubleTextField zField;
    private final DoubleTextField scaleField;
    private final DoubleTextField rotateXField;
    private final DoubleTextField rotateYField;
    private final DoubleTextField rotateZField;

    public PIDSProjectorScreen(PIDSProjectorBlockEntity blockEntity) {
        super(Blocks.PIDS_PROJECTOR.get().getName(), blockEntity);

        this.xField = new DoubleTextField(0, 0, 40, 20, -1000, 1000, 0);
        this.yField = new DoubleTextField(0, 0, 40, 20, -1000, 1000, 0);
        this.zField = new DoubleTextField(0, 0, 40, 20, -1000, 1000, 0);
        this.rotateXField = new DoubleTextField(0, 0, 40, 20, -359, 360, 0);
        this.rotateYField = new DoubleTextField(0, 0, 40, 20, -359, 360, 0);
        this.rotateZField = new DoubleTextField(0, 0, 40, 20, -359, 360, 0);
        this.scaleField = new DoubleTextField(0, 0, 40, 20, 0, 30, 0);

        xField.setValue(String.valueOf(blockEntity.getOffsetX()));
        yField.setValue(String.valueOf(blockEntity.getOffsetY()));
        zField.setValue(String.valueOf(blockEntity.getOffsetZ()));
        rotateXField.setValue(String.valueOf(blockEntity.getRotateX()));
        rotateYField.setValue(String.valueOf(blockEntity.getRotateY()));
        rotateZField.setValue(String.valueOf(blockEntity.getRotateZ()));
        scaleField.setValue(String.valueOf(blockEntity.getScale()));
    }

    @Override
    public void addConfigEntries() {
        WidgetSet positionFields = new WidgetSet(20, 0);
        WidgetSet rotationFields = new WidgetSet(20, 0);

        addWidget(xField);
        addWidget(yField);
        addWidget(zField);
        addWidget(rotateXField);
        addWidget(rotateYField);
        addWidget(rotateZField);
        addWidget(scaleField);
        addWidget(positionFields);
        addWidget(rotationFields);

        positionFields.addWidget(xField);
        positionFields.addWidget(yField);
        positionFields.addWidget(zField);

        rotationFields.addWidget(rotateXField);
        rotationFields.addWidget(rotateYField);
        rotationFields.addWidget(rotateZField);

        rotationFields.setXYSize(0, 0, 140, 20);
        positionFields.setXYSize(0, 0, 140, 20);
        listViewWidget.add(new CategoryItem(TextUtil.translatable(TextCategory.GUI, "pids.listview.category.projection")));
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.position"), positionFields);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.rotate"), rotationFields);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.scale"), scaleField);
        listViewWidget.add(new CategoryItem(TextUtil.translatable(TextCategory.GUI, "pids.listview.category.pids")));
        super.addConfigEntries();
    }

    @Override
    public void onSave() {
        String[] customMessages = new String[customMessagesWidgets.length];
        boolean[] rowHidden = new boolean[rowHiddenWidgets.length];

            for(int i = 0; i < customMessagesWidgets.length; i++) {
            customMessages[i] = this.customMessagesWidgets[i].getValue();
        }

            for(int i = 0; i < rowHiddenWidgets.length; i++) {
            rowHidden[i] = this.rowHiddenWidgets[i].selected();
        }

        Networking.sendPacketToServer(new PIDSProjectorUpdatePacket(blockPos, filteredPlatforms, customMessages, rowHidden, hidePlatformNumber.selected(), presetId, xField.getNumber(), yField.getNumber(), zField.getNumber(), rotateXField.getNumber(), rotateYField.getNumber(), rotateZField.getNumber(), scaleField.getNumber()));
    }
}
