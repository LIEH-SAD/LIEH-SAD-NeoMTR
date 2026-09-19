package com.lx862.jcm.mod.util;

import com.lx862.jcm.mod.Constants;
import mtr.client.Config;
import mtr.data.IGui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class TextUtil {
    public static MutableComponent literal(String content) {
        return Component.literal(content);
    }

    public static MutableComponent translatable(TextCategory textCategory, String id, Object... variables) {
        return Component.translatable(textCategory.prefix + "." + Constants.MOD_ID + "." + id, variables);
    }

    public static MutableComponent translatable(String id, Object... variables) {
        return Component.translatable(id, variables);
    }

    public static Style withFontStyle(Identifier fontId) {
        return Config.useMTRFont() ? Style.EMPTY.withFont(new FontDescription.Resource(fontId)) : Style.EMPTY;
    }

    /** Set a custom font style to MutableComponent, this respects the client config. */
    public static MutableComponent withFont(MutableComponent text, Identifier fontId) {
        return text.setStyle(withFontStyle(fontId));
    }

    public static boolean haveNonCjk(String str) {
        for(String dest : str.split("\\|")) {
            if(!IGui.isCjk(dest)) {
                return true;
            }
        }
        return false;
    }
}