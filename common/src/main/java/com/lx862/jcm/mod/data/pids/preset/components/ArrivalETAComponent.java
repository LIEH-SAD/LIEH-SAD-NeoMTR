package com.lx862.jcm.mod.data.pids.preset.components;

import com.google.gson.JsonObject;
import com.lx862.jcm.mod.data.KVPair;
import com.lx862.jcm.mod.data.pids.preset.PIDSContext;
import com.lx862.jcm.mod.data.pids.preset.components.base.PIDSComponent;
import com.lx862.jcm.mod.data.pids.preset.components.base.TextComponent;
import com.lx862.jcm.mod.render.text.TextInfo;
import com.lx862.jcm.mod.util.TextUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.client.ClientData;
import mtr.data.Platform;
import mtr.data.Route;
import mtr.data.ScheduleEntry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ArrivalETAComponent extends TextComponent {
    private final boolean useAbsoluteTime;
    private final boolean showDeparture;
    private final int arrivalIndex;
    public ArrivalETAComponent(double x, double y, double width, double height, KVPair additionalParam) {
        super(x, y, width, height, additionalParam);
        this.arrivalIndex = additionalParam.getInt("arrivalIndex", 0);
        this.showDeparture = additionalParam.get("showDeparture", false);
        this.useAbsoluteTime = additionalParam.get("useAbsTime", false);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, PIDSContext context) {
        List<ScheduleEntry> arrivals = context.arrivals;
        if(arrivalIndex >= arrivals.size()) return;

        ScheduleEntry arrival = arrivals.get(arrivalIndex);
        final Route route = ClientData.DATA_CACHE.routeIdMap.get(arrival.routeId);
        final Route.RoutePlatform routePlatform = route.platformIds.get(arrival.currentStationIndex);
        final Platform platform = ClientData.DATA_CACHE.platformIdMap.get(routePlatform.platformId);

        long arrDepTime = showDeparture ? arrival.arrivalMillis + (int)((platform.getDwellTime() / 2d) * 1000) : arrival.arrivalMillis;

        long remTime = arrDepTime - System.currentTimeMillis();
        int remSec = (int)(remTime / 1000L);
        if(remSec <= 0) return;

        final MutableComponent etaText;

        if(useAbsoluteTime) {
            etaText = TextUtil.literal(new SimpleDateFormat("HH:mm").format(new Date(arrival.arrivalMillis)));
        } else {
            if(remSec >= 60) {
                etaText = TextUtil.translatable(cycleStringTranslation("gui.mtr.arrival_min_cjk", "gui.mtr.arrival_min"), remSec / 60);
            } else {
                etaText = TextUtil.translatable(cycleStringTranslation("gui.mtr.arrival_sec_cjk", "gui.mtr.arrival_sec"), remSec % 60);
            }
        }

        drawText(poseStack, bufferSource, new TextInfo(etaText));
    }

    public static PIDSComponent parseComponent(double x, double y, double width, double height, JsonObject jsonObject) {
        return new ArrivalETAComponent(x, y, width, height, new KVPair(jsonObject));
    }
}
