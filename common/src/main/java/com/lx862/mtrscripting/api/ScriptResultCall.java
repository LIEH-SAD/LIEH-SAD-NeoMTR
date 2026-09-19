package com.lx862.mtrscripting.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public abstract class ScriptResultCall {
    public abstract void run(Level level, PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, int light);
}
