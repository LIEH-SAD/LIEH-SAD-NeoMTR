package com.lx862.jcm.mod.render.text;

import com.lx862.jcm.mod.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class TextInfo {
    private final String content;
    private final WidthInfo widthInfo;
    private Identifier fontId;
    private TextAlignment textAlignment = TextAlignment.LEFT;
    private int textColor;
    private boolean forScrollingText = false;

    public TextInfo(String content) {
        this.content = content;
        this.widthInfo = new WidthInfo();
    }

    public TextInfo(MutableComponent text) {
        this.content = text.getString();
        this.widthInfo = new WidthInfo();
    }

    public String getContent() {
        return content;
    }

    public int getTextColor() {
        return textColor;
    }

    public WidthInfo getWidthInfo() {
        return widthInfo;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public boolean isForScrollingText() {
        return this.forScrollingText;
    }

    public TextInfo withScrollingText() {
        this.forScrollingText = true;
        return this;
    }

    public TextInfo withColor(ChatFormatting formatting) {
        if(formatting.getColor() != null) {
            this.textColor = formatting.getColor();
        }
        return this;
    }

    public TextInfo withColor(int color) {
        this.textColor = color;
        return this;
    }

    public TextInfo withTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public TextInfo withFixedWidth(int targetWidth) {
        this.widthInfo.setFixedWidth(targetWidth);
        return this;
    }

    public TextInfo withMaxWidth(float maxWidth) {
        this.widthInfo.setMaxWidth(maxWidth);
        return this;
    }

    public TextInfo withFont(String font) {
        return withFont(Identifier.parse(font));
    }

    public TextInfo withFont(Identifier fontId) {
        this.fontId = fontId;
        return this;
    }

    public MutableComponent toMutableComponent() {
        MutableComponent text = TextUtil.literal(content);
        if(fontId != null) {
            text = TextUtil.withFont(text, fontId);
        }
        return text;
    }

    public TextInfo copy(String newContent) {
        TextInfo ti = new TextInfo(newContent);
        ti.withColor(textColor);
        ti.withFont(fontId);
        return ti;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TextInfo)) return false;
        TextInfo other = ((TextInfo) o);
        return other.content.equals(content) && other.textColor == textColor && this.forScrollingText == other.forScrollingText && this.fontId.equals(other.fontId);
    }
}
