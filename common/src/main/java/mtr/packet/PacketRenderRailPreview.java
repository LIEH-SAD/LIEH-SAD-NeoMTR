package mtr.packet;

import io.netty.buffer.Unpooled;
import mtr.Registry;
import mtr.client.ClientData;
import mtr.data.Rail;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PacketRenderRailPreview {

    public static void sendS2C(ServerPlayer player, List<Rail> rails) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeInt(rails.size());
        for (final Rail rail : rails) {
            rail.writePacket(packet);
        }
        Registry.sendToPlayer(player, IPacket.PACKET_RENDER_RAIL_PREVIEW, packet);
    }

    public static void receiveS2C(FriendlyByteBuf packet) {
        final List<Rail> rails = new ArrayList<>();
        final int count = packet.readInt();
        for (int i = 0; i < count; i++) {
            rails.add(new Rail(packet));
        }
        Minecraft.getInstance().execute(() -> ClientData.PREVIEW_RAILS = rails);
    }
}