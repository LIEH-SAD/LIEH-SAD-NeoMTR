package com.Nanbin.client.Screen;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import mtr.packet.PacketTrainDataGuiClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TicketMachineScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.title"));

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.general"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.amount"), 0)
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> {
                    int emeraldCost = (int) Math.ceil((double) newValue / 10);
                    PacketTrainDataGuiClient.addBalanceC2S(newValue, emeraldCost);
                })
                .build());

        builder.setSavingRunnable(() -> {
            // This is called when the "Save" button is clicked.
        });

        return builder.build();
    }
}