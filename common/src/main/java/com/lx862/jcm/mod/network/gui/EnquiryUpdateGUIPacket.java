package com.lx862.jcm.mod.network.gui;

import com.lx862.jcm.mod.data.TransactionEntry;
import com.lx862.jcm.mod.network.PacketHandler;
import com.lx862.jcm.mod.render.gui.screen.ScreenType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnquiryUpdateGUIPacket extends PacketHandler {

    private final ScreenType type;
    private final List<TransactionEntry> entries;
    private final BlockPos pos;
    private final int entryCount;
    private final int remainingBalance;

    public EnquiryUpdateGUIPacket(FriendlyByteBuf packetBufferReceiver) {
        this.entries = new ArrayList<>();
        this.type = ScreenType.valueOf(packetBufferReceiver.readUtf());
        this.pos = BlockPos.of(packetBufferReceiver.readLong());
        this.remainingBalance = packetBufferReceiver.readInt();

        this.entryCount = packetBufferReceiver.readInt();
        for (int i = 0; i < entryCount; i++) {
            String source = packetBufferReceiver.readUtf();
            long amount = packetBufferReceiver.readLong();
            long time = packetBufferReceiver.readLong();
            entries.add(new TransactionEntry(source, amount, time));
        }
    }

    public EnquiryUpdateGUIPacket(ScreenType type, BlockPos pos, List<TransactionEntry> entries, int remainingBalance) {
        this.type = type;
        this.entries = entries.stream().sorted((a, b) -> (int)(b.time - a.time)).collect(Collectors.toList());
        this.entryCount = entries.size();
        this.pos = pos;
        this.remainingBalance = remainingBalance;
    }

    @Override
    public void write(FriendlyByteBuf packetBufferSender) {
        packetBufferSender.writeUtf(type.toString());
        packetBufferSender.writeLong(pos.asLong());
        packetBufferSender.writeInt(remainingBalance);
        packetBufferSender.writeInt(entries.size());

        for (TransactionEntry transactionEntry : entries) {
            packetBufferSender.writeUtf(transactionEntry.source);
            packetBufferSender.writeLong(transactionEntry.amount);
            packetBufferSender.writeLong(transactionEntry.time);
        }
    }

    @Override
    public void runClient() {
        ScreenType.openEnquiryScreen(type, pos, entries, remainingBalance);
    }
}