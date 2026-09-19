package com.lx862.jcm.mod.data.pids.preset.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lx862.jcm.mod.data.JCMServerStats;
import com.lx862.jcm.mod.data.KVPair;
import com.lx862.jcm.mod.data.pids.preset.PIDSContext;
import com.lx862.jcm.mod.data.pids.preset.components.base.PIDSComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CycleComponent extends PIDSComponent {
    private final List<PIDSComponent> components;
    private final double cycleTime;

    public CycleComponent(KVPair additionalParam, PIDSComponent... components) {
        super(0, 0, 0, 0);
        this.cycleTime = additionalParam.getInt("cycleTime", 20);
        this.components = new ArrayList<>();
        JsonArray array = additionalParam.get("components", new JsonArray());
        for(int i = 0; i < array.size(); i++) {
            this.components.add(PIDSComponent.parse(array.get(i).getAsJsonObject()));
        }
        this.components.addAll(Arrays.asList(components));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, PIDSContext context) {
        if(components.isEmpty()) return;
        List<PIDSComponent> filteredComponents = components.stream().filter(e -> e.canRender(context)).collect(Collectors.toList());

        if(cycleTime == -1 && !filteredComponents.isEmpty()) {
            filteredComponents.getFirst().render(poseStack, bufferSource, facing, context); // Render first available component
        } else {
            // TODO: Client tick!
            int currentComponentIndex = (int) ((int)(JCMServerStats.getGameTick() % (cycleTime * filteredComponents.size())) / cycleTime);

            PIDSComponent component = filteredComponents.get(currentComponentIndex);
            component.render(poseStack, bufferSource, facing, context);
        }
    }

    public static PIDSComponent parseComponent(double x, double y, double width, double height, JsonObject jsonObject) {
        return new CycleComponent(new KVPair(jsonObject));
    }
}