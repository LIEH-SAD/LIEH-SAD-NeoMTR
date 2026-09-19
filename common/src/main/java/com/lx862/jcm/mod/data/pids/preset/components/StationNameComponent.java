package com.lx862.jcm.mod.data.pids.preset.components;

import com.google.gson.JsonObject;
import com.lx862.jcm.mod.data.KVPair;
import com.lx862.jcm.mod.data.pids.preset.PIDSContext;
import com.lx862.jcm.mod.data.pids.preset.components.base.PIDSComponent;
import com.lx862.jcm.mod.data.pids.preset.components.base.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.client.ClientData;
import mtr.data.IGui;
import mtr.data.RailwayData;
import mtr.data.Station;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

public class StationNameComponent extends TextComponent {
    public StationNameComponent(double x, double y, double width, double height, KVPair additionalParam) {
        super(x, y, width, height, additionalParam);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, PIDSContext context) {
        if(context.pos == null) {
            drawText(poseStack, bufferSource, "車站|Station");
        } else {
            final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, context.pos);
            if(station == null) {
                drawText(poseStack, bufferSource, IGui.textOrUntitled(""));
            } else {
                drawText(poseStack, bufferSource, IGui.textOrUntitled(station.name));
            }
        }
    }

    public static PIDSComponent parseComponent(double x, double y, double width, double height, JsonObject jsonObject) {
        return new StationNameComponent(x, y, width, height, new KVPair(jsonObject));
    }
}
