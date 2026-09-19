package com.lx862.jcm.mod.network.block;

import com.lx862.jcm.mod.block.base.JCMBlock;
import com.lx862.jcm.mod.block.entity.ButterflyLightBlockEntity;
import com.lx862.jcm.mod.network.PacketHandler;
import com.lx862.jcm.mod.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ButterflyLightUpdatePacket extends PacketHandler {
    private final BlockPos blockPos;
    private final int startBlinkingSeconds;

    public ButterflyLightUpdatePacket(FriendlyByteBuf packetBufferReceiver) {
        this.blockPos = BlockPos.of(packetBufferReceiver.readLong());
        this.startBlinkingSeconds = packetBufferReceiver.readInt();
    }

    public ButterflyLightUpdatePacket(BlockPos blockPos, int startBlinkingSeconds) {
        this.blockPos = blockPos;
        this.startBlinkingSeconds = startBlinkingSeconds;
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayer) {
        Level world = serverPlayer.level();
        BlockState state = BlockUtil.getBlockState(world, blockPos);
        if(state == null || !(state.getBlock() instanceof JCMBlock)) return;

        ((JCMBlock)state.getBlock()).loopStructure(state, world, blockPos, (bs, be) -> {
            if(be instanceof ButterflyLightBlockEntity) {
                ((ButterflyLightBlockEntity)be).setData(startBlinkingSeconds);
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf packetBufferSender) {
        packetBufferSender.writeLong(blockPos.asLong());
        packetBufferSender.writeInt(startBlinkingSeconds);
    }
}
