package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.block.entity.PIDSBlockEntity;
import com.lx862.jcm.mod.data.pids.PIDSManager;
import com.lx862.jcm.mod.network.block.PIDSUpdatePacket;
import com.lx862.jcm.mod.registry.Blocks;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.render.gui.screen.base.BlockConfigListScreen;
import com.lx862.jcm.mod.render.gui.widget.ContentItem;
import com.lx862.jcm.mod.render.gui.widget.HorizontalWidgetSet;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import mtr.client.ClientData;
import mtr.data.*;
import mtr.screen.DashboardListSelectorScreen;
import mtr.screen.WidgetBetterTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PIDSScreen extends BlockConfigListScreen {
    protected final WidgetBetterTextField[] customMessagesWidgets;
    protected final Checkbox[] rowHiddenWidgets;
    protected final Checkbox hidePlatformNumber;
    protected final Button choosePresetButton;
    protected final Button choosePlatformButton;

    protected LongAVLTreeSet filteredPlatforms;
    protected String presetId;

    public PIDSScreen(Component title, PIDSBlockEntity blockEntity) {
        super(title, blockEntity.getBlockPos());
        this.filteredPlatforms = new LongAVLTreeSet();
        this.customMessagesWidgets = new WidgetBetterTextField[blockEntity.getCustomMessages().length];
        this.rowHiddenWidgets = new Checkbox[blockEntity.getRowHidden().length];

        for(int i = 0; i < blockEntity.getCustomMessages().length; i++) {
            String customMessage = blockEntity.getCustomMessages()[i];
            this.customMessagesWidgets[i] = new WidgetBetterTextField(TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.custom_message").getString());
            this.customMessagesWidgets[i].setSize(120, 20);
            this.customMessagesWidgets[i].setValue(customMessage);
        }

        for(int i = 0; i < blockEntity.getRowHidden().length; i++) {
            boolean rowIsHidden = blockEntity.getRowHidden()[i];
            this.rowHiddenWidgets[i] = Checkbox.builder(TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.row_hidden"), Minecraft.getInstance().font).selected(rowIsHidden).build();
        }

        this.hidePlatformNumber = Checkbox.builder(Component.empty(), Minecraft.getInstance().font).selected(blockEntity.platformNumberHidden()).build();

        this.choosePresetButton = Button.builder(TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.choose_preset"), (btn) -> {
            Minecraft.getInstance().setScreen(
                new PIDSPresetScreen(blockEntity, this.presetId, (str) -> {
                    this.presetId = str;
                }).withPreviousScreen(this)
            );
        }).size(60, 20).build();

        this.choosePlatformButton = Button.builder(TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.change_platform"), (btn) -> {
            final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, blockPos);
            if(station == null) {
                //platformsForList = getPlatformsForList(new ObjectArrayList<>(station.savedRails));
            } else {
                final List<NameColorDataBase> platformsForList = new ArrayList<>();
                final List<Platform> platforms = new ArrayList<>(ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id).values());
                Collections.sort(platforms);
                platforms.stream().map(platform -> new DataConverter(platform.id, platform.name + " " + IGui.mergeStations(ClientData.DATA_CACHE.requestPlatformIdToRoutes(platform.id).stream().map(route -> route.stationDetails.get(route.stationDetails.size() - 1).stationName).collect(Collectors.toList())), 0)).forEach(platformsForList::add);
                final Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(new DashboardListSelectorScreen(() -> {
                    minecraft.setScreen(this);
                }, platformsForList, blockEntity.getPlatformIds(), false, false));
            }
        }).size(60, 20).build();

        this.presetId = blockEntity.getPresetId();
        this.filteredPlatforms = blockEntity.getPlatformIds();
    }

    public PIDSScreen(PIDSBlockEntity pidsBlockEntity) {
        this(Blocks.RV_PIDS.get().getName(), pidsBlockEntity);
    }

    @Override
    public void addConfigEntries() {
        // Preset button
        addWidget(choosePresetButton);
        ContentItem presetEntry = new ContentItem(TextUtil.translatable(TextCategory.GUI, "pids.listview.title.pids_preset"), choosePresetButton, 26);
        presetEntry.setIconCallback((guiGraphics, startX, startY, width, height) -> {
            PIDSPresetScreen.drawPIDSPreview(PIDSManager.getPreset(presetId), guiGraphics, startX, startY, width, height, true);
        });
        listViewWidget.add(presetEntry);

        // Filter Platform button
        ObjectList<String> platformList = new ObjectArrayList<>();
        for(long filteredPlatform : filteredPlatforms) {
            Platform platform = ClientData.PLATFORMS.stream().filter(p -> p.id == filteredPlatform).findFirst().orElse(null);
            if(platform == null) {
                platformList.add("?");
            } else {
                platformList.add(platform.name);
            }
        }
        String platforms = String.join(",", platformList);
        listViewWidget.add(filteredPlatforms.isEmpty() ? TextUtil.translatable(TextCategory.GUI, "pids.listview.title.filtered_platform.nearby") : TextUtil.translatable(TextCategory.GUI, "pids.listview.title.filtered_platform", platforms), choosePlatformButton);
        addWidget(choosePlatformButton);

        for(int i = 0; i < this.customMessagesWidgets.length; i++) {
            addWidget(this.customMessagesWidgets[i]);
            addWidget(this.rowHiddenWidgets[i]);

            int w = listViewWidget.getWidth();
            this.rowHiddenWidgets[i].setWidth(95);
            this.customMessagesWidgets[i].setWidth(w - this.rowHiddenWidgets[i].getWidth() - 18);

            HorizontalWidgetSet widgetSet = new HorizontalWidgetSet();
            widgetSet.addWidget(this.customMessagesWidgets[i]);
            widgetSet.addWidget(this.rowHiddenWidgets[i]);
            widgetSet.setXYSize(listViewWidget.getX(), 20, listViewWidget.getWidth(), 20);
            listViewWidget.add(null, widgetSet);
        }

        addWidget(hidePlatformNumber);
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "pids.listview.title.hide_platform_number"), hidePlatformNumber);
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

        Networking.sendPacketToServer(new PIDSUpdatePacket(blockPos, filteredPlatforms, customMessages, rowHidden, hidePlatformNumber.selected(), presetId));
    }
}
