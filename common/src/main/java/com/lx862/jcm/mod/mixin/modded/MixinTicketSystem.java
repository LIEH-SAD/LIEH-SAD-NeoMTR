package com.lx862.jcm.mod.mixin.modded;

import com.lx862.jcm.mod.block.FareSaverBlock;
import com.lx862.jcm.mod.data.TransactionEntry;
import com.lx862.jcm.mod.data.TransactionLog;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import mtr.data.Station;
import mtr.data.TicketSystem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.ScoreAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TicketSystem.class, remap = false)
public class MixinTicketSystem {
    @Unique
    private static final int BASE_FARE = 2;
    @Unique
    private static final int ZONE_FARE = 1;

    @Inject(method = "onExit", at = @At("HEAD"))
    private static void onExit(Station station, Player player, ScoreAccess balanceScore, ScoreAccess entryZoneScore, boolean remindIfNoRecord, CallbackInfoReturnable<Boolean> cir) {
        final int entryZone = entryZoneScore.get();
        final boolean evasion = entryZone == 0;

        final int fare = BASE_FARE + ZONE_FARE * Math.abs(station.zone - decodeZone(entryZone));
        int finalFare = isConcessionary(player) ? (int) Math.ceil(fare / 2F) : fare;

        if(!evasion && FareSaverBlock.discountList.containsKey(player.getUUID())) {
            long subsidizedAmount = Math.min(finalFare, FareSaverBlock.discountList.get(player.getUUID()));
            TicketSystem.getPlayerScore(player.level(), player, "mtr_balance").add((int)subsidizedAmount);
            finalFare += subsidizedAmount;

            if (subsidizedAmount <= 0) {
                player.sendSystemMessage(TextUtil.translatable(TextCategory.HUD, "faresaver.saved_sarcasm", -subsidizedAmount));
            } else {
                player.sendSystemMessage(TextUtil.translatable(TextCategory.HUD, "faresaver.saved", subsidizedAmount));
            }

            FareSaverBlock.discountList.remove(player.getUUID());
        }

        TransactionLog.writeLog(player, new TransactionEntry(station.name, -finalFare, System.currentTimeMillis()));
    }

    private static int decodeZone(int zone) {
        return zone > 0 ? zone - 1 : zone;
    }

    private static boolean isConcessionary(Player player) {
        return player.isCreative();
    }
}