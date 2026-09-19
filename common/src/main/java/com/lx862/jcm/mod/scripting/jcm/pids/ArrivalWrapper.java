package com.lx862.jcm.mod.scripting.jcm.pids;

import mtr.client.ClientData;
import mtr.data.MultipartName;
import mtr.data.Platform;
import mtr.data.Route;
import mtr.data.ScheduleEntry;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class ArrivalWrapper {
    private final ScheduleEntry scheduleEntry;

    public ArrivalWrapper(ScheduleEntry scheduleEntry) {
        this.scheduleEntry = scheduleEntry;
    }

    public long arrivalTime() {
        return scheduleEntry.arrivalMillis;
    }

    public String destination() {
        return route().getDestination(scheduleEntry.currentStationIndex, MultipartName.Usage.PIDS_DEST);
    }

    public boolean arrived() {
        return arrivalTime() <= System.currentTimeMillis();
    }

    public Route route() {
        return ClientData.DATA_CACHE.routeIdMap.get(routeId());
    }

    public long routeId() {
        return scheduleEntry.routeId;
    }

    public String routeName() {
        return route().name;
    }

    public int routeColor() {
        return route().color;
    }

    public String routeNumber() {
        return route().lightRailRouteNumber;
    }

    public Route.CircularState circularState() {
        return route().circularState;
    }

    public Platform platform() {
        throw new NotImplementedException();
    }

    public long platformId() {
        throw new NotImplementedException();
    }

    public String platformName() {
        throw new NotImplementedException();
    }

    public int carCount() {
        return scheduleEntry.trainCars;
    }

    public List<?> cars() { // CarDetails
        throw new NotImplementedException();
    }
}
