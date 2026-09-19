package com.lx862.jcm.mod.data.pids.preset.components;

import com.google.gson.JsonObject;
import com.lx862.jcm.mod.data.KVPair;
import com.lx862.jcm.mod.data.pids.preset.PIDSContext;
import com.lx862.jcm.mod.data.pids.preset.components.base.PIDSComponent;
import com.lx862.jcm.mod.data.pids.preset.components.base.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

public class StaticCustomMessageComponent extends TextComponent {
    private final String text;
    public StaticCustomMessageComponent(double x, double y, double width, double height, KVPair addtionalParam) {
        super(x, y, width, height, addtionalParam);
        this.text = addtionalParam.get("text", "");
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, PIDSContext context) {
        drawText(poseStack, bufferSource, parsePIDSVariable(text, context));
    }

    @Override
    public boolean canRender(PIDSContext context) {
        return !text.isEmpty();
    }

    public static PIDSComponent parseComponent(double x, double y, double width, double height, JsonObject jsonObject) {
        return new StaticCustomMessageComponent(x, y, width, height, new KVPair(jsonObject));
    }

    public static String parsePIDSVariable(String str, PIDSContext context) {
        long time = context.level.getOverworldClockTime() + 6000;
        long hours = time / 1000;
        long minutes = Math.round((time - (hours * 1000)) / 16.8);
        String timeString = String.format("%02d:%02d", hours % 24, minutes % 60);
        String weatherString = context.level.isRaining() ? "Raining" : context.level.isThundering() ? "Thundering" : "Sunny";
        String weatherChinString = context.level.isRaining() ? "下雨" : context.level.isThundering() ? "雷暴" : "晴天";
        int worldDay = (int) (context.level.getOverworldClockTime() / 24000L);
        String timeGreetings;

        if (time >= 6000 & time <= 12000) {
            timeGreetings = "Morning";
        } else if (time >= 12000 & time <= 18000) {
            timeGreetings = "Afternoon";
        } else {
            timeGreetings = "Night";
        }

        int playerInWorld = context.level.players().size();

        return str.replace("{time}", timeString)
                .replace("{day}", String.valueOf(worldDay))
                .replace("{weather}", weatherString)
                .replace("{time_period}", timeGreetings)
                .replace("{weatherChin}", weatherChinString)
                .replace("{worldPlayer}", String.valueOf(playerInWorld));
    }
}
