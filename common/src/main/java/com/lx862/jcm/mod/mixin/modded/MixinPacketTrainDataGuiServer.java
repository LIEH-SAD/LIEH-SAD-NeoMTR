package com.lx862.jcm.mod.mixin.modded;

import com.lx862.jcm.mod.data.TransactionEntry;
import com.lx862.jcm.mod.data.TransactionLog;
import mtr.packet.PacketTrainDataGuiServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketTrainDataGuiServer.class)
public class MixinPacketTrainDataGuiServer {
    @Inject(method = "lambda$receiveAddBalanceC2S$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/ScoreAccess;add(I)I", shift = At.Shift.AFTER))
    private static void recordEntryLog(ServerPlayer player, int emeralds, int addAmount, CallbackInfo ci) {
        TransactionLog.writeLog(player, new TransactionEntry("Add Value", addAmount, System.currentTimeMillis()));
    }
}
