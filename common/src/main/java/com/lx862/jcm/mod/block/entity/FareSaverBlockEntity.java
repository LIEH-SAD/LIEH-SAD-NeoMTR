package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FareSaverBlockEntity extends JCMBlockEntity {
    private String prefix = "$";
    private int discount = 2;
    public FareSaverBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.FARE_SAVER.get(), blockPos, blockState);
    }

    @Override
    public void readCompoundTag(ValueInput valueInput) {
        super.readCompoundTag(valueInput);
        this.discount = valueInput.getIntOr("discount", 2);
        this.prefix = valueInput.getStringOr("currency", "$");
    }

    @Override
    public void writeCompoundTag(ValueOutput valueOutput) {
        super.writeCompoundTag(valueOutput);
        valueOutput.putInt("discount", discount);
        valueOutput.putString("currency", prefix);
    }

    public void setData(String prefix, int discount) {
        this.prefix = prefix;
        this.discount = discount;
        this.setChanged();
        this.syncData();
    }

    public int getDiscount() {
        return discount;
    }

    public String getPrefix() {
        return prefix;
    }
}
