package com.lx862.jcm.mod.scripting.jcm.pids;

import cn.zbx1425.sowcer.math.Matrices;
import com.lx862.mtrscripting.api.ScriptResultCall;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public abstract class PIDSDrawCall extends ScriptResultCall {
    public Matrices matrices;
    public double x;
    public double y;
    public double w;
    public double h;

    public PIDSDrawCall(double defaultW, double defaultH) {
        this.w = defaultW;
        this.h = defaultH;
        this.matrices = new Matrices();
    }

    public PIDSDrawCall pos(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public PIDSDrawCall size(double w, double h) {
        this.w = w;
        this.h = h;
        return this;
    }

    public PIDSDrawCall matrices(Matrices matrices) {
        this.matrices = matrices;
        return this;
    }

    public void draw(PIDSScriptContext ctx) {
        ctx.draw(this);
    }

    @Override
    public void run(Level world, PoseStack poseStack, MultiBufferSource bufferSource, Direction facing, int light) {
        poseStack.pushPose();

//        storedMatrixTransformations.transform(graphicsHolder, Vector3d.getZeroMapped());
        poseStack.translate(this.x, this.y, 0);
        drawTransformed(poseStack, bufferSource, facing);
        poseStack.popPose();
    }

    protected abstract void validate();

    protected abstract void drawTransformed(PoseStack poseStack, MultiBufferSource bufferSource, Direction facing);
}
