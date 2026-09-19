package com.lx862.jcm.mod.data.pids.preset.components;

import com.google.gson.JsonObject;
import com.lx862.jcm.mod.data.JCMServerStats;
import com.lx862.jcm.mod.data.KVPair;
import com.lx862.jcm.mod.data.pids.preset.PIDSContext;
import com.lx862.jcm.mod.data.pids.preset.components.base.PIDSComponent;
import com.lx862.jcm.mod.data.pids.preset.components.base.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mtr.client.ClientData;
import mtr.data.MultipartName;
import mtr.data.Route;
import mtr.data.ScheduleEntry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

import java.util.List;

public class DestinationMessageComponent extends TextComponent {
    private final int arrivalIndex;
    private final String customMessageOverride;

    public DestinationMessageComponent(double x, double y, double width, double height, KVPair additionalParam) {
        super(x, y, width, height, additionalParam);
        this.arrivalIndex = additionalParam.getInt("arrivalIndex", 0);
        this.customMessageOverride = additionalParam.get("customMessageOverride", "");
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, PIDSContext context) {
        List<ScheduleEntry> arrivals = context.arrivals;
        if(arrivalIndex >= arrivals.size()) return;

        ScheduleEntry arrival = arrivals.get(arrivalIndex);
        final Route route = ClientData.DATA_CACHE.routeIdMap.get(arrival.routeId);
        if(route == null) return;

        final PIDSDisplay destinationString = getDisplayString(arrival);

        if(route.circularState == Route.CircularState.CLOCKWISE) {
//            destinationString = (isCjk(destinationString, false) ? TranslationProvider.GUI_MTR_CLOCKWISE_VIA_CJK : TranslationProvider.GUI_MTR_CLOCKWISE_VIA).getString(destinationString);
        } else if(route.circularState == Route.CircularState.ANTICLOCKWISE) {
//            destinationString = (isCjk(destinationString, false) ? TranslationProvider.GUI_MTR_ANTICLOCKWISE_VIA_CJK : TranslationProvider.GUI_MTR_ANTICLOCKWISE_VIA).getString(destinationString);
        }

        drawText(poseStack, bufferSource, destinationString.string());
    }

    public PIDSDisplay getDisplayString(ScheduleEntry arrival) {
        String customMessage = customMessageOverride == null ? "" : customMessageOverride;
        final Route route = ClientData.DATA_CACHE.routeIdMap.get(arrival.routeId);
        if(route == null) return null;

        final String destination = ClientData.DATA_CACHE.getFormattedRouteDestination(route, arrival.currentStationIndex, "", MultipartName.Usage.PIDS_DEST);

        final int languageTicks = (int) Math.floor(JCMServerStats.getGameTick()) / TextComponent.SWITCH_LANG_DURATION;
        final String[] destinationSplit;
        final String[] customMessageSplit = customMessage.split("\\|");
        final String[] tempDestinationSplit = destination.split("\\|");

        if (getRouteNumber(route).isEmpty()) {
            destinationSplit = tempDestinationSplit;
        } else {
            final String[] tempNumberSplit = getRouteNumber(route).split("\\|");
            int destinationIndex = 0;
            int numberIndex = 0;
            final ObjectArrayList<String> newDestinations = new ObjectArrayList<>();

            while (true) {
                final String newDestination = String.format("%s %s", tempNumberSplit[numberIndex % tempNumberSplit.length], tempDestinationSplit[destinationIndex % tempDestinationSplit.length]);
                if (newDestinations.contains(newDestination)) {
                    break;
                } else {
                    newDestinations.add(newDestination);
                }
                destinationIndex++;
                numberIndex++;
            }
            destinationSplit = newDestinations.toArray(new String[0]);
        }
        final int messageCount = destinationSplit.length + (customMessage.isEmpty() ? 0 : customMessageSplit.length);
        boolean renderCustomMessage = languageTicks % messageCount >= destinationSplit.length;
        int languageIndex = (languageTicks % messageCount) - (renderCustomMessage ? destinationSplit.length : 0);
        String strToDisplay = renderCustomMessage ? customMessageSplit[languageIndex] : destinationSplit[languageIndex];

        return new PIDSDisplay(strToDisplay, renderCustomMessage);
    }

    private static String getRouteNumber(Route route) {
        return route.isLightRailRoute ? route.lightRailRouteNumber : "";
    }

    public static PIDSComponent parseComponent(double x, double y, double width, double height, JsonObject jsonObject) {
        return new DestinationMessageComponent(x, y, width, height, new KVPair(jsonObject));
    }

    public record PIDSDisplay(String string, boolean isRenderingCustomMessage) {
    }
}
