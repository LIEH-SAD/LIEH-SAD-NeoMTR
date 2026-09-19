package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SubsidyMachineBlockEntity extends JCMBlockEntity {
    private int subsidyAmount = 10;
    private int cooldown = 0;
    public SubsidyMachineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.SUBSIDY_MACHINE.get(), blockPos, blockState);
    }

    @Override
    public void readCompoundTag(ValueInput valueInput) {
        super.readCompoundTag(valueInput);
        this.subsidyAmount = valueInput.getIntOr("price_per_click", 10);
        this.cooldown = valueInput.getIntOr("timeout", 0);
    }

    @Override
    public void writeCompoundTag(ValueOutput valueOutput) {
        super.writeCompoundTag(valueOutput);
        valueOutput.putInt("price_per_click", subsidyAmount);
        valueOutput.putInt("timeout", cooldown);
    }

    public void setData(int pricePerUse, int cooldown) {
        this.subsidyAmount = pricePerUse;
        this.cooldown = cooldown;
        this.setChanged();
        this.syncData();
    }

    public int getSubsidyAmount() {
        return subsidyAmount;
    }

    public int getCooldown() {
        return cooldown;
    }
}
