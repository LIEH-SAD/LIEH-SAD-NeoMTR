package com.lx862.jcm.mod.data.pids.preset;

import mtr.data.ScheduleEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class PIDSContext {
    public final Level level;
    public final String[] customMessages;
    public final List<ScheduleEntry> arrivals;
    public final BlockPos pos;
    public final double deltaTime;

    public PIDSContext(Level level, BlockPos pos, String[] customMessages, List<ScheduleEntry> arrivals, double deltaTime) {
        this.level = level;
        this.pos = pos;
        this.arrivals = arrivals;
        this.deltaTime = deltaTime;
        this.customMessages = customMessages;
    }
}
