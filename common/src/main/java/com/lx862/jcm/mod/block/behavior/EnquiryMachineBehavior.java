package com.lx862.jcm.mod.block.behavior;

import com.lx862.jcm.mod.data.TransactionEntry;
import com.lx862.jcm.mod.data.TransactionLog;
import com.lx862.jcm.mod.network.gui.EnquiryUpdateGUIPacket;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.render.gui.screen.ScreenType;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.data.TicketSystem;
import mtr.sound.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.List;

import static mtr.data.TicketSystem.BALANCE_OBJECTIVE;

public interface EnquiryMachineBehavior {
    default void enquiry(ScreenType type, BlockPos pos, Level world, ServerPlayer player) {
        world.playSound(null, player.blockPosition(), SoundEvents.TICKET_PROCESSOR_ENTRY, SoundSource.BLOCKS, 1, 1);

        if(type == null) {
            int score = TicketSystem.getPlayerScore(world, player, BALANCE_OBJECTIVE).get();
            player.sendOverlayMessage(TextUtil.translatable("gui.mtr.balance", String.valueOf(score)));
        } else {
            List<TransactionEntry> entries = TransactionLog.readLog(player, player.getStringUUID());
            Networking.sendPacketToClient(player, new EnquiryUpdateGUIPacket(type, pos, entries, TicketSystem.getPlayerScore(world, player, BALANCE_OBJECTIVE).get()));
        }
    }
}
