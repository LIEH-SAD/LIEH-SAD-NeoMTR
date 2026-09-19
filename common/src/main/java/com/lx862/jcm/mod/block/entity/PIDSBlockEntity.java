package com.lx862.jcm.mod.block.entity;

import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;

public abstract class PIDSBlockEntity extends JCMBlockEntity {
    private final String[] customMessages;
    private final boolean[] rowHidden;
    private boolean hidePlatformNumber;
    private String pidsPresetId;
    private final LongAVLTreeSet platformIds = new LongAVLTreeSet();
    public PIDSBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.customMessages = new String[getRowAmount()];
        this.rowHidden = new boolean[getRowAmount()];
        this.pidsPresetId = getDefaultPresetId();
    }

    @Override
    public void readCompoundTag(ValueInput valueInput) {
        super.readCompoundTag(valueInput);
        for(int i = 0; i < getRowAmount(); i++) {
            this.customMessages[i] = valueInput.getStringOr("message" + i, "");
            this.rowHidden[i] = valueInput.getBooleanOr("hide_arrival" + i, false);
        }

        platformIds.clear();
        final ValueInput.TypedInputList<Long> platformIdsArray = valueInput.listOrEmpty("platform_ids", Codec.LONG);
        platformIdsArray.forEach(platformId -> platformIds.add((long) platformId));

        this.hidePlatformNumber = valueInput.getBooleanOr("hide_platform_number", false);
        this.pidsPresetId = valueInput.getStringOr("preset_id", getDefaultPresetId());
    }

    @Override
    public void writeCompoundTag(ValueOutput valueOutput) {
        super.writeCompoundTag(valueOutput);
        for(int i = 0; i < getRowAmount(); i++) {
            String customMessage = this.customMessages[i] == null ? "" : this.customMessages[i];
            boolean rowHidden = this.rowHidden[i];
            valueOutput.putString(("message" + i), customMessage);
            valueOutput.putBoolean(("hide_arrival" + i), rowHidden);
        }

        final ValueOutput.TypedOutputList<Long> platformIdOutputList = valueOutput.list("platform_ids", Codec.LONG);
        platformIds.forEach(platformIdOutputList::add);
        valueOutput.putBoolean("hide_platform_number", hidePlatformNumber);
        valueOutput.putString("preset_id", this.pidsPresetId);
    }

    public String[] getCustomMessages() {
        return this.customMessages;
    }

    public LongAVLTreeSet getPlatformIds() {
        return platformIds;
    }

    public boolean[] getRowHidden() {
        return this.rowHidden;
    }

    public boolean platformNumberHidden() {
        return this.hidePlatformNumber;
    }

    public void setData(String[] customMessages, LongAVLTreeSet filteredPlatforms, boolean[] rowHidden, boolean hidePlatformNumber, String pidsPresetId) {
        System.arraycopy(customMessages, 0, this.customMessages, 0, customMessages.length);
        System.arraycopy(rowHidden, 0, this.rowHidden, 0, rowHidden.length);
        this.hidePlatformNumber = hidePlatformNumber;
        this.pidsPresetId = pidsPresetId;
        this.platformIds.clear();
        this.platformIds.addAll(filteredPlatforms);
        this.setChanged();
        this.syncData();
    }

    public String getPresetId() {
        return pidsPresetId.isEmpty() ? getDefaultPresetId() : pidsPresetId;
    }

    public abstract String getPIDSType();

    public abstract String getDefaultPresetId();

    public abstract int getRowAmount();
}
