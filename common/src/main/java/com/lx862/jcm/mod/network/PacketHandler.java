package com.lx862.jcm.mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class PacketHandler {
    public abstract void write(FriendlyByteBuf packetBufferSender);

    public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayerEntity) {
    }

    public void runClient() {
    }
}
