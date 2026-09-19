package com.lx862.jcm.mod.data.pids.preset.components;

import com.lx862.jcm.mod.data.KVPair;
import com.lx862.jcm.mod.data.pids.preset.PIDSContext;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.client.ClientData;
import mtr.data.Route;
import mtr.data.ScheduleEntry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

import java.util.List;

/**
 * A CustomTextureComponent that automatically tints the color to the route's color of the arriving entry.
 * Will not render if there's no arrival for the speified arrivalIndex.
 */
public class ArrivalTextureComponent extends CustomTextureComponent {
    private final int arrivalIndex;
    public ArrivalTextureComponent(double x, double y, double width, double height, KVPair additionalParam) {
        super(x, y, width, height, additionalParam);
        this.arrivalIndex = additionalParam.getInt("arrivalIndex", 0);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, PIDSContext context) {
        final List<ScheduleEntry> arrivals = context.arrivals;
        if(arrivalIndex >= arrivals.size()) return;

        final ScheduleEntry arrival = arrivals.get(arrivalIndex);
        final Route route = ClientData.DATA_CACHE.routeIdMap.get(arrival.routeId);
        final int routeColor;

        if(route == null) {
            routeColor = 0;
        } else {
            routeColor = route.color;
        }
        drawTexture(poseStack, bufferSource, facing, texture, 0, 0, width, height, routeColor + ARGB_BLACK);
    }
}
