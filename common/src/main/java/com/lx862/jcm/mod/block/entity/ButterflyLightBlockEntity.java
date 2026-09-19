package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ButterflyLightBlockEntity extends JCMBlockEntity {
    private int startBlinkingSeconds = 10;
    public ButterflyLightBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.BUTTERFLY_LIGHT.get(), blockPos, blockState);
    }

    @Override
    public void readCompoundTag(ValueInput valueInput) {
        super.readCompoundTag(valueInput);
        this.startBlinkingSeconds = valueInput.getIntOr("seconds_to_blink", 10);
    }

    @Override
    public void writeCompoundTag(ValueOutput valueOutput) {
        super.writeCompoundTag(valueOutput);
        valueOutput.putInt("seconds_to_blink", startBlinkingSeconds);
    }

    public void setData(int secondsToBlink) {
        this.startBlinkingSeconds = secondsToBlink;
        this.setChanged();
        this.syncData();
    }

    public int getStartBlinkingSeconds() {
        return startBlinkingSeconds;
    }
}
