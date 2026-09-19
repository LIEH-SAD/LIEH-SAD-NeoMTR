package com.lx862.jcm.mod.render.gui.screen.base;

import com.lx862.jcm.mod.render.gui.GuiHelper;
import com.lx862.jcm.mod.render.gui.widget.WidgetSet;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.client.ClientData;
import mtr.data.IGui;
import mtr.data.RailwayData;
import mtr.data.Station;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * GUI Screen for configuring block settings, you should extend this class for your own block config screen
 */
public abstract class BlockConfigScreen extends TitledScreen implements GuiHelper {
    protected final BlockPos blockPos;
    protected final WidgetSet bottomEntryWidget;
    private final Button saveButton;
    private final Button discardButton;
    private boolean discardConfig = false;

    public BlockConfigScreen(Component title, BlockPos blockPos) {
        super(title, false);
        this.blockPos = blockPos;

        this.saveButton = Button.builder(TextUtil.translatable(TextCategory.GUI, "block_config.save"), (btn) -> {
            onClose();
        }).size(0, 20).build();

        this.discardButton = Button.builder(TextUtil.translatable(TextCategory.GUI, "block_config.discard"), (btn) -> {
            discardConfig = true;
            onClose();
        }).size(0, 20).build();

        this.bottomEntryWidget = new WidgetSet(20);
    }

    protected void addBottomRowEntry(int x, int y, int width, int height) {
        bottomEntryWidget.reset();
        addRenderableWidget(saveButton);
        addRenderableWidget(discardButton);

        bottomEntryWidget.addWidget(saveButton);
        bottomEntryWidget.addWidget(discardButton);
        bottomEntryWidget.setXYSize(x, y, width, height);
        addRenderableWidget(bottomEntryWidget);
    }

    @Override
    public MutableComponent getScreenSubtitle() {
        Station atStation = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, blockPos);

        if(atStation != null) {
            return TextUtil.translatable(TextCategory.GUI,
                    "block_config.subtitle_with_station",
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                    IGui.formatStationName(atStation.name)
            );
        } else {
            return TextUtil.translatable(TextCategory.GUI,
                    "block_config.subtitle",
                    blockPos.getX(), blockPos.getY(), blockPos.getZ()
            );
        }
    }

    public abstract void onSave();

    @Override
    public void onClose() {
        // Save config by default, unless explicitly requested not to
        if(!discardConfig) {
            onSave();
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
