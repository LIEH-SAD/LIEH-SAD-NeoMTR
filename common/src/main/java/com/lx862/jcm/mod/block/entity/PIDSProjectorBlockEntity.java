package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PIDSProjectorBlockEntity extends PIDSBlockEntity {
    private double x;
    private double y;
    private double z;
    private double rotateX;
    private double rotateY;
    private double rotateZ;
    private double scale;

    public PIDSProjectorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.PIDS_PROJECTOR.get(), blockPos, blockState);
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.scale = 1.0f;
        this.rotateX = 0;
        this.rotateY = 0;
        this.rotateZ = 0;
    }

    @Override
    public void readCompoundTag(ValueInput valueInput) {
        super.readCompoundTag(valueInput);
        this.x = valueInput.getDoubleOr("x1", 0);
        this.y = valueInput.getDoubleOr("y1", 0);
        this.z = valueInput.getDoubleOr("z1", 0);
        this.rotateX = valueInput.getDoubleOr("rotateX", 0);
        this.rotateY = valueInput.getDoubleOr("rotateY", 0);
        this.rotateZ = valueInput.getDoubleOr("rotateZ", 0);
        this.scale = valueInput.getDoubleOr("scale", 1);
    }

    @Override
    public void writeCompoundTag(ValueOutput valueOutput) {
        super.writeCompoundTag(valueOutput);
        valueOutput.putDouble("x1", this.x);
        valueOutput.putDouble("y1", this.y);
        valueOutput.putDouble("z1", this.z);
        valueOutput.putDouble("rotateX", this.rotateX);
        valueOutput.putDouble("rotateY", this.rotateY);
        valueOutput.putDouble("rotateZ", this.rotateZ);
        valueOutput.putDouble("scale", this.scale);
    }

    @Override
    public String getPIDSType() {
        return "pids_projector";
    }

    @Override
    public String getDefaultPresetId() {
        return "rv_pids";
    }

    @Override
    public int getRowAmount() {
        return 4;
    }

    public void setData(String[] customMessages, LongAVLTreeSet filteredPlatforms, boolean[] rowHidden, boolean hidePlatformNumber, String pidsPresetId, double x, double y, double z, double rotateX, double rotateY, double rotateZ, double scale) {
        super.setData(customMessages, filteredPlatforms, rowHidden, hidePlatformNumber, pidsPresetId);
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.setChanged();
        this.syncData();
    }

    public double getOffsetX() { return x; }

    public double getOffsetY() {
        return y;
    }

    public double getOffsetZ() {
        return z;
    }

    public double getScale() { return scale; }

    public double getRotateX() { return rotateX; }
    public double getRotateY() { return rotateY; }
    public double getRotateZ() { return rotateZ; }
}
