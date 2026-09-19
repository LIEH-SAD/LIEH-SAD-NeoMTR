package com.lx862.jcm.mod.scripting.jcm.pids;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mtr.data.Platform;
import mtr.data.ScheduleEntry;

import java.util.List;
import java.util.function.Consumer;

public class ArrivalsWrapper {
    public final ObjectArrayList<ArrivalWrapper> arrivals;

    public ArrivalsWrapper(List<ScheduleEntry> arrivals) {
        this.arrivals = new ObjectArrayList<>();
        for(ScheduleEntry scheduleEntry : arrivals) {
            this.arrivals.add(new ArrivalWrapper(scheduleEntry));
        }
    }

    public ArrivalWrapper get(int i) {
        return i >= arrivals.size() ? null : arrivals.get(i);
    }

    public boolean mixedCarLength() {
        int car = -1;
        for(ArrivalWrapper arrivalResponse : arrivals) {
            if(car == -1) car = arrivalResponse.carCount();
            if(car != arrivalResponse.carCount()) return true;
        }
        return false;
    }

    public ObjectArrayList<Platform> platforms() {
        ObjectArrayList<Platform> platforms = new ObjectArrayList<>();
        for(ArrivalWrapper wrapper : arrivals) {
            Platform plat = wrapper.platform();
            if(plat != null) {
                platforms.add(plat);
            }
        }
        return platforms;
    }

    public void forEach(Consumer<ArrivalWrapper> consumer) {
        for(ArrivalWrapper arrivalResponse : arrivals) {
            consumer.accept(arrivalResponse);
        }
    }

    public void forEach(long platformId, Consumer<ArrivalWrapper> consumer) {
        for(ArrivalWrapper arrivalResponse : arrivals) {
            if(arrivalResponse.platformId() != platformId) continue;
            consumer.accept(arrivalResponse);
        }
    }
}
