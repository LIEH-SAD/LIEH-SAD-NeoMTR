package cn.zbx1425.mtrsteamloco.gui;

import cn.zbx1425.mtrsteamloco.ClientConfig;
import cn.zbx1425.mtrsteamloco.Main;
import cn.zbx1425.mtrsteamloco.block.BlockEyeCandy.BlockEntityEyeCandy;
import cn.zbx1425.mtrsteamloco.block.BlockItemEyeCandy;
import cn.zbx1425.mtrsteamloco.data.EyeCandyProperties;
import cn.zbx1425.mtrsteamloco.data.EyeCandyRegistry;
import cn.zbx1425.mtrsteamloco.gui.entries.SliderOrTextFieldListEntry;
import cn.zbx1425.mtrsteamloco.network.PacketUpdateHoldingItem;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import mtr.mappings.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EyeCandyScreen {

    public static Screen createScreen(InteractionHand hand, Screen parent) {
        ItemStack itemStack = Minecraft.getInstance().player.getItemInHand(hand);
        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof BlockItemEyeCandy)) {
            return parent;
        }

        VirtualEyeCandy virtualEyeCandy = new VirtualEyeCandy(itemStack, hand);
        return createScreen(virtualEyeCandy, parent);
    }

    public static Screen createScreen(BlockPos blockPos, Screen parent) {
        Optional<BlockEntityEyeCandy> opt = getBlockEntity(blockPos);
        BlockEntityEyeCandy blockEntity = opt.orElse(null);
        if (blockEntity == null) {
            return parent;
        }

        return createScreen(blockEntity, parent);
    }

    public static Screen createScreen(BlockEntityEyeCandy blockEntity, Screen parent) {

        List<Consumer<BlockEntityEyeCandy>> update = new ArrayList<>();// updateBlockEntityCallbacks;

        EyeCandyProperties properties = blockEntity.getProperties();
        String pid = "";
        if (properties != null) {
            pid = properties.name.getString() + " (" + blockEntity.prefabId + ")";
        }

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(tr("title"))
                .setDoesConfirmSave(false)
                .setSavingRunnable(() -> {
                    for (Consumer<BlockEntityEyeCandy> callback : update) {
                        callback.accept(blockEntity);
                    }
                    blockEntity.sendUpdateC2S();
                })
                .transparentBackground();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory common = builder.getOrCreateCategory(
                Text.translatable("gui.mtrsteamloco.config.client.category.common")
        );

        common.addEntry(ButtonListEntry.createCenteredInstance(
            Text.translatable("gui.mtrsteamloco.eye_candy.present", properties == null ? (blockEntity.prefabId == null ? "" : blockEntity.prefabId) : properties.name.getString()),
            btn -> Minecraft.getInstance().setScreen(createSelectScreen(blockEntity, () -> createScreen(blockEntity, parent)))));

        common.addEntry(entryBuilder
                .startBooleanToggle(
                        tr("full_light"),
                        blockEntity.fullLight
                ).setSaveConsumer(checked -> {
                    if (checked != blockEntity.fullLight) {
                        update.add(be -> be.fullLight = checked);
                    }
                }).setDefaultValue(blockEntity.fullLight).build()
        );

        common.addEntry(entryBuilder
                .startBooleanToggle(
                        tr("as_platform"), 
                        blockEntity.asPlatform
                ).setSaveConsumer(checked -> {
                    if (checked != blockEntity.asPlatform) {
                        update.add(be -> be.asPlatform = checked);
                    }
                }).setDefaultValue(blockEntity.asPlatform).build()
        );

        common.addEntry(entryBuilder.startTextDescription(
                    Text.translatable("gui.mtrsteamloco.eye_candy.shape", blockEntity.getShape().toString())
            ).build());

        common.addEntry(entryBuilder.startTextDescription(
                    Text.translatable("gui.mtrsteamloco.eye_candy.collision", blockEntity.getCollisionShape().toString())
            ).build());

        if (blockEntity.fixedMatrix) {
            common.addEntry(entryBuilder.startTextDescription(
                    tr("fixed")
            ).build());

            common.addEntry(entryBuilder.startTextDescription(
                    Text.literal("TX: " + blockEntity.translateX * 100 + "cm, TY: " + blockEntity.translateY * 100 + "cm, TZ: " + blockEntity.translateZ * 100 + "cm")
            ).build());

            common.addEntry(entryBuilder.startTextDescription(
                    Text.literal("RX: " + Math.toDegrees(blockEntity.rotateX) + "°, RY: " + Math.toDegrees(blockEntity.rotateY) + "°, RZ: " + Math.toDegrees(blockEntity.rotateZ) + "°")
            ).build());

            common.addEntry(entryBuilder.startTextDescription(
                    Text.literal("SX: " + blockEntity.scaleX + ", SY: " + blockEntity.scaleY + ", SZ: " + blockEntity.scaleZ)
            ).build());
        } else {
            List<SliderOrTextFieldListEntry> entries = new ArrayList<>();

            common.addEntry(new SimpleButtonListEntry(
                tr("adjust_settings_and_reset_pose"), 
                Text.translatable("gui.mtrsteamloco.adjust_settings"),
                btn -> Minecraft.getInstance().setScreen(createAdjustScreen(() -> createScreen(blockEntity, parent))),
                entryBuilder.getResetButtonKey(), 
                btn -> {
                    blockEntity.translateX = 0;
                    blockEntity.translateY = 0;
                    blockEntity.translateZ = 0;
                    blockEntity.rotateX = 0;
                    blockEntity.rotateY = 0;
                    blockEntity.rotateZ = 0;
                    blockEntity.scaleX = 1;
                    blockEntity.scaleY = 1;
                    blockEntity.scaleZ = 1;
                    blockEntity.sendUpdateC2S();
                    for (int i = 0; i < 9; i++) {
                        entries.get(i).setValue(getValue(i, blockEntity));
                    }
                },
                entryBuilder.getResetButtonKey()
            ));
        
            addTranslation(entries, common, entryBuilder, 0, blockEntity);
            addTranslation(entries, common, entryBuilder, 1, blockEntity);
            addTranslation(entries, common, entryBuilder, 2, blockEntity);
            addRotation(entries, common, entryBuilder, 3, blockEntity);
            addRotation(entries, common, entryBuilder, 4, blockEntity);
            addRotation(entries, common, entryBuilder, 5, blockEntity);
            addScale(entries, common, entryBuilder, 6, blockEntity);
            addScale(entries, common, entryBuilder, 7, blockEntity);
            addScale(entries, common, entryBuilder, 8, blockEntity);
        }

        // List<AbstractConfigListEntry> customEntrys = ConfigResponder.getEntrysFromMaps(blockEntity.getCustomConfigs(), blockEntity.getCustomResponders(), entryBuilder, () -> createScreen(blockEntity, parent));
        // for (AbstractConfigListEntry entry : customEntrys) {
        //     common.addEntry(entry);
        // }

        return builder.build();
    }

    private static Screen createAdjustScreen(Supplier<Screen> parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(new FakeScreen(parent.get()))
                .setTitle(Text.translatable("gui.mtrsteamloco.adjust_settings"))
                .setDoesConfirmSave(false)
                .setSavingRunnable(() -> {
                    ClientConfig.save();
                })
                .transparentBackground();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory common = builder.getOrCreateCategory(
                Text.translatable("gui.mtrsteamloco.config.client.category.common")
        );

        List<AbstractConfigListEntry> entries = new ArrayList<>();
        ClientConfig.eyecandyScreenGroup.getListEntries(entries, entryBuilder, () -> createAdjustScreen(parent));

        for (AbstractConfigListEntry entry : entries) {
            common.addEntry(entry);
        }

        return builder.build();
    }

    private static Screen createSelectScreen(BlockEntityEyeCandy blockEntity, Supplier<Screen> parent) {
        return new SelectScreen(parent.get(),  EyeCandyRegistry.TREE, () -> blockEntity.prefabId, (mc, key) -> {
            blockEntity.setPrefabId((String) key);
            blockEntity.sendUpdateC2S();
        });
    }

    private static void save(int type, float value, BlockEntityEyeCandy blockEntity) {
        float old = getValue(type, blockEntity);
        if (old == value) return;
        switch (type) {
            case 0: blockEntity.translateX = value; break;
            case 1: blockEntity.translateY = value; break;
            case 2: blockEntity.translateZ = value; break;
            case 3: blockEntity.rotateX = value; break;
            case 4: blockEntity.rotateY = value; break;
            case 5: blockEntity.rotateZ = value; break;
            case 6: blockEntity.scaleX = value; break;
            case 7: blockEntity.scaleY = value; break;
            case 8: blockEntity.scaleZ = value; break;
        }
        blockEntity.sendUpdateC2S();
    }

    private static float getValue(int type, BlockEntityEyeCandy blockEntity) {
        switch (type) {
            case 0: return blockEntity.translateX;
            case 1: return blockEntity.translateY;
            case 2: return blockEntity.translateZ;
            case 3: return blockEntity.rotateX;
            case 4: return blockEntity.rotateY;
            case 5: return blockEntity.rotateZ;
            case 6: return blockEntity.scaleX;
            case 7: return blockEntity.scaleY;
            case 8: return blockEntity.scaleZ;
        }
        return 0;
    }

    private static String getStr(int type, BlockEntityEyeCandy blockEntity) {
        switch (type) {
            case 0: return "TX";
            case 1: return "TY";
            case 2: return "TZ";
            case 3: return "RX";
            case 4: return "RY";
            case 5: return "RZ";
            case 6: return "SX";
            case 7: return "SY";
            case 8: return "SZ";
        }
        return "";
    }

    private static void addTranslation(List<SliderOrTextFieldListEntry> entries, ConfigCategory common, ConfigEntryBuilder entryBuilder, int type, BlockEntityEyeCandy blockEntity) {
        ClientConfig.Entry e = ClientConfig.eyecandyScreenGroup.entries[0];
        int type0 = type;
        SliderOrTextFieldListEntry entry = new SliderOrTextFieldListEntry(
            Text.literal(getStr(type, blockEntity)),
            () -> entryBuilder.getResetButtonKey(),
            getValue(type, blockEntity),
            e.min, e.max, e.step, f -> Component.literal(String.format("%.0fCM", f * 100)),
            f -> save(type, f, blockEntity),
            str -> parseMovement(str),
            e.modes[type0], i -> {
                e.modes[type0] = i;
                ClientConfig.save();
            }
        );

        common.addEntry(entry);
        entries.add(entry);
    }

    private static void addRotation(List<SliderOrTextFieldListEntry> entries, ConfigCategory common, ConfigEntryBuilder entryBuilder, int type, BlockEntityEyeCandy blockEntity) {
        ClientConfig.Entry e = ClientConfig.eyecandyScreenGroup.entries[1];
        int type0 = type - 3;
        SliderOrTextFieldListEntry entry = new SliderOrTextFieldListEntry(
            Text.literal(getStr(type, blockEntity)),
            () -> entryBuilder.getResetButtonKey(),
            getValue(type, blockEntity) * 180 / (float) Math.PI,
            e.min, e.max, e.step, f -> Component.literal(String.format("%.0f°", f)),
            f -> save(type, f / 180 * (float) Math.PI, blockEntity),
            str -> parseRotation(str),
            e.modes[type0], i -> {
                e.modes[type0] = i;
                ClientConfig.save();
            }
        );

        common.addEntry(entry);
        entries.add(entry);
    }

    private static void addScale(List<SliderOrTextFieldListEntry> entries, ConfigCategory common, ConfigEntryBuilder entryBuilder, int type, BlockEntityEyeCandy blockEntity) {
        ClientConfig.Entry e = ClientConfig.eyecandyScreenGroup.entries[2];
        int type0 = type - 6;
        SliderOrTextFieldListEntry entry = new SliderOrTextFieldListEntry(
            Text.literal(getStr(type, blockEntity)),
            () -> entryBuilder.getResetButtonKey(),
            getValue(type, blockEntity),
            e.min, e.max, e.step, f -> Component.literal(String.format("%.2f", f)),
            f -> save(type, f, blockEntity),
            str -> parseScale(str),
            e.modes[type0], i -> {
                e.modes[type0] = i;
                ClientConfig.save();
            }
        );

        common.addEntry(entry);
        entries.add(entry);
    }

    private static Optional<Float> parseScale(String str) {
        try {
            Float value = Float.parseFloat(str);
            return Optional.of(value);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<Float> parseMovement(String str) {
        try {
            Float value = 0f;
            str = str.toLowerCase().trim();
            if (str.endsWith("cm")) {
                value = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
            } else if (str.endsWith("m")) {
                value = Float.parseFloat(str.substring(0, str.length() - 1));
            } else {
                value = Float.parseFloat(str);
            }
            return Optional.of(value);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<Float> parseRotation(String str) {
        try {
            Float value = 0f;
            str = str.toLowerCase().trim();
            if (str.endsWith("°")) {
                value = Float.parseFloat(str.substring(0, str.length() - 1));
            } else {
                value = Float.parseFloat(str);
            }
            return Optional.of(value);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<BlockEntityEyeCandy> getBlockEntity(BlockPos blockPos) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return Optional.empty();
        return level.getBlockEntity(blockPos, Main.BLOCK_ENTITY_TYPE_EYE_CANDY.get());
    }

    private static Component tr(String key) {
        return Text.translatable("gui.mtrsteamloco.eye_candy." + key);
    }

    private static class VirtualEyeCandy extends BlockEntityEyeCandy {
        private final ItemStack itemStack;
        private final InteractionHand hand;

        public VirtualEyeCandy(ItemStack itemStack, InteractionHand hand) {
            super(new BlockPos(0, -1145141919, 0), null);
            this.itemStack = itemStack;
            this.hand = hand;
            readCompoundTag(itemStack.getOrDefault(Main.TOOL_TAG.get(), new CompoundTag()));
        }

        @Override
        public void sendUpdateC2S() {
            CompoundTag tag = itemStack.getOrDefault(Main.TOOL_TAG.get(), new CompoundTag()).copy();
            writeCompoundTag(tag);
            itemStack.set(Main.TOOL_TAG.get(), tag);
            PacketUpdateHoldingItem.sendUpdateC2S();
        }

        public void readCompoundTag(net.minecraft.nbt.CompoundTag compoundTag) {
            prefabId = compoundTag.getStringOr("prefabId", "");
            if (prefabId.isEmpty()) prefabId = null;
            fullLight = compoundTag.getBooleanOr("fullLight", false);
            asPlatform = compoundTag.getBooleanOr("asPlatform", false);
            fixedMatrix = compoundTag.getBooleanOr("fixedMatrix", false);

            translateX = compoundTag.getFloatOr("translateX", 0);
            translateY = compoundTag.getFloatOr("translateY", 0);
            translateZ = compoundTag.getFloatOr("translateZ", 0);
            rotateX = compoundTag.getFloatOr("rotateX", 0);
            rotateY = compoundTag.getFloatOr("rotateY", 0);
            rotateZ = compoundTag.getFloatOr("rotateZ", 0);
            scaleX = compoundTag.getFloatOr("scaleX", 1);
            scaleY = compoundTag.getFloatOr("scaleY", 1);
            scaleZ = compoundTag.getFloatOr("scaleZ", 1);
        }

        public void writeCompoundTag(net.minecraft.nbt.CompoundTag compoundTag) {
            compoundTag.putString("prefabId", prefabId == null ? "" : prefabId);
            compoundTag.putBoolean("fullLight", fullLight);
            compoundTag.putBoolean("asPlatform", asPlatform);
            compoundTag.putBoolean("fixedMatrix", fixedMatrix);

            compoundTag.putFloat("translateX", translateX);
            compoundTag.putFloat("translateY", translateY);
            compoundTag.putFloat("translateZ", translateZ);
            compoundTag.putFloat("rotateX", rotateX);
            compoundTag.putFloat("rotateY", rotateY);
            compoundTag.putFloat("rotateZ", rotateZ);
            compoundTag.putFloat("scaleX", scaleX);
            compoundTag.putFloat("scaleY", scaleY);
            compoundTag.putFloat("scaleZ", scaleZ);
        }
    }
}