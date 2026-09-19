package com.lx862.jcm.mod.data.pids.preset.components;

import com.google.gson.JsonObject;
import com.lx862.jcm.mod.data.KVPair;
import com.lx862.jcm.mod.data.pids.preset.PIDSContext;
import com.lx862.jcm.mod.data.pids.preset.components.base.PIDSComponent;
import com.lx862.jcm.mod.data.pids.preset.components.base.TextureComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class WeatherIconComponent extends TextureComponent {
    private final Identifier iconSunny;
    private final Identifier iconThunder;
    private final Identifier iconRainy;
    public WeatherIconComponent(double x, double y, double width, double height, KVPair additionalParam) {
        super(x, y, width, height, additionalParam);
        this.iconSunny = additionalParam.getIdentifier("weatherIconSunny");
        this.iconRainy = additionalParam.getIdentifier("weatherIconRainy");
        this.iconThunder = additionalParam.getIdentifier("weatherIconThunder");
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, PIDSContext context) {
        Identifier textureId;
        if(context.level.isThundering()) {
            textureId = iconThunder;
        } else if(context.level.isRaining()) {
            textureId = iconRainy;
        } else {
            textureId = iconSunny;
        }

        if(textureId != null) {
            drawTexture(poseStack, bufferSource, facing, textureId, 0, 0, width, height, ARGB_WHITE);
        }
    }

    public static PIDSComponent parseComponent(double x, double y, double width, double height, JsonObject jsonObject) {
        return new WeatherIconComponent(x, y, width, height, new KVPair(jsonObject));
    }
}
