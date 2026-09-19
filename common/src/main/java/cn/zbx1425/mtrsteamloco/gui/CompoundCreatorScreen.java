package cn.zbx1425.mtrsteamloco.gui;

import cn.zbx1425.mtrsteamloco.Main;
import cn.zbx1425.mtrsteamloco.data.RailExtraSupplier;
import cn.zbx1425.mtrsteamloco.data.RailModelProperties;
import cn.zbx1425.mtrsteamloco.data.RailModelRegistry;
import cn.zbx1425.mtrsteamloco.data.RailModelRepeater;
import cn.zbx1425.mtrsteamloco.data.RepeaterAttachment;
import cn.zbx1425.mtrsteamloco.gui.entries.ButtonCycleListEntry;
import cn.zbx1425.mtrsteamloco.item.CompoundCreator.Lump;
import cn.zbx1425.mtrsteamloco.item.CompoundCreator.RailModifierTask;
import cn.zbx1425.mtrsteamloco.item.CompoundCreator.SliceTask;
import cn.zbx1425.mtrsteamloco.item.CompoundCreator.Task;
import cn.zbx1425.mtrsteamloco.network.PacketUpdateHoldingItem;
import com.Nanbin.client.Screen.RailwaySignClearConfirmScreen;
import com.mojang.datafixers.util.Pair;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import mtr.client.IDrawing;
import mtr.data.Rail;
import mtr.data.RailType;
import mtr.mappings.Text;
import mtr.screen.WidgetBetterTextField;
import mtr.util.UtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Editor for the tasks stored on the Compound Creator item. Ported from ANTE's
 * {@code CompoundCreatorScreen}, adapted to this fork's GUI framework.
 */
public class CompoundCreatorScreen extends Screen {

    public static Screen createScreen(Screen parent) {
        final CompoundCreatorScreen screen = new CompoundCreatorScreen(parent);
        if (screen.load()) return screen;
        return parent;
    }

    private static final String TAG_TASKS = "tasks";

    private static final Identifier BLUE_CIRCLE = Identifier.fromNamespaceAndPath(Main.MOD_ID, "textures/gui/compound_creator/blue_circle.png");
    private static final Identifier PURPLE_CIRCLE = Identifier.fromNamespaceAndPath(Main.MOD_ID, "textures/gui/compound_creator/purple_circle.png");
    private static final Identifier MID_CIRCLE = Identifier.fromNamespaceAndPath(Main.MOD_ID, "textures/gui/compound_creator/mid_circle.png");
    private static final int ICON_SIZE = 16;

    private final Screen parent;
    private List<Entry> entries = new ArrayList<>();
    private Entry selectedEntry = null;
    private int scroll = 0;
    private int scissorX = 0, scissorY = 40, scissorW = 0, scissorH = 0;
    private boolean draggingSlider = false;

    private final Button btnAdd = UtilitiesClient.newButton(20, Text.literal("+"), button -> minecraft.setScreen(new TaskSelectScreen()));
    private final Button btnRemove = UtilitiesClient.newButton(20, Text.literal("-"), button -> removeEntry());
    private final Button btnCopy = UtilitiesClient.newButton(20, Text.translatable("gui.mtrsteamloco.compound_creator.copy"), button -> copyEntry());
    private final Button btnClear = UtilitiesClient.newButton(20, Text.translatable("gui.mtrsteamloco.compound_creator.clear"), button -> minecraft.setScreen(RailwaySignClearConfirmScreen.create(this, this::clearEntries)));
    private final Button btnClose = UtilitiesClient.newButton(20, Text.literal("X"), button -> onClose());

    CompoundCreatorScreen(Screen parent) {
        super(Text.translatable("gui.mtrsteamloco.compound_creator.title"));
        this.parent = parent;
    }

    public boolean load() {
        final CompoundTag tag = getTag();
        if (tag == null) return false;
        if (!tag.contains(TAG_TASKS)) return true;
        final List<Task> tasks = new ArrayList<>();
        final CompoundTag tasksTag = tag.getCompoundOrEmpty(TAG_TASKS).copy();
        for (String key : tasksTag.keySet()) {
            final CompoundTag task = tasksTag.getCompoundOrEmpty(key);
            final String type = task.getStringOr(Task.TAG_TYPE, "");
            if (type.equals(SliceTask.TYPE)) {
                tasks.add(new SliceTask(task));
            } else if (type.equals(RailModifierTask.TYPE)) {
                tasks.add(new RailModifierTask(task));
            } else {
                Main.LOGGER.error("Unknown task type: " + type);
            }
        }
        tasks.sort(Comparator.comparingInt(task -> task.order));
        final List<Entry> loaded = new ArrayList<>();
        for (Task task : tasks) {
            loaded.add(new Entry(task));
        }
        entries = loaded;
        selectedEntry = loaded.isEmpty() ? null : loaded.get(0);
        return true;
    }

    public static CompoundTag getTag() {
        if (Minecraft.getInstance().player == null) return null;
        final ItemStack item = Minecraft.getInstance().player.getMainHandItem();
        if (!item.is(Main.COMPOUND_CREATOR.get())) return null;
        return item.getOrDefault(Main.TOOL_TAG.get(), new CompoundTag());
    }

    public static void updateTag(Consumer<CompoundTag> modifier) {
        if (Minecraft.getInstance().player == null) return;
        final ItemStack item = Minecraft.getInstance().player.getMainHandItem();
        if (!item.is(Main.COMPOUND_CREATOR.get())) return;
        final CompoundTag tag = item.getOrDefault(Main.TOOL_TAG.get(), new CompoundTag()).copy();
        modifier.accept(tag);
        item.set(Main.TOOL_TAG.get(), tag);
        PacketUpdateHoldingItem.sendUpdateC2S();
    }

    @Override
    protected void init() {
        scissorX = 0;
        scissorY = 40;
        scissorW = width;
        scissorH = Math.max(0, height - 80);

        IDrawing.setPositionAndWidth(btnAdd, width - 50, 60, 40);
        IDrawing.setPositionAndWidth(btnRemove, width - 50, 90, 40);
        IDrawing.setPositionAndWidth(btnCopy, width - 50, 120, 40);
        IDrawing.setPositionAndWidth(btnClear, width - 50, 150, 40);
        IDrawing.setPositionAndWidth(btnClose, 10, 10, 20);

        addRenderableWidget(btnAdd);
        addRenderableWidget(btnRemove);
        addRenderableWidget(btnCopy);
        addRenderableWidget(btnClear);
        addRenderableWidget(btnClose);
    }

    private void update() {
        updateTag(tag -> {
            final List<Entry> copy = new ArrayList<>(entries);
            final CompoundTag tasksTag = new CompoundTag();
            for (int i = 0; i < copy.size(); i++) {
                copy.get(i).task.order = i;
                tasksTag.put(i + "", copy.get(i).task.toCompoundTag());
            }
            tag.put(TAG_TASKS, tasksTag);
        });
    }

    public void clearEntries() {
        entries.clear();
        selectedEntry = null;
        update();
    }

    public void addEntry(Task task) {
        final Entry entry = new Entry(task);
        entries.add(entry);
        selectedEntry = entry;
        update();
    }

    private void removeEntry() {
        if (selectedEntry == null) return;
        entries.remove(selectedEntry);
        selectedEntry = entries.isEmpty() ? null : entries.get(0);
        update();
    }

    private void copyEntry() {
        if (selectedEntry == null) return;
        final Entry entry;
        if (selectedEntry.task instanceof SliceTask sliceTask) {
            entry = new Entry(new SliceTask(sliceTask));
        } else if (selectedEntry.task instanceof RailModifierTask railModifierTask) {
            entry = new Entry(new RailModifierTask(railModifierTask));
        } else {
            Main.LOGGER.error("Unknown task type: " + selectedEntry.task.getClass());
            return;
        }
        entries.add(entry);
        selectedEntry = entry;
        update();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        // Drawn in the background layer so the buttons rendered afterwards stay on top.
        guiGraphics.fill(0, 38, width, height - 38, 0x90000000);
        guiGraphics.centeredText(font, title.getString(), width / 2, 18, 0xFFFFFFFF);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.enableScissor(scissorX, scissorY, scissorX + scissorW, scissorY + scissorH);
        checkAndScroll(scroll);
        int y = scissorY + scroll;
        for (Entry entry : entries) {
            entry.render(guiGraphics, mouseX, mouseY, y, partialTick);
            y += Entry.height();
        }
        guiGraphics.disableScissor();

        if (canScroll()) {
            final int[] pas = getSliderPositionAndSize();
            guiGraphics.fill(pas[0], pas[1], pas[0] + pas[2], pas[1] + pas[3], 0xb0b0b0b0);
        }
    }

    private void setScroll(int mouseY) {
        final int[] pas = getSliderPositionAndSize();
        final int sh = pas[3];
        final int maxd = Math.max(1, scissorH - sh);
        final int dy = mouseY - sh / 2 - scissorY;
        final int maxScroll = -entries.size() * Entry.height() + scissorH;
        checkAndScroll((int) (dy / ((float) maxd) * maxScroll));
    }

    private void checkAndScroll(int temp) {
        if (!canScroll()) {
            scroll = 0;
            return;
        }
        if (temp > 0) temp = 0;
        final int min = -entries.size() * Entry.height() + scissorH;
        if (temp < min) temp = min;
        scroll = temp;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double amount) {
        if (!canScroll()) return super.mouseScrolled(x, y, scrollX, amount);
        if (scissorX <= x && x <= scissorX + scissorW && scissorY <= y && y <= scissorY + scissorH) {
            checkAndScroll(scroll + 10 * (int) amount);
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, amount);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (canScroll() && isMouseOverSlider(event.x(), event.y())) {
            setScroll((int) event.y());
            return true;
        }
        // The container only ever dispatches to the topmost child, so rows are selected here
        // before delegating to the row widgets (name field, enter/up/down buttons).
        for (Entry entry : entries) {
            if (entry.isMouseOver(event.x(), event.y())) {
                selectedEntry = entry;
                break;
            }
        }
        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (isMouseOverSlider(event.x(), event.y()) || draggingSlider) {
            setScroll((int) (event.y() + dragY));
            draggingSlider = true;
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggingSlider) {
            draggingSlider = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    private boolean isMouseOverSlider(double mouseX, double mouseY) {
        if (!canScroll()) return false;
        final int[] pas = getSliderPositionAndSize();
        return pas[0] <= mouseX && mouseX <= pas[0] + pas[2] && pas[1] <= mouseY && mouseY <= pas[1] + pas[3];
    }

    private int[] getSliderPositionAndSize() {
        final float ah = (float) entries.size() * Entry.height();
        final float th = (float) scissorH;
        final int h = ah <= 0 ? scissorH : (int) (th / ah * th);
        final int py = scissorY + (int) (-1F * scroll / Math.max(1F, ah) * th);
        return new int[]{Entry.x() + Entry.width(width), py, Math.max(2, (int) (width * 0.01F)), h};
    }

    private boolean canScroll() {
        return entries.size() * Entry.height() > scissorH;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        final List<GuiEventListener> result = new ArrayList<>(super.children());
        for (Entry entry : entries) {
            result.addAll(entry.widgets());
        }
        return result;
    }

    @Override
    public void onClose() {
        update();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public class Entry implements GuiEventListener {

        public Task task;
        private int y;
        private static final int X = 20;
        private static final int HEIGHT = 24;

        private final WidgetBetterTextField nameField = new WidgetBetterTextField("", 256);
        private final Button enter = UtilitiesClient.newButton(20, Text.literal("Enter"), btn -> enter());
        private final Button up = UtilitiesClient.newButton(20, Text.literal("▲"), btn -> moveUp());
        private final Button down = UtilitiesClient.newButton(20, Text.literal("▼"), btn -> moveDown());
        private final List<GuiEventListener> children = Arrays.asList(nameField, enter, up, down);

        private boolean isFocused = false;

        public Entry(Task task) {
            this.task = task;
            nameField.setResponder(this::updateName);
            nameField.setValue(task.name);
            nameField.moveCursorToStart(false);
        }

        public List<? extends GuiEventListener> widgets() {
            return children;
        }

        public void enter() {
            if (task instanceof SliceTask sliceTask) {
                minecraft.setScreen(new SliceTaskScreen(sliceTask));
            } else if (task instanceof RailModifierTask railModifierTask) {
                setRailModifierScreen(railModifierTask);
            } else {
                Main.LOGGER.error("Unknown task type: " + task.getClass().getName());
            }
        }

        public void updateName(String name) {
            task.name = name;
            update();
            selectedEntry = this;
        }

        public void moveUp() {
            final int index = entries.indexOf(this);
            if (index > 0) {
                entries.set(index, entries.get(index - 1));
                entries.set(index - 1, this);
                update();
            }
            selectedEntry = this;
        }

        public void moveDown() {
            final int index = entries.indexOf(this);
            if (index >= 0 && index < entries.size() - 1) {
                entries.set(index, entries.get(index + 1));
                entries.set(index + 1, this);
                update();
            }
            selectedEntry = this;
        }

        public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int y, float partialTick) {
            this.y = y;
            if (isMouseOver(mouseX, mouseY)) {
                guiGraphics.fill(0, y, width - 55, y + HEIGHT, 0x40eeeeee);
            }
            final int rowY = y + 2;
            final int count = rowWidth() / 20;
            int x = X;
            IDrawing.setPositionAndWidth(nameField, x, rowY, count * 7);
            x += count * 8;
            IDrawing.setPositionAndWidth(enter, x, rowY, count * 6);
            x += count * 7;
            IDrawing.setPositionAndWidth(up, x, rowY, count * 2);
            x += count * 3;
            IDrawing.setPositionAndWidth(down, x, rowY, count * 2);
            nameField.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            enter.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            up.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            down.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return X <= mouseX && mouseX <= X + rowWidth() && y <= mouseY && mouseY <= y + HEIGHT;
        }

        public static int x() {
            return X;
        }

        public int y() {
            return y;
        }

        public static int width(int screenWidth) {
            return screenWidth - 80;
        }

        private int rowWidth() {
            return width - 80;
        }

        public static int height() {
            return HEIGHT;
        }

        public boolean isSelected() {
            return selectedEntry == this;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
            if (isMouseOver(event.x(), event.y())) {
                selectedEntry = this;
                return true;
            }
            return false;
        }

        @Override
        public boolean isFocused() {
            return isFocused;
        }

        @Override
        public void setFocused(boolean focused) {
            isFocused = focused;
        }
    }

    public class TaskSelectScreen extends Screen {

        private final Button btnReturn = UtilitiesClient.newButton(20, Text.literal("X"), btn -> onClose());
        private final Button btnNewRailModifier = UtilitiesClient.newButton(20, Text.translatable("gui.mtrsteamloco.compound_creator.rail_modifier"), btn -> {
            addEntry(new RailModifierTask());
            minecraft.setScreen(CompoundCreatorScreen.this);
        });
        private final Button btnNewSliceTask = UtilitiesClient.newButton(20, Text.translatable("gui.mtrsteamloco.compound_creator.slice_task"), btn -> {
            addEntry(new SliceTask());
            minecraft.setScreen(CompoundCreatorScreen.this);
        });

        public TaskSelectScreen() {
            super(Text.literal("Select Task"));
        }

        @Override
        protected void init() {
            IDrawing.setPositionAndWidth(btnReturn, 10, 10, 20);
            IDrawing.setPositionAndWidth(btnNewRailModifier, width / 2 - 150, 50, 300);
            IDrawing.setPositionAndWidth(btnNewSliceTask, width / 2 - 150, 80, 300);
            addRenderableWidget(btnReturn);
            addRenderableWidget(btnNewRailModifier);
            addRenderableWidget(btnNewSliceTask);
        }

        @Override
        public void onClose() {
            minecraft.setScreen(CompoundCreatorScreen.this);
        }
    }

    private static String getRailModelKey(Rail rail) {
        final List<RailModelRepeater> repeaters = ((RailExtraSupplier) (Object) rail).getRepeaters();
        if (repeaters.isEmpty()) return "";
        return repeaters.get(0).getPrimaryModelTypeKey();
    }

    private static void setRailModelKey(Rail rail, String key) {
        final List<RailModelRepeater> repeaters = ((RailExtraSupplier) (Object) rail).getRepeaters();
        if (repeaters.isEmpty()) repeaters.add(new RailModelRepeater());
        final RailModelRepeater repeater = repeaters.get(0);
        if (repeater.attachments.isEmpty()) repeater.attachments.add(new RepeaterAttachment());
        repeater.attachments.get(0).modelTypeKey = key;
    }

    public void setRailModifierScreen(RailModifierTask task) {
        minecraft.setScreen(newRailModifierScreen(task));
    }

    public Screen newRailModifierScreen(RailModifierTask task0) {
        final RailModifierTask task = new RailModifierTask(task0);
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(this)
                .setTitle(Text.translatable("gui.mtrsteamloco.compound_creator.rail_modifier"))
                .setDoesConfirmSave(false)
                .transparentBackground()
                .setSavingRunnable(() -> {
                    task0.copyFrom(task);
                    CompoundCreatorScreen.this.update();
                });
        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        final ConfigCategory common = builder.getOrCreateCategory(
                Text.translatable("gui.mtrsteamloco.config.client.category.common")
        );

        final Rail rail = task.rail;

        final String modelKey = getRailModelKey(rail);
        final RailModelProperties properties = RailModelRegistry.elements.get(modelKey);
        final Component modelName = Text.literal(properties != null ? properties.name.getString() : (modelKey.isEmpty() ? "(default)" : modelKey));
        final Runnable openSelect = () -> minecraft.setScreen(new RailModelSelectScreen(task, () -> {
            task0.copyFrom(task);
            setRailModifierScreen(task0);
        }));
        common.addEntry(new SimpleButtonListEntry(
                modelName,
                Text.translatable("gui.mtrsteamloco.brush_edit_rail.present", modelName.getString()),
                b -> openSelect.run(),
                entryBuilder.getResetButtonKey(),
                b -> {
                    setRailModelKey(task.rail, "");
                    task.tryCallRailScript();
                },
                Text.literal("")
        ));

        common.addEntry(
                entryBuilder.startStrField(
                        Text.translatable("gui.mtrsteamloco.brush_edit_rail.rail_type"),
                        rail.railType.name()
                ).setErrorSupplier(str -> {
                    try {
                        final RailType type = RailType.valueOf(str);
                        if (type != null && type != RailType.NONE) return Optional.empty();
                    } catch (Exception ignored) {
                    }
                    return Optional.of(Text.translatable("gui.mtrsteamloco.brush_edit_rail.rail_type_error"));
                }).setSaveConsumer(str -> {
                    try {
                        task.railType = RailType.valueOf(str);
                    } catch (Exception ignored) {
                    }
                }).build()
        );

        common.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.translatable("gui.mtrsteamloco.compound_creator.isOneWay"),
                        task.isOneWay
                ).setSaveConsumer(isOneWay -> task.isOneWay = isOneWay).setDefaultValue(false).build()
        );

        common.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.translatable("gui.mtrsteamloco.compound_creator.isReversed"),
                        task.isReversed
                ).setSaveConsumer(isReversed -> task.isReversed = isReversed).setDefaultValue(false).build()
        );

        return builder.build();
    }

    private class RailModelSelectScreen extends SelectListScreen {

        private final RailModifierTask task;
        private final Runnable returnParent;

        public RailModelSelectScreen(RailModifierTask task, Runnable returnParent) {
            super(Text.literal("Select rail model"));
            this.task = task;
            this.returnParent = returnParent;
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
            final String modelKey = getRailModelKey(task.rail);
            loadSelectPage(key -> !key.equals(modelKey));
        }

        @Override
        protected void onBtnClick(String btnKey) {
            setRailModelKey(task.rail, btnKey);
            task.tryCallRailScript();
        }

        @Override
        protected List<Pair<String, String>> getRegistryEntries() {
            return new HashSet<>(RailModelRegistry.elements.entrySet()).stream()
                    .filter(e -> !e.getValue().name.getString().isEmpty())
                    .map(e -> new Pair<>(e.getKey(), e.getValue().name.getString()))
                    .toList();
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            renderSelectPage(guiGraphics);
        }

        @Override
        public void onClose() {
            returnParent.run();
        }

        @Override
        public boolean isPauseScreen() {
            return true;
        }
    }

    public class SliceTaskScreen extends Screen {

        public SliceTask task;
        protected int tx = 0;
        protected int ty = 0;

        private final Button btnReturn = UtilitiesClient.newButton(20, Text.literal("X"), btn -> onClose());
        private int[] scissor = new int[]{0, 0, 0, 0};
        private List<Square> canvas = new ArrayList<>();
        private final Inventory inventory = new Inventory();

        private final WidgetBetterTextField nameField = new WidgetBetterTextField("", 256);
        private final Button btnAddWidth = UtilitiesClient.newButton(20, Text.literal("+"), btn -> setWidthAndHeight(task.width + 2, task.height));
        private final Button btnSubWidth = UtilitiesClient.newButton(20, Text.literal("-"), btn -> setWidthAndHeight(task.width - 2, task.height));
        private final Button btnAddHeight = UtilitiesClient.newButton(20, Text.literal("+"), btn -> setWidthAndHeight(task.width, task.height + 2));
        private final Button btnSubHeight = UtilitiesClient.newButton(20, Text.literal("-"), btn -> setWidthAndHeight(task.width, task.height - 2));
        private final Button btnSubTX = UtilitiesClient.newButton(20, Text.literal("◁"), btn -> setTX(tx + 4 * Square.length));
        private final Button btnAddTX = UtilitiesClient.newButton(20, Text.literal("▷"), btn -> setTX(tx - 4 * Square.length));
        private final Button btnSubTY = UtilitiesClient.newButton(20, Text.literal("▲"), btn -> setTY(ty + 4 * Square.length));
        private final Button btnAddTY = UtilitiesClient.newButton(20, Text.literal("▼"), btn -> setTY(ty - 4 * Square.length));
        private final Button btnCenter = UtilitiesClient.newButton(20, Text.literal("▣"), btn -> {
            tx = 0;
            ty = 0;
        });
        private final Button btnEnterConfig = UtilitiesClient.newButton(20, Text.translatable("gui.mtrsteamloco.compound_creator.task.config.button"), btn -> setConfigScreen());

        private Square mouseOver = null;

        private final Square now = new Square(0, 0, null, square -> square.state = null, square -> true, square -> true, false, true);

        public SliceTaskScreen(SliceTask task) {
            super(Text.translatable("gui.mtrsteamloco.compound_creator.slice_task"));
            this.task = task;
            reload();
        }

        private void setWidthAndHeight(int width, int height) {
            if (!task.setWidthAndHeight(width, height)) return;
            updateTask();
            reload();
        }

        private void updateTask() {
            for (int i = 0; i < canvas.size() && i < task.lumps.size(); i++) {
                final Lump lump = task.lumps.get(i);
                final Square sq = canvas.get(i);
                lump.blockState = sq.state;
                lump.replacement = sq.replacement;
            }
            update();
        }

        private void reload() {
            final List<Square> newCanvas = new ArrayList<>();
            final Consumer<Square> consumer = square -> {
                square.state = now.state;
                square.replacement = now.replacement;
                updateTask();
            };
            final Function<Square, Boolean> visible = sq -> {
                final int l = Square.length;
                return sq.x + l >= scissor[0] && sq.x <= scissor[0] + scissor[2] && sq.y + l >= scissor[1] && sq.y <= scissor[1] + scissor[3];
            };
            for (Lump lump : task.lumps) {
                newCanvas.add(new Square(0, 0, lump.blockState, consumer, square -> true, visible, false, lump.replacement));
            }
            canvas = newCanvas;
        }

        @Override
        protected void init() {
            nameField.setValue(task.name);
            nameField.moveCursorToStart(false);
            nameField.setResponder(str -> {
                task.name = str;
                updateTask();
            });
            updateWidgetPosition();
            addRenderableWidget(btnReturn);
            addRenderableWidget(nameField);
            addRenderableWidget(btnAddWidth);
            addRenderableWidget(btnSubWidth);
            addRenderableWidget(btnAddHeight);
            addRenderableWidget(btnSubHeight);
            addRenderableWidget(btnSubTX);
            addRenderableWidget(btnAddTX);
            addRenderableWidget(btnSubTY);
            addRenderableWidget(btnAddTY);
            addRenderableWidget(btnCenter);
            addRenderableWidget(btnEnterConfig);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            final List<GuiEventListener> result = new ArrayList<>(super.children());
            result.addAll(canvas);
            result.addAll(inventory.widgets());
            result.add(now);
            return result;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
            if (inventory.isMouseOver(event.x(), event.y()) && inventory.handleClick(event)) {
                return true;
            }
            return super.mouseClicked(event, isDoubleClick);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
            if (inventory.handleDrag(event, dragY)) {
                return true;
            }
            return super.mouseDragged(event, dragX, dragY);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            if (inventory.handleRelease()) {
                return true;
            }
            return super.mouseReleased(event);
        }

        @Override
        public boolean mouseScrolled(double x, double y, double scrollX, double amount) {
            if (inventory.isMouseOver(x, y)) {
                inventory.handleScroll(amount);
                return true;
            }
            return super.mouseScrolled(x, y, scrollX, amount);
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

            setTY(ty);
            setTX(tx);
            mouseOver = null;
            updateWidgetPosition();
            final int[] full = new int[]{40, 40, width - 40 - 10 - Inventory.width, height - 40 - 40};
            scissor = new int[]{62, 52, width - 60 - 10 - Inventory.width - 4, height - 50 - 40 - 4};

            final float midX = tx + scissor[0] + scissor[2] / 2.0F;
            final float midY = ty + scissor[1] + scissor[3] / 2.0F;
            final int x = (int) (midX - task.width / 2.0F * Square.length);
            final int y = (int) (midY - task.height / 2.0F * Square.length);

            final boolean inFull = full[0] <= mouseX && mouseX <= full[0] + full[2] && full[1] <= mouseY && mouseY <= full[1] + full[3];
            guiGraphics.fill(full[0] - 1, full[1] - 1, full[0] + full[2] + 1, full[1] + full[3] + 1, inFull ? 0xfff0eacc : 0xff0c0d0b);
            guiGraphics.fill(full[0], full[1], full[0] + full[2], full[1] + full[3], 0xff424242);

            final boolean inScissor = scissor[0] <= mouseX && mouseX <= scissor[0] + scissor[2] && scissor[1] <= mouseY && mouseY <= scissor[1] + scissor[3];
            guiGraphics.fill(scissor[0] - 1, scissor[1] - 1, scissor[0] + scissor[2] + 1, scissor[1] + scissor[3] + 1, inScissor ? 0xffd1b2b2 : 0xff403636);
            guiGraphics.fill(scissor[0], scissor[1], scissor[0] + scissor[2], scissor[1] + scissor[3], 0xff8f5d5d);

            guiGraphics.enableScissor(scissor[0], full[1], scissor[0] + scissor[2], full[1] + 12);
            final int a = task.width / 2;
            final int step = 3;
            guiGraphics.centeredText(font, "0", (int) midX, 42, 0xFFFFFFFF);
            if (a >= 1) {
                if (a < step) {
                    guiGraphics.centeredText(font, "+" + a, (int) midX + a * Square.length, 42, 0xFFFFFFFF);
                    guiGraphics.centeredText(font, "-" + a, (int) midX - a * Square.length, 42, 0xFFFFFFFF);
                } else {
                    for (int i = step; i <= a; i += step) {
                        guiGraphics.centeredText(font, "+" + i, (int) midX + i * Square.length, 42, 0xFFFFFFFF);
                        guiGraphics.centeredText(font, "-" + i, (int) midX - i * Square.length, 42, 0xFFFFFFFF);
                    }
                }
            }
            guiGraphics.disableScissor();

            guiGraphics.enableScissor(full[0], scissor[1], full[0] + 22, scissor[1] + scissor[3]);
            final int b = task.height / 2;
            final int ax = 50;
            guiGraphics.centeredText(font, "0", ax, (int) midY - 5, 0xFFFFFFFF);
            if (b >= 1) {
                if (b < step) {
                    guiGraphics.centeredText(font, "+" + b, ax, (int) midY - 5 + b * Square.length, 0xFFFFFFFF);
                    guiGraphics.centeredText(font, "-" + b, ax, (int) midY - 5 - b * Square.length, 0xFFFFFFFF);
                } else {
                    for (int i = step; i <= b; i += step) {
                        guiGraphics.centeredText(font, "-" + i, ax, (int) midY - 5 + i * Square.length, 0xFFFFFFFF);
                        guiGraphics.centeredText(font, "+" + i, ax, (int) midY - 5 - i * Square.length, 0xFFFFFFFF);
                    }
                }
            }
            guiGraphics.disableScissor();

            guiGraphics.enableScissor(scissor[0], scissor[1], scissor[0] + scissor[2], scissor[1] + scissor[3]);
            for (int i = 0; i < canvas.size(); i++) {
                canvas.get(i).render(guiGraphics, mouseX, mouseY, x + i % task.width * Square.length, y + i / task.width * Square.length, partialTick);
            }
            int py = (int) midY - 18 / 2 - 1;
            guiGraphics.fill(x, py, x + task.width * Square.length, py + 2, 0x7FFF0000);
            py += 18;
            guiGraphics.fill(x, py, x + task.width * Square.length, py + 2, 0x7FFF0000);
            int px = (int) midX - 18 / 2 - 1;
            guiGraphics.fill(px, y, px + 2, y + task.height * Square.length, 0x7F00FF00);
            px += 18;
            guiGraphics.fill(px, y, px + 2, y + task.height * Square.length, 0x7F00FF00);
            guiGraphics.disableScissor();

            now.render(guiGraphics, mouseX, mouseY, 191, 11, partialTick);
            inventory.render(guiGraphics, mouseX, mouseY, partialTick);

            if (mouseOver != null) {
                mouseOver.renderTooltip(guiGraphics, mouseX, mouseY);
            }
        }

        private void setConfigScreen() {
            final ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(this)
                    .setTitle(Text.translatable("gui.mtrsteamloco.compound_creator.task.config.title"))
                    .setDoesConfirmSave(false)
                    .transparentBackground();
            final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            final ConfigCategory common = builder.getOrCreateCategory(
                    Text.translatable("gui.mtrsteamloco.config.client.category.common")
            );

            final Function<Double, Optional<Component>> positive = d -> d <= 0
                    ? Optional.of(Text.translatable("gui.mtrsteamloco.error.invalid_value"))
                    : Optional.empty();

            common.addEntry(entryBuilder
                    .startDoubleField(Text.translatable("gui.mtrsteamloco.compound_creator.task.config.start_pos"), task.start)
                    .setDefaultValue(0)
                    .setSaveConsumer(d -> task.start = d)
                    .setMin(0)
                    .build()
            );

            common.addEntry(entryBuilder
                    .startDoubleField(Text.translatable("gui.mtrsteamloco.compound_creator.task.config.length"), task.length == null ? -1 : task.length)
                    .setDefaultValue(-1)
                    .setSaveConsumer(d -> task.length = d <= 0 ? null : d)
                    .build()
            );

            common.addEntry(entryBuilder
                    .startDoubleField(Text.translatable("gui.mtrsteamloco.compound_creator.task.config.interval"), task.interval == null ? -1 : task.interval)
                    .setDefaultValue(-1)
                    .setSaveConsumer(d -> task.interval = d <= 0 ? null : d)
                    .build()
            );

            common.addEntry(entryBuilder
                    .startDoubleField(Text.translatable("gui.mtrsteamloco.compound_creator.task.config.increment"), task.increment)
                    .setDefaultValue(0.1)
                    .setSaveConsumer(d -> task.increment = d)
                    .setErrorSupplier(positive)
                    .build()
            );

            common.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("gui.mtrsteamloco.compound_creator.task.config.use_yaw"), task.useYaw)
                    .setDefaultValue(true)
                    .setSaveConsumer(value -> task.useYaw = value)
                    .build()
            );

            common.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("gui.mtrsteamloco.compound_creator.task.config.use_pitch"), task.usePitch)
                    .setDefaultValue(true)
                    .setSaveConsumer(value -> task.usePitch = value)
                    .build()
            );

            common.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("gui.mtrsteamloco.compound_creator.task.config.use_roll"), task.useRoll)
                    .setDefaultValue(false)
                    .setSaveConsumer(value -> task.useRoll = value)
                    .build()
            );

            builder.setSavingRunnable(this::updateTask);

            minecraft.setScreen(builder.build());
        }

        private void updateWidgetPosition() {
            IDrawing.setPositionAndWidth(btnReturn, 10, 10, 20);
            IDrawing.setPositionAndWidth(nameField, 40, 10, 90);
            // Kept at ANTE's width: a wider button would collide with the "current block" square
            // that is drawn at x = 191 on the same row.
            IDrawing.setPositionAndWidth(btnEnterConfig, 140, 10, 40);

            IDrawing.setPositionAndWidth(btnAddWidth, 40, height - 30, 20);
            IDrawing.setPositionAndWidth(btnSubWidth, 70, height - 30, 20);
            IDrawing.setPositionAndWidth(btnSubTX, 100, height - 30, 20);
            IDrawing.setPositionAndWidth(btnAddTX, 130, height - 30, 20);

            IDrawing.setPositionAndWidth(btnAddHeight, 10, 40, 20);
            IDrawing.setPositionAndWidth(btnSubHeight, 10, 70, 20);
            IDrawing.setPositionAndWidth(btnSubTY, 10, 100, 20);
            IDrawing.setPositionAndWidth(btnAddTY, 10, 130, 20);

            IDrawing.setPositionAndWidth(btnCenter, 10, height - 30, 20);
        }

        private void setTX(int tx) {
            final int w = task.width * Square.length;
            if (w <= scissor[2]) {
                this.tx = 0;
                return;
            }
            final int full = w - scissor[2];
            final int min = -full / 2;
            final int max = full / 2;
            if (tx < min) tx = min;
            else if (tx > max) tx = max;
            this.tx = tx;
        }

        private void setTY(int ty) {
            final int h = task.height * Square.length;
            if (h <= scissor[3]) {
                this.ty = 0;
                return;
            }
            final int full = h - scissor[3];
            final int min = -full / 2;
            final int max = full / 2;
            if (ty < min) ty = min;
            else if (ty > max) ty = max;
            this.ty = ty;
        }

        @Override
        public void onClose() {
            minecraft.setScreen(CompoundCreatorScreen.this);
        }

        public class Square implements GuiEventListener {

            public static final int length = 18;
            public int x;
            public int y;
            public BlockState state;
            public Consumer<Square> consumer;
            public Function<Square, Boolean> highlight = square -> false;
            public Function<Square, Boolean> visible = square -> true;
            public boolean fixed = false;
            public boolean replacement = false;

            private boolean isFocused = false;

            public Square(Square other) {
                this.x = other.x;
                this.y = other.y;
                this.state = other.state;
                this.consumer = other.consumer;
                this.highlight = other.highlight;
                this.visible = other.visible;
                this.fixed = other.fixed;
                this.replacement = other.replacement;
            }

            public Square(int x, int y, BlockState state, Consumer<Square> consumer, Function<Square, Boolean> highlight, Function<Square, Boolean> visible, boolean fixed, boolean replacement) {
                this.x = x;
                this.y = y;
                this.state = state;
                this.consumer = consumer;
                if (highlight != null) this.highlight = highlight;
                if (visible != null) this.visible = visible;
                this.fixed = fixed;
                this.replacement = replacement;
            }

            public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int tx, int ty, float partialTick) {
                x = tx;
                y = ty;
                if (!isVisible()) return;
                final boolean hovered = isMouseOver(mouseX, mouseY);
                guiGraphics.fill(x, y, x + length, y + length, hovered ? 0xfffafff2 : 0xff9b9e96);
                guiGraphics.fill(x + 1, y + 1, x + length - 1, y + length - 1, 0xff919191);
                if (state != null) {
                    renderBlockState(guiGraphics, x + 1, y + 1, state);
                    guiGraphics.fill(x + 1, y + 1, x + length - 1, y + length - 1, highlight.apply(this) ? 0x2ff5f5f5 : 0x1fdda9df);
                    blitIcon(guiGraphics, replacement ? PURPLE_CIRCLE : BLUE_CIRCLE, x + 1, y + 1);
                    if (fixed) blitIcon(guiGraphics, MID_CIRCLE, x + 1, y + 1);
                }
                if (hovered) {
                    mouseOver = this;
                }
            }

            public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
                final String str;
                if (state != null) {
                    final Block block = state.getBlock();
                    final StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
                    final Collection<Property<?>> collection = stateDefinition.getProperties();
                    final StringBuilder sb = new StringBuilder();
                    sb.append("/");
                    for (Property<?> property : collection) {
                        sb.append(property.getName());
                        sb.append(": ");
                        sb.append(state.getValue(property).toString());
                        sb.append("/");
                    }
                    str = "Replacement:" + replacement + '\n' + block.getName().getString() + '\n' + block.getDescriptionId() + '\n' + sb;
                } else {
                    str = "Empty";
                }
                final List<net.minecraft.util.FormattedCharSequence> lines = font.split(Component.literal(str), Math.max(40, width - Inventory.width - 10));
                final int h = (int) (font.lineHeight * 1.1F);
                int y = height - lines.size() * h;
                for (net.minecraft.util.FormattedCharSequence line : lines) {
                    guiGraphics.text(font, line, 0, y, 0xFFFFFFFF);
                    y += h;
                }
            }

            public boolean isMouseOver(double mouseX, double mouseY) {
                if (!isVisible()) return false;
                return x <= mouseX && mouseX <= x + length && y <= mouseY && mouseY <= y + length;
            }

            private boolean isVisible() {
                return x >= -length && x <= SliceTaskScreen.this.width && y >= -length && y <= SliceTaskScreen.this.height && visible.apply(this);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
                if (!isMouseOver(event.x(), event.y())) return false;
                if (event.button() == 0) {
                    consumer.accept(this);
                } else if (event.button() == 1) {
                    if (state != null) {
                        minecraft.setScreen(newPropertyScreen(this, fixed));
                    }
                } else if (event.button() == 2) {
                    now.state = this.state;
                    now.replacement = this.replacement;
                }
                return true;
            }

            @Override
            public boolean isFocused() {
                return isFocused;
            }

            @Override
            public void setFocused(boolean focused) {
                isFocused = focused;
            }
        }

        private void blitIcon(GuiGraphicsExtractor guiGraphics, Identifier texture, int x, int y) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0F, 0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        private void renderBlockState(GuiGraphicsExtractor guiGraphics, int x, int y, BlockState state) {
            final ItemStack stack = new ItemStack(state.getBlock());
            if (!stack.isEmpty()) {
                guiGraphics.item(stack, x, y);
            }
        }

        public Screen newPropertyScreen(Square square, boolean fixed) {
            final Square present = fixed ? new Square(square) : square;
            if (present.state == null) return this;
            final Block block = present.state.getBlock();
            final StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
            final Collection<Property<?>> collection = stateDefinition.getProperties();

            final ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(this)
                    .setTitle(block.getName())
                    .setDoesConfirmSave(false)
                    .setSavingRunnable(() -> {
                        now.state = present.state;
                        now.replacement = present.replacement;
                    });
            final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            final ConfigCategory common = builder.getOrCreateCategory(
                    Text.translatable("gui.mtrsteamloco.config.client.category.common")
            );

            common.addEntry(
                    entryBuilder.startBooleanToggle(
                                    Text.translatable("gui.mtrsteamloco.compound_creator.replacement"),
                                    present.replacement)
                            .setDefaultValue(true)
                            .setSaveConsumer(value -> present.replacement = value)
                            .build()
            );

            for (Property<?> property : collection) {
                common.addEntry(newPropertyEntry(present, property, entryBuilder));
            }

            return builder.build();
        }

        @SuppressWarnings("unchecked")
        private <T extends Comparable<T>> ButtonCycleListEntry newPropertyEntry(Square square, Property<?> property, ConfigEntryBuilder entryBuilder) {
            return newButtonCycleListEntry(square, (Property<T>) property, entryBuilder);
        }

        private <T extends Comparable<T>> ButtonCycleListEntry newButtonCycleListEntry(Square square, Property<T> property, ConfigEntryBuilder entryBuilder) {
            final BlockState state = square.state;
            final BlockState def = state.getBlock().defaultBlockState();
            final List<String> values = new ArrayList<>();
            for (T value : property.getPossibleValues()) {
                values.add(property.getName(value));
            }
            final String current = property.getName(property.value(state).value());
            final int index = Math.max(0, values.indexOf(current));
            final String defaultName = property.getName(property.value(def).value());
            final int defIndex = Math.max(0, values.indexOf(defaultName));
            return new ButtonCycleListEntry(Text.literal(property.getName()), index, values, entryBuilder.getResetButtonKey(), () -> defIndex, ind -> {
                final Optional<T> value = property.getValue(values.get(ind));
                value.ifPresent(t -> square.state = square.state.setValue(property, t));
            }, null, false);
        }

        public class Inventory implements GuiEventListener {

            public static final int width = 100;
            public static final int col = 5;

            private int scroll = 0;
            private final List<Square> blocksList = new ArrayList<>();
            private List<Square> searchedList = new ArrayList<>();
            private boolean draggingSlider = false;
            private final EditBox searchField = new EditBox(Minecraft.getInstance().font, 11, 45, width, 15, Text.literal(""));
            private int[] scissor = new int[]{19, 19, 8, 10};
            private boolean isFocused = false;

            public Inventory() {
                for (Block block : BuiltInRegistries.BLOCK) {
                    markSquare(new Square(0, 0, block.defaultBlockState(), square -> now.state = square.state, square -> square.state == now.state, square -> true, true, true));
                }
                searchedList = new ArrayList<>(blocksList);
                searchField.moveCursorToStart(false);
                searchField.setResponder(this::search);
            }

            private void markSquare(Square square) {
                blocksList.add(square);
            }

            public void search(String str) {
                if (str.isEmpty()) {
                    searchedList = new ArrayList<>(blocksList);
                    return;
                }
                final String lower = str.toLowerCase();
                final List<Square> list = new ArrayList<>();
                for (Square square : blocksList) {
                    final String blockName = square.state.getBlock().getName().getString().toLowerCase();
                    final String descriptionId = square.state.getBlock().getDescriptionId().toLowerCase();
                    if (blockName.contains(lower) || descriptionId.contains(lower)) {
                        list.add(square);
                    }
                }
                searchedList = list;
            }

            public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
                guiGraphics.fill(scissor[0], 0, scissor[0] + scissor[2], height, 0xff212121);
                guiGraphics.fill(scissor[0], 0, scissor[0] + 1, height, mouseX >= scissor[0] ? 0xfff2f7eb : 0xffafb3aa);
                IDrawing.setPositionAndWidth(searchField, SliceTaskScreen.this.width - width + 3, 1, width - 3);
                searchField.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
                scissor = new int[]{SliceTaskScreen.this.width - width, 18, width, SliceTaskScreen.this.height - 18};
                checkAndScroll(scroll);
                final int x = scissor[0] + 3;
                guiGraphics.enableScissor(scissor[0], scissor[1], scissor[0] + scissor[2], scissor[1] + scissor[3]);
                for (int i = 0; i < searchedList.size(); i++) {
                    searchedList.get(i).render(guiGraphics, mouseX, mouseY, x + i % col * Square.length, scissor[1] + scroll + i / col * Square.length, partialTick);
                }
                guiGraphics.disableScissor();
                if (canScroll()) {
                    final int[] pas = getSliderPositionAndSize();
                    guiGraphics.fill(pas[0], pas[1], pas[0] + pas[2], pas[1] + pas[3], 0xffb0b0b0);
                }
            }

            public List<? extends GuiEventListener> widgets() {
                final List<GuiEventListener> result = new ArrayList<>();
                result.add(searchField);
                result.addAll(searchedList);
                return result;
            }

            public boolean handleClick(MouseButtonEvent event) {
                if (!canScroll()) return false;
                if (isMouseOverSlider(event.x(), event.y())) {
                    setScroll((int) event.y());
                    return true;
                }
                return false;
            }

            public boolean handleDrag(MouseButtonEvent event, double dragY) {
                if (isMouseOverSlider(event.x(), event.y()) || draggingSlider) {
                    setScroll((int) (event.y() + dragY));
                    draggingSlider = true;
                    return true;
                }
                return false;
            }

            public boolean handleRelease() {
                if (draggingSlider) {
                    draggingSlider = false;
                    return true;
                }
                return false;
            }

            public void handleScroll(double amount) {
                if (!canScroll()) return;
                checkAndScroll(scroll + 20 * (int) amount);
            }

            @Override
            public boolean isMouseOver(double mouseX, double mouseY) {
                return scissor[0] <= mouseX && mouseX <= scissor[0] + scissor[2] && scissor[1] <= mouseY && mouseY <= scissor[1] + scissor[3];
            }

            private boolean isMouseOverSlider(double mouseX, double mouseY) {
                if (!canScroll()) return false;
                final int[] pas = getSliderPositionAndSize();
                return pas[0] <= mouseX && mouseX <= pas[0] + pas[2] && pas[1] <= mouseY && mouseY <= pas[1] + pas[3];
            }

            private int[] getSliderPositionAndSize() {
                final float ah = ah();
                final float th = scissor[3];
                final int h = ah <= 0 ? scissor[3] : (int) (th / ah * th);
                final int py = scissor[1] + (int) (-1F * scroll / Math.max(1F, ah) * th);
                return new int[]{SliceTaskScreen.this.width - 5, py, 5, Math.max(5, h)};
            }

            private boolean canScroll() {
                return ah() > scissor[3];
            }

            private int ah() {
                return (searchedList.size() / col) * Square.length;
            }

            private void setScroll(int mouseY) {
                final int[] pas = getSliderPositionAndSize();
                final int sh = pas[3];
                final int maxd = Math.max(1, scissor[3] - sh);
                final int dy = mouseY - sh / 2 - scissor[1];
                final int maxScroll = -ah() + scissor[3];
                checkAndScroll((int) (dy / ((float) maxd) * maxScroll));
            }

            private void checkAndScroll(int temp) {
                if (!canScroll()) {
                    scroll = 0;
                    return;
                }
                if (temp > 0) temp = 0;
                final int min = -ah() + scissor[3];
                if (temp < min) temp = min;
                scroll = temp;
            }

            @Override
            public boolean isFocused() {
                return isFocused;
            }

            @Override
            public void setFocused(boolean focused) {
                isFocused = focused;
            }
        }
    }
}
