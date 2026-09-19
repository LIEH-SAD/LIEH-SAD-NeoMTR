package com.lx862.jcm.mod.data.pids.preset;

import com.lx862.jcm.mod.block.entity.PIDSBlockEntity;
import com.lx862.jcm.mod.render.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import mtr.data.ScheduleEntry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class PIDSPresetBase implements RenderHelper {
    private final String id;
    private final String name;
    private final List<String> blacklist;
    protected final Identifier thumbnail;
    public final boolean builtin;

    public PIDSPresetBase(String id, String name, Identifier thumbnail, List<String> blacklistedPIDS, boolean builtin) {
        this.id = id;
        if(name == null) {
            this.name = id;
        } else {
            this.name = name;
        }
        this.blacklist = blacklistedPIDS;
        this.builtin = builtin;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Identifier getThumbnail() {
        return thumbnail;
    }

    public boolean typeAllowed(String pidsType) {
        return !blacklist.contains(pidsType);
    }

    public abstract int getTextColor();
    public abstract boolean isRowHidden(int row);

    public abstract void render(PIDSBlockEntity be, PoseStack poseStack, MultiBufferSource bufferSource, Level world, BlockPos pos, Direction facing, List<ScheduleEntry> arrivals, boolean[] rowHidden, float tickDelta, int x, int y, int width, int height);
}