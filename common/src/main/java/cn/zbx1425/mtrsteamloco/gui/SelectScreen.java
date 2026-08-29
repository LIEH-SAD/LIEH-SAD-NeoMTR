package cn.zbx1425.mtrsteamloco.gui;

import cn.zbx1425.mtrsteamloco.data.EyeCandyRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SelectScreen extends SelectListScreen {

    private final Screen parent;
    private final Supplier<String> currentKey;
    private final BiConsumer<Object, Object> onSelect;

    public SelectScreen(Screen parent, Object tree, Supplier<String> currentKey, BiConsumer<Object, Object> onSelect) {
        super(Component.translatable("gui.mtrsteamloco.eye_candy.select_title"));
        this.parent = parent;
        this.currentKey = currentKey;
        this.onSelect = onSelect;
    }

    @Override
    protected void init() {
        super.init();
        loadPage();
    }

    @Override
    protected void loadPage() {
        clearWidgets();
        scrollList.visible = true;
        loadSelectPage(key -> !key.equals(currentKey.get()));
    }

    @Override
    protected void onBtnClick(String btnKey) {
        onSelect.accept(this, btnKey);
        //拒绝返回屏幕，方便玩家选择
        //Minecraft.getInstance().setScreen(parent);
    }

    @Override
    protected List<Pair<String, String>> getRegistryEntries() {
        return EyeCandyRegistry.elements.entrySet().stream()
                .filter(e -> e.getValue().name != null)
                .map(e -> new Pair<>(e.getKey(), e.getValue().name.getString()))
                .toList();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}