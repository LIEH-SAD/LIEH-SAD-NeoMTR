package com.lx862.jcm.mod.render.gui.screen.base;

import com.lx862.jcm.mod.render.gui.widget.ListViewWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

/**
 * Block Config Screen with a GUI list, override {@link BlockConfigListScreen#addConfigEntries()} to add items to the list.
 */
public abstract class BlockConfigListScreen extends BlockConfigScreen {
    protected final ListViewWidget listViewWidget;

    public BlockConfigListScreen(Component title, BlockPos blockPos) {
        super(title, blockPos);
        this.listViewWidget = new ListViewWidget();
    }

    @Override
    protected void init() {
        super.init();
        int contentWidth = (int)Math.min((width * 0.75), MAX_CONTENT_WIDTH);
        int listViewHeight = Math.max(150, (int)((height - 60) * 0.75));
        int startX = (width - contentWidth) / 2;
        int startY = getStartY() + TEXT_PADDING;
        int bottomEntryHeight = (height - startY - listViewHeight - (BOTTOM_ROW_MARGIN * 2));

        listViewWidget.reset();
        listViewWidget.setXYSize(startX, startY, contentWidth, listViewHeight);
        addConfigEntries();
        addBottomRowEntry(startX, startY + listViewHeight + BOTTOM_ROW_MARGIN, contentWidth, bottomEntryHeight);
        listViewWidget.positionWidgets();
        addRenderableWidget(listViewWidget);
    }

    public abstract void addConfigEntries();
}
