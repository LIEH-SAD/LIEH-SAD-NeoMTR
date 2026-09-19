package com.lx862.jcm.mod.registry;

import com.lx862.jcm.loader.JCMRegistry;
import com.lx862.jcm.loader.JCMRegistryClient;
import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.network.PacketHandler;
import com.lx862.jcm.mod.network.block.*;
import com.lx862.jcm.mod.network.gui.EnquiryUpdateGUIPacket;
import com.lx862.jcm.mod.util.JCMLogger;
import com.lx862.jcm.mod.util.JCMUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.function.Function;

public class Networking {
    private static final HashMap<String, Function<FriendlyByteBuf, ? extends PacketHandler>> packets = new HashMap<>();
    private static final Identifier PACKET_ID = Constants.id("packet");

    public static <T extends PacketHandler> void registerPacket(Class<T> clazz, Function<FriendlyByteBuf, T> packet) {
        packets.put(clazz.getName(), packet);
    }

    public static void setupPacketServer() {
        JCMRegistry.registerNetworkPacket(PACKET_ID);
        JCMRegistry.registerNetworkReceiver(PACKET_ID, (MinecraftServer server, ServerPlayer player, FriendlyByteBuf packet) -> {
            String className = packet.readUtf();
            JCMLogger.debug("Received C2S packet: " + className);
            Function<FriendlyByteBuf, ? extends PacketHandler> packetHandlerConstructor = packets.get(className);
            if(packetHandlerConstructor == null) {
                JCMLogger.warn("Unknown C2S packet: " + className);
                return;
            }

            PacketHandler packetHandler = packetHandlerConstructor.apply(packet);
            server.execute(() -> {
                packetHandler.runServer(server, player);
            });
        });
    }

    public static void setupPacketClient() {
        JCMRegistryClient.registerNetworkReceiver(PACKET_ID, packet -> {
            String className = packet.readUtf();
            JCMLogger.debug("Received S2C packet: " + className);
            Function<FriendlyByteBuf, ? extends PacketHandler> packetHandlerConstructor = packets.get(className);
            if(packetHandlerConstructor == null) {
                JCMLogger.warn("Unknown S2C packet: " + className);
                return;
            }

            PacketHandler packetHandler = packetHandlerConstructor.apply(packet);
            JCMUtil.executeOnClientThread(() -> packetHandler.runClient());
        });
    }

    public static void register() {
        JCMLogger.debug("Registering network packets...");
        registerPacket(ButterflyLightUpdatePacket.class, ButterflyLightUpdatePacket::new);
        registerPacket(FareSaverUpdatePacket.class, FareSaverUpdatePacket::new);
        registerPacket(PIDSUpdatePacket.class, PIDSUpdatePacket::new);
        registerPacket(PIDSProjectorUpdatePacket.class, PIDSProjectorUpdatePacket::new);
        registerPacket(SoundLooperUpdatePacket.class, SoundLooperUpdatePacket::new);
        registerPacket(SubsidyMachineUpdatePacket.class, SubsidyMachineUpdatePacket::new);
        registerPacket(EnquiryUpdateGUIPacket.class, EnquiryUpdateGUIPacket::new);
        setupPacketServer();
    }

    public static void registerClient() {
        setupPacketClient();
    }

    public static <T extends PacketHandler> void sendPacketToClient(ServerPlayer player, T packetHandler) {
        final String className = packetHandler.getClass().getName();
        FriendlyByteBuf newPacket = new FriendlyByteBuf(Unpooled.buffer());
        newPacket.writeUtf(className);
        packetHandler.write(newPacket);
        JCMRegistry.sendToPlayer(player, PACKET_ID, newPacket);
        JCMLogger.debug("Sent S2C packet: " + className);
    }

    public static <T extends PacketHandler> void sendPacketToServer(T packetHandler) {
        final String className = packetHandler.getClass().getName();
        FriendlyByteBuf newPacket = new FriendlyByteBuf(Unpooled.buffer());
        newPacket.writeUtf(className);
        packetHandler.write(newPacket);
        JCMRegistryClient.sendToServer(PACKET_ID, newPacket);
        JCMLogger.debug("Sent C2S packet: " + className);
    }
}

//package com.lx862.jcm.mod.registry;
//
//import com.lx862.jcm.mod.network.block.*;
//import com.lx862.jcm.mod.network.gui.*;
//import com.lx862.jcm.mod.util.JCMLogger;
//import mtr.mapping.holder.Player;
//import mtr.mapping.holder.ServerPlayer;
//import mtr.mapping.registry.PacketHandler;
//import mtr.mapping.tool.PacketBufferReceiver;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Function;
//
//public class Networking {
//    private static final List<String> registeredPackets = new ArrayList<>();
//    public static void register() {
//        JCMLogger.debug("Registering network packets...");
////        JCMRegistry.setupPacket();
//
//        // Block Entity Update
//        registerPacket(ButterflyLightUpdatePacket.class, ButterflyLightUpdatePacket::new);
//        registerPacket(FareSaverUpdatePacket.class, FareSaverUpdatePacket::new);
//        registerPacket(PIDSUpdatePacket.class, PIDSUpdatePacket::new);
//        registerPacket(PIDSProjectorUpdatePacket.class, PIDSProjectorUpdatePacket::new);
//        registerPacket(SoundLooperUpdatePacket.class, SoundLooperUpdatePacket::new);
//        registerPacket(SubsidyMachineUpdatePacket.class, SubsidyMachineUpdatePacket::new);
//        registerPacket(EnquiryUpdateGUIPacket.class, EnquiryUpdateGUIPacket::new);
//
//        // GUI Screen
//        registerPacket(ButterflyLightGUIPacket.class, ButterflyLightGUIPacket::new);
//        registerPacket(FareSaverGUIPacket.class, FareSaverGUIPacket::new);
//        registerPacket(PIDSGUIPacket.class, PIDSGUIPacket::new);
//        registerPacket(PIDSProjectorGUIPacket.class, PIDSProjectorGUIPacket::new);
//        registerPacket(SoundLooperGUIPacket.class, SoundLooperGUIPacket::new);
//        registerPacket(SubsidyMachineGUIPacket.class, SubsidyMachineGUIPacket::new);
//    }
//
//    public static void registerClient() {
//        JCMRegistryClient.setupPacketClient();
//    }
//
//    private static <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBufferReceiver, T> getInstance) {
//        // Keep track of the registered packets, so we can warn if we try to send a non-registered packet
//        // I believe Minecraft Mapping itself already keeps track of the registered packets, it just doesn't warn
////        registeredPackets.add(classObject.getName());
////        JCMRegistry.registerPacket(classObject, getInstance);
//    }
//
//    public static <T extends PacketHandler> void sendPacketToServer(T data) {
//        if(!registeredPackets.contains(data.getClass().getName())) {
//            JCMLogger.warn("Non-registered packets is sent: " + data.getClass().getName());
//            JCMLogger.warn("Consider registering in method: mod/registry/Networking.register()");
//        }
//        JCMRegistryClient.REGISTRY_CLIENT.sendPacketToServer(data);
//    }
//
//    public static <T extends PacketHandler> void sendPacketToClient(Player player, T data) {
//        if(!registeredPackets.contains(data.getClass().getName())) {
//            JCMLogger.warn("Non-registered packets is sent: " + data.getClass().getName());
//            JCMLogger.warn("Consider registering in method: mod/registry/Networking.register()");
//        }
//        JCMRegistry.REGISTRY.sendPacketToClient(ServerPlayer.cast(player), data);
//    }
//}
