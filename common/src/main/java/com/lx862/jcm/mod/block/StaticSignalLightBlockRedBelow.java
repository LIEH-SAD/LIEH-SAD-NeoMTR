package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.entity.StaticSignalLightBlockEntity;

public class StaticSignalLightBlockRedBelow extends StaticSignalLightBlock {
    public StaticSignalLightBlockRedBelow(Properties settings) {
        super(settings, 2, 14);
    }

    @Override
    public StaticSignalLightBlockEntity.SignalType getSignalType() {
        return StaticSignalLightBlockEntity.SignalType.RED_BELOW;
    }
}
