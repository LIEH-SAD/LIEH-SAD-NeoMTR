package com.lx862.jcm.mod.network.block;

import com.lx862.jcm.mod.block.entity.PIDSProjectorBlockEntity;
import com.lx862.jcm.mod.util.BlockUtil;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PIDSProjectorUpdatePacket extends PIDSUpdatePacket {
    private final double x;
    private final double y;
    private final double z;
    private final double rotateX;
    private final double rotateY;
    private final double rotateZ;
    private final double scale;

    public PIDSProjectorUpdatePacket(FriendlyByteBuf packetBufferReceiver) {
        super(packetBufferReceiver);
        this.x = packetBufferReceiver.readDouble();
        this.y = packetBufferReceiver.readDouble();
        this.z = packetBufferReceiver.readDouble();
        this.rotateX = packetBufferReceiver.readDouble();
        this.rotateY = packetBufferReceiver.readDouble();
        this.rotateZ = packetBufferReceiver.readDouble();
        this.scale = packetBufferReceiver.readDouble();
    }

    public PIDSProjectorUpdatePacket(BlockPos blockPos, LongAVLTreeSet filteredPlatforms, String[] customMessages, boolean[] rowHidden, boolean hidePlatformNumber, String pidsPreset, double x, double y, double z, double rotateX, double rotateY, double rotateZ, double scale) {
        super(blockPos, filteredPlatforms, customMessages, rowHidden, hidePlatformNumber, pidsPreset);
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.scale = scale;
    }

    @Override
    public void write(FriendlyByteBuf packetBufferSender) {
        super.write(packetBufferSender);
        packetBufferSender.writeDouble(x);
        packetBufferSender.writeDouble(y);
        packetBufferSender.writeDouble(z);
        packetBufferSender.writeDouble(rotateX);
        packetBufferSender.writeDouble(rotateY);
        packetBufferSender.writeDouble(rotateZ);
        packetBufferSender.writeDouble(scale);
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayer) {
        Level world = serverPlayer.level();
        BlockEntity be = BlockUtil.getBlockEntityOrNull(world, blockPos);

        if(be != null && be instanceof PIDSProjectorBlockEntity) {
            ((PIDSProjectorBlockEntity)be).setData(customMessages, filteredPlatforms, rowHidden, hidePlatformNumber, presetId, x, y, z, rotateX, rotateY, rotateZ, scale);
        }
    }
}