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

public class CustomTextureComponent extends TextureComponent {
    protected final Identifier texture;
    private final int color;

    public CustomTextureComponent(double x, double y, double width, double height, KVPair additionalParam) {
        super(x, y, width, height, additionalParam);
        this.color = additionalParam.getColor("tint", 0xFFFFFF);
        this.texture = Identifier.parse(additionalParam.get("textureId", ""));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, PIDSContext context) {
        drawTexture(poseStack, bufferSource, facing, texture, 0, 0, width, height, color + ARGB_BLACK);
    }

    public static PIDSComponent parseComponent(double x, double y, double width, double height, JsonObject jsonObject) {
        return new CustomTextureComponent(x, y, width, height, new KVPair(jsonObject));
    }
}
