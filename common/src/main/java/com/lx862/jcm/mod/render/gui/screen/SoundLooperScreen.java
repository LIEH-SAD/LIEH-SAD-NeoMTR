package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.block.entity.SoundLooperBlockEntity;
import com.lx862.jcm.mod.network.block.SoundLooperUpdatePacket;
import com.lx862.jcm.mod.registry.Blocks;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.render.gui.screen.base.BlockConfigListScreen;
import com.lx862.jcm.mod.render.gui.widget.BlockPosWidget;
import com.lx862.jcm.mod.render.gui.widget.IntegerTextField;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.screen.WidgetBetterTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

public class SoundLooperScreen extends BlockConfigListScreen {
    private final Button soundCategoryButton;
    private final WidgetBetterTextField soundIdTextField;
    private final IntegerTextField repeatTickTextField;
    private final IntegerTextField soundVolumeTextField;
    private final Checkbox needRedstoneCheckbox;
    private final Checkbox limitSoundRangeCheckbox;
    private final BlockPosWidget corner1Widget;
    private final BlockPosWidget corner2Widget;
    private int soundCategory;

    public SoundLooperScreen(SoundLooperBlockEntity blockEntity) {
        super(Blocks.SOUND_LOOPER.get().getName(), blockEntity.getBlockPos());

        this.soundCategoryButton = Button.builder(Component.empty(), (btn) -> {
            setSoundCategory((this.soundCategory + 1) % SoundLooperBlockEntity.SOURCE_LIST.length);
        }).size(60, 20).build();

        setSoundCategory(blockEntity.getSoundCategory());

        this.corner1Widget = new BlockPosWidget(0, 0, 120, 20);
        this.corner1Widget.setBlockPos(blockEntity.getCorner1());
        this.corner2Widget = new BlockPosWidget(0, 0, 120, 20);
        this.corner2Widget.setBlockPos(blockEntity.getCorner2());

        this.soundIdTextField = new WidgetBetterTextField("", "mtr:ticket_barrier", 100);
        this.soundIdTextField.setSize(100, 20);
        this.soundIdTextField.setValue(blockEntity.getSoundId());
        this.soundVolumeTextField = new IntegerTextField(0, 0, 60, 20, 1, 1000, 100);
        this.soundVolumeTextField.setValue((int)(blockEntity.getSoundVolume() * 100));
        this.repeatTickTextField = new IntegerTextField(0, 0, 60, 20, 1, 99999, 20);
        this.repeatTickTextField.setValue(blockEntity.getLoopInterval());
        this.needRedstoneCheckbox = Checkbox.builder(Component.empty(), Minecraft.getInstance().font).selected(blockEntity.needRedstone()).pos(0, 0).build();
        this.limitSoundRangeCheckbox = Checkbox.builder(Component.empty(), Minecraft.getInstance().font).selected(blockEntity.rangeLimited()).pos(0, 0).onValueChange((cb, checked) -> {
            if(checked) {
                this.corner1Widget.setActive(true);
                this.corner2Widget.setActive(true);
            } else {
                this.corner1Widget.setActive(false);
                this.corner2Widget.setActive(false);
            }
        }).build();
        this.corner1Widget.setActive(blockEntity.rangeLimited());
        this.corner2Widget.setActive(blockEntity.rangeLimited());
    }

    @Override
    public void addConfigEntries() {
        corner1Widget.addWidget(this::addWidget);
        corner2Widget.addWidget(this::addWidget);
        addWidget(soundCategoryButton);
        addWidget(soundIdTextField);
        addWidget(soundVolumeTextField);
        addWidget(repeatTickTextField);
        addWidget(needRedstoneCheckbox);
        addWidget(limitSoundRangeCheckbox);
        addWidget(corner1Widget);
        addWidget(corner2Widget);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "sound_looper.listview.title.sound_category"), soundCategoryButton);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "sound_looper.listview.title.sound_id"), soundIdTextField);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "sound_looper.listview.title.sound_volume"), soundVolumeTextField);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "sound_looper.listview.title.repeat_tick"), repeatTickTextField);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "sound_looper.listview.title.need_redstone"), needRedstoneCheckbox);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "sound_looper.listview.title.limit_range"), limitSoundRangeCheckbox);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "sound_looper.listview.title.pos1"), corner1Widget);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "sound_looper.listview.title.pos2"), corner2Widget);
    }

    @Override
    public void onSave() {
        Networking.sendPacketToServer(new SoundLooperUpdatePacket(blockPos, corner1Widget.getBlockPos(), corner2Widget.getBlockPos(), soundIdTextField.getValue(), soundCategory, (int)repeatTickTextField.getNumber(), soundVolumeTextField.getNumber() / 100f, needRedstoneCheckbox.selected(), limitSoundRangeCheckbox.selected()));
    }

    private void setSoundCategory(int category) {
        if(category >= SoundLooperBlockEntity.SOURCE_LIST.length) {
            this.soundCategory = 0;
        } else {
            this.soundCategory = category;
        }
        this.soundCategoryButton.setMessage(TextUtil.literal(SoundLooperBlockEntity.SOURCE_LIST[category].getName()));
    }
}
