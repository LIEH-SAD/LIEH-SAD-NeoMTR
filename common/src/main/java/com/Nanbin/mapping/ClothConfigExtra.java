package com.Nanbin.mapping;

import com.Nanbin.mapping.builders.ButtonEntryBuilder;
import com.Nanbin.mapping.entries.ButtonListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

/**
 * Extension of {@link me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl} with entries it
 * does not ship: a pure button entry ({@link ButtonListEntry}) without a label, a reset button
 * or a value, meant for actions such as opening another screen.
 *
 * <p>Cloth Config hands out its builders through the {@link ConfigEntryBuilder} interface, which
 * cannot be extended with new entry types, so the static helpers below take the entry builder as
 * an argument. Everything else follows the same builder pattern, so entries can be added the usual
 * way:
 *
 * <pre>{@code
 * ConfigEntryBuilder entryBuilder = builder.entryBuilder();
 * ConfigCategory category = builder.getOrCreateCategory(Text.translatable("config.general"));
 *
 * // Opens another screen and comes back to the config screen when it is closed.
 * category.addEntry(ClothConfigExtra.startScreenButton(entryBuilder,
 *         Text.translatable("config.open_another_screen"), parent -> new MyScreen(parent)).build());
 *
 * // Runs any action, here stretched over the whole row.
 * category.addEntry(ClothConfigExtra.startButton(entryBuilder, Text.translatable("config.reset")).setFullWidth()
 *         .setOnPress(() -> Config.reset()).build());
 * }</pre>
 */
public final class ClothConfigExtra {

    private ClothConfigExtra() {
    }

    /** Starts a builder for a pure button entry that shows {@code fieldName} on the button. */
    public static ButtonEntryBuilder startButton(ConfigEntryBuilder entryBuilder, Component fieldName) {
        return ButtonEntryBuilder.create(entryBuilder, fieldName);
    }

    /** Starts a builder for a pure button entry that runs {@code onPress} when clicked. */
    public static ButtonEntryBuilder startButton(ConfigEntryBuilder entryBuilder, Component fieldName, Runnable onPress) {
        return startButton(entryBuilder, fieldName).setOnPress(onPress);
    }

    /**
     * Starts a builder for a pure button entry that opens the screen created by {@code screenFactory}.
     * The factory receives the screen the button was pressed in, which is the config screen.
     */
    public static ButtonEntryBuilder startScreenButton(ConfigEntryBuilder entryBuilder, Component fieldName,
                                                       Function<Screen, Screen> screenFactory) {
        return startButton(entryBuilder, fieldName).openScreen(screenFactory);
    }

    /** Creates such an entry directly, for when no builder is at hand. */
    public static ButtonListEntry openScreenButton(Component fieldName, Function<Screen, Screen> screenFactory) {
        return new ButtonListEntry(fieldName, fieldName, button -> openScreen(screenFactory));
    }

    /**
     * Opens the screen created by {@code screenFactory}, using the currently opened screen as the
     * parent so that closing the new screen comes back to it.
     */
    public static void openScreen(Function<Screen, Screen> screenFactory) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(screenFactory.apply(minecraft.screen));
    }
}
