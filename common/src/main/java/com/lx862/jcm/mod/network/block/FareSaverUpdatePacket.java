package com.lx862.jcm.mod.network.block;

import com.lx862.jcm.mod.block.base.JCMBlock;
import com.lx862.jcm.mod.block.entity.FareSaverBlockEntity;
import com.lx862.jcm.mod.network.PacketHandler;
import com.lx862.jcm.mod.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FareSaverUpdatePacket extends PacketHandler {
    private final BlockPos blockPos;
    private final String prefix;
    private final int discount;

    public FareSaverUpdatePacket(FriendlyByteBuf packetBufferReceiver) {
        this.blockPos = BlockPos.of(packetBufferReceiver.readLong());
        this.prefix = packetBufferReceiver.readUtf();
        this.discount = packetBufferReceiver.readInt();
    }

    public FareSaverUpdatePacket(BlockPos blockPos, String prefix, int discount) {
        this.blockPos = blockPos;
        this.prefix = prefix;
        this.discount = discount;
    }

    @Override
    public void write(FriendlyByteBuf packetBufferSender) {
        packetBufferSender.writeLong(blockPos.asLong());
        packetBufferSender.writeUtf(prefix);
        packetBufferSender.writeInt(discount);
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayer) {
        Level world = serverPlayer.level();
        BlockState state = BlockUtil.getBlockState(world, blockPos);
        if(state == null || !(state.getBlock() instanceof JCMBlock)) return;

        ((JCMBlock)state.getBlock()).loopStructure(state, world, blockPos, (bs, be) -> {
            if(be instanceof FareSaverBlockEntity) {
                ((FareSaverBlockEntity)be).setData(prefix, discount);
            }
        });
    }
}
