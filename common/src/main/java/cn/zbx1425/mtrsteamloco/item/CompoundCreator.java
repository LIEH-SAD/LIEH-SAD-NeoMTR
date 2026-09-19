package cn.zbx1425.mtrsteamloco.item;

import cn.zbx1425.mtrsteamloco.Main;
import cn.zbx1425.mtrsteamloco.data.RailExtraSupplier;
import cn.zbx1425.mtrsteamloco.data.RailModelProperties;
import cn.zbx1425.mtrsteamloco.data.RailModelRegistry;
import cn.zbx1425.mtrsteamloco.network.PacketScreen;
import cn.zbx1425.sowcer.math.Matrix4f;
import cn.zbx1425.sowcer.math.Vector3f;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import mtr.block.BlockNode;
import mtr.data.Rail;
import mtr.data.RailAngle;
import mtr.data.RailType;
import mtr.data.RailwayData;
import mtr.data.TransportMode;
import mtr.item.ItemNodeModifierBase;
import mtr.mappings.Text;
import mtr.packet.PacketTrainDataGuiServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class CompoundCreator extends ItemNodeModifierBase {

    public static final String TAG_TASKS = "tasks";

    public CompoundCreator(Item.Properties properties) {
        super(properties, true, false, false, true);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketScreen.sendScreenS2C(serverPlayer, "compound_creator");
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (clickCondition(ctx)) {
            // Let the base class handle the start/end click of the connection. It reports PASS on
            // the client, which would make the client additionally fire use() and open the task
            // editor on top of the connection flow, so report a consuming result here.
            final InteractionResult result = super.useOn(ctx);
            return result.consumesAction() ? result : InteractionResult.SUCCESS;
        } else {
            // Not a connectable node: open the task editor instead of connecting.
            final Player player = ctx.getPlayer();
            if (player instanceof ServerPlayer serverPlayer) {
                PacketScreen.sendScreenS2C(serverPlayer, "compound_creator");
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected void onRemove(Level world, BlockPos start, BlockPos end, Player player, RailwayData data) {
    }

    @Override
    protected void onConnect(Level world, ItemStack stack, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, RailAngle facingStart, RailAngle facingEnd, Player player, RailwayData railwayData) {
        BlockPos tempPos = posStart;
        posStart = posEnd;
        posEnd = tempPos;
        RailAngle tempFacing = facingStart;
        facingStart = facingEnd;
        facingEnd = tempFacing;
        BlockState tempState = stateStart;
        stateStart = stateEnd;
        stateEnd = tempState;

        if (player == null) return;
        final CompoundTag tag = stack.getOrDefault(Main.TOOL_TAG.get(), new CompoundTag());
        if (tag.contains(TAG_TASKS)) {
            final mtr.data.RailwayDataRailActionsModule acc = railwayData.railwayDataRailActionsModule;
            final List<Task> tasks = new ArrayList<>();
            final CompoundTag tasksTag = tag.getCompoundOrEmpty(TAG_TASKS);
            for (String key : tasksTag.keySet()) {
                final CompoundTag taskTag = tasksTag.getCompoundOrEmpty(key);
                final String type = taskTag.getStringOr(Task.TAG_TYPE, "");
                if (type.equals(SliceTask.TYPE)) {
                    tasks.add(new SliceTask(taskTag));
                } else if (type.equals(RailModifierTask.TYPE)) {
                    tasks.add(new RailModifierTask(taskTag));
                } else {
                    player.sendOverlayMessage(Text.translatable("gui.mtrsteamloco.unknown_task_type", type));
                }
            }
            tasks.sort(Comparator.comparingInt(task -> task.order));
            for (Task task : tasks) {
                if (task instanceof SliceTask sliceTask) {
                    if (railwayData.containsRail(posStart, posEnd)) {
                        acc.getRailActions().add(new SliceAction(acc.getWorld(), player, acc.getRails().get(posStart).get(posEnd), sliceTask));
                    } else {
                        player.sendOverlayMessage(Text.translatable("gui.mtr.rail_not_found_action"));
                    }
                } else if (task instanceof RailModifierTask railModifierTask) {
                    final boolean result = railModifier(world, transportMode, stateStart, stateEnd, posStart, posEnd, facingStart, facingEnd, player, railwayData, railModifierTask.rail, railModifierTask.railType, railModifierTask.isOneWay, railModifierTask.isReversed);
                    Main.LOGGER.info("RailModifierTask result: " + result);
                } else {
                    Main.LOGGER.error("Unknown task type: " + task.name);
                }
            }
            acc.sendUpdateS2C();
        } else {
            player.sendOverlayMessage(Text.translatable("gui.mtrsteamloco.no_tasks_found"));
        }
    }

    private static boolean railModifier(Level world, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, RailAngle facingStart, RailAngle facingEnd, Player player, RailwayData railwayData, Rail baseRail, RailType overrideRailType, boolean isOneWay, boolean isReversed) {
        final RailType railType = overrideRailType == null ? baseRail.railType : overrideRailType;
        if (railType.hasSavedRail && (railwayData.hasSavedRail(posStart) || railwayData.hasSavedRail(posEnd))) {
            if (player != null) {
                player.sendOverlayMessage(Text.translatable("gui.mtr.platform_or_siding_exists"));
            }
            return false;
        }

        final boolean isValidContinuousMovement;
        final RailType newRailType;
        if (transportMode.continuousMovement) {
            final Block blockStart = stateStart.getBlock();
            final Block blockEnd = stateEnd.getBlock();

            if (blockStart instanceof BlockNode.BlockContinuousMovementNode && blockEnd instanceof BlockNode.BlockContinuousMovementNode) {
                if (((BlockNode.BlockContinuousMovementNode) blockStart).isStation && ((BlockNode.BlockContinuousMovementNode) blockEnd).isStation) {
                    isValidContinuousMovement = true;
                    newRailType = railType.hasSavedRail ? railType : RailType.CABLE_CAR_STATION;
                } else {
                    final int differenceX = posEnd.getX() - posStart.getX();
                    final int differenceZ = posEnd.getZ() - posStart.getZ();
                    isValidContinuousMovement = !railType.hasSavedRail && facingStart.isParallel(facingEnd)
                            && ((facingStart.equals(RailAngle.N) || facingStart.equals(RailAngle.S)) && differenceX == 0
                            || (facingStart.equals(RailAngle.E) || facingStart.equals(RailAngle.W)) && differenceZ == 0
                            || (facingStart.equals(RailAngle.NE) || facingStart.equals(RailAngle.SW)) && differenceX == -differenceZ
                            || (facingStart.equals(RailAngle.SE) || facingStart.equals(RailAngle.NW)) && differenceX == differenceZ);
                    newRailType = RailType.CABLE_CAR;
                }
            } else {
                isValidContinuousMovement = false;
                newRailType = railType;
            }
        } else {
            isValidContinuousMovement = true;
            newRailType = railType;
        }

        final RailType p1, p2;
        if (isReversed) {
            p2 = newRailType;
            p1 = isOneWay ? RailType.NONE : newRailType;
        } else {
            p1 = newRailType;
            p2 = isOneWay ? RailType.NONE : newRailType;
        }
        final Rail rail1 = new Rail(posStart, facingStart, posEnd, facingEnd, p1, transportMode);
        final Rail rail2 = new Rail(posEnd, facingEnd, posStart, facingStart, p2, transportMode);

        final boolean goodRadius = rail1.goodRadius() && rail2.goodRadius();
        final boolean isValid = rail1.isValid() && rail2.isValid();

        if (goodRadius && isValid && isValidContinuousMovement) {
            final RailExtraSupplier rail1Extra = (RailExtraSupplier) (Object) rail1;
            final RailExtraSupplier rail2Extra = (RailExtraSupplier) (Object) rail2;
            rail1Extra.partialCopyFrom(baseRail);
            rail2Extra.partialCopyFrom(baseRail);
            rail1Extra.setRenderReversed(!isReversed);
            rail2Extra.setRenderReversed(isReversed);

            railwayData.addRail(player, transportMode, posStart, posEnd, rail1, false);
            final long newId = railwayData.addRail(player, transportMode, posEnd, posStart, rail2, true);
            world.setBlockAndUpdate(posStart, stateStart.setValue(BlockNode.IS_CONNECTED, true));
            world.setBlockAndUpdate(posEnd, stateEnd.setValue(BlockNode.IS_CONNECTED, true));
            PacketTrainDataGuiServer.createRailS2C(world, transportMode, posStart, posEnd, rail1, rail2, newId);
            return true;
        } else if (player != null) {
            player.sendOverlayMessage(Text.translatable(isValidContinuousMovement ? goodRadius ? "gui.mtr.invalid_orientation" : "gui.mtr.radius_too_small" : "gui.mtr.cable_car_invalid_orientation"));
        }
        return false;
    }

    public static class Task {
        public static final String TAG_TYPE = "type";
        public static final String TAG_ORDER = "order";
        public static final String TAG_NAME = "name";

        public int order;
        public String name;

        public Task(int order, String name) {
            this.order = order;
            this.name = name;
        }

        public Task(CompoundTag compoundTag) {
            order = compoundTag.getIntOr(TAG_ORDER, 0);
            name = compoundTag.getStringOr(TAG_NAME, "");
        }

        public Task(Task other) {
            order = other.order;
            name = other.name;
        }

        public void copyFrom(Task other) {
            order = other.order;
            name = other.name;
        }

        public CompoundTag toCompoundTag() {
            final CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt(TAG_ORDER, order);
            compoundTag.putString(TAG_NAME, name);
            return compoundTag;
        }
    }

    public static class SliceTask extends Task {
        public static final String TYPE = "Slice";

        public int width;
        public int height;
        public double start;
        public Double length;
        public Double interval;
        public double increment;
        public List<Lump> lumps;
        public boolean useYaw;
        public boolean usePitch;
        public boolean useRoll;

        public static final String TAG_HEIGHT = "height";
        public static final String TAG_LENGTH = "length";
        public static final String TAG_START = "start";
        public static final String TAG_WIDTH = "width";
        public static final String TAG_INTERVAL = "interval";
        public static final String TAG_INCREMENT = "increment";
        public static final String TAG_LUMPS = "lumps";
        public static final String TAG_USE_YAW = "use_yaw";
        public static final String TAG_USE_PITCH = "use_pitch";
        public static final String TAG_USE_ROLL = "use_roll";

        public SliceTask() {
            super(0, TYPE);
            this.width = 11;
            this.height = 11;
            this.start = 0;
            this.length = null;
            this.interval = null;
            this.increment = 0.1;
            this.lumps = new ArrayList<>();
            for (int i = 0; i < this.width * this.height; i++) {
                lumps.add(new Lump(null, true));
            }
            this.useYaw = true;
            this.usePitch = true;
            this.useRoll = true;
        }

        public SliceTask(SliceTask other) {
            super(other.order, other.name);
            this.width = other.width;
            this.height = other.height;
            this.start = other.start;
            this.length = other.length;
            this.interval = other.interval;
            this.increment = other.increment;
            this.lumps = Lump.copyFrom(other.lumps);
            this.useYaw = other.useYaw;
            this.usePitch = other.usePitch;
            this.useRoll = other.useRoll;
        }

        public SliceTask(int order, String name, int width, int height, double start, Double length, Double interval, double increment, List<Lump> lumps, boolean useYaw, boolean usePitch, boolean useRoll) {
            super(order, name);
            this.width = width;
            this.height = height;
            this.start = start;
            this.length = length;
            this.interval = interval;
            this.increment = increment;
            this.lumps = lumps;
            this.useYaw = useYaw;
            this.usePitch = usePitch;
            this.useRoll = useRoll;
        }

        public SliceTask(CompoundTag compoundTag) {
            super(compoundTag);
            this.width = compoundTag.getIntOr(TAG_WIDTH, 1);
            this.height = compoundTag.getIntOr(TAG_HEIGHT, 1);
            this.start = compoundTag.getDoubleOr(TAG_START, 0);
            this.length = compoundTag.contains(TAG_LENGTH) ? compoundTag.getDoubleOr(TAG_LENGTH, 0) : null;
            this.interval = compoundTag.contains(TAG_INTERVAL) ? compoundTag.getDoubleOr(TAG_INTERVAL, 0) : null;
            this.increment = compoundTag.getDoubleOr(TAG_INCREMENT, 0.1);
            this.lumps = Lump.fromByteArray(compoundTag.getByteArray(TAG_LUMPS).orElse(new byte[0]));
            if (this.lumps.size() != this.width * this.height) {
                this.lumps = new ArrayList<>();
                for (int i = 0; i < this.width * this.height; i++) {
                    this.lumps.add(new Lump(null, true));
                }
            }
            this.useYaw = compoundTag.getBooleanOr(TAG_USE_YAW, true);
            this.usePitch = compoundTag.getBooleanOr(TAG_USE_PITCH, true);
            this.useRoll = compoundTag.getBooleanOr(TAG_USE_ROLL, false);
        }

        public boolean setWidthAndHeight(int width, int height) {
            if (width < 1 || height < 1) return false;
            if (width % 2 != 1 || height % 2 != 1) return false;
            if (width == this.width && height == this.height) return false;

            final List<Lump> lumps = new ArrayList<>();
            for (int i = 0; i < width * height; i++) lumps.add(new Lump(null, true));
            final int thiMidX = width / 2;
            final int thiMidY = height / 2;
            final int oldMidX = this.width / 2;
            final int oldMidY = this.height / 2;

            for (int i = -thiMidY; i <= thiMidY; i++) {
                final int thiy = thiMidY + i;
                final int oldy = oldMidY + i;
                if (oldy < 0 || oldy >= this.height) continue;
                for (int j = -thiMidX; j <= thiMidX; j++) {
                    final int thix = thiMidX + j;
                    final int oldx = oldMidX + j;
                    if (oldx < 0 || oldx >= this.width) continue;
                    lumps.set(thiy * width + thix, this.lumps.get(oldy * this.width + oldx));
                }
            }
            this.width = width;
            this.height = height;
            this.lumps = lumps;
            return true;
        }

        public void copyFrom(SliceTask other) {
            super.copyFrom(other);
            width = other.width;
            height = other.height;
            start = other.start;
            length = other.length;
            interval = other.interval;
            increment = other.increment;
            lumps = Lump.copyFrom(other.lumps);
            useYaw = other.useYaw;
            usePitch = other.usePitch;
            useRoll = other.useRoll;
        }

        @Override
        public CompoundTag toCompoundTag() {
            final CompoundTag compoundTag = super.toCompoundTag();
            compoundTag.putString(TAG_TYPE, TYPE);
            compoundTag.putInt(TAG_WIDTH, width);
            compoundTag.putInt(TAG_HEIGHT, height);
            compoundTag.putDouble(TAG_START, start);
            if (length != null) {
                compoundTag.putDouble(TAG_LENGTH, length);
            }
            compoundTag.putDouble(TAG_INCREMENT, increment);
            if (interval != null) {
                compoundTag.putDouble(TAG_INTERVAL, interval);
            }
            compoundTag.putByteArray(TAG_LUMPS, Lump.toByteArray(lumps));
            compoundTag.putBoolean(TAG_USE_YAW, useYaw);
            compoundTag.putBoolean(TAG_USE_PITCH, usePitch);
            compoundTag.putBoolean(TAG_USE_ROLL, useRoll);
            return compoundTag;
        }
    }

    public static class Lump {
        public BlockState blockState;
        public boolean replacement;

        public Lump(Lump other) {
            this(other.blockState, other.replacement);
        }

        public Lump(BlockState blockState, boolean replacement) {
            this.blockState = blockState;
            this.replacement = replacement;
        }

        public static byte[] toByteArray(List<Lump> lumps) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(bos);
            try {
                dos.writeInt(lumps.size());
                for (Lump lump : lumps) {
                    final boolean hasState = lump.blockState != null;
                    dos.writeBoolean(hasState);
                    if (hasState) dos.writeInt(Block.getId(lump.blockState));
                    dos.writeBoolean(lump.replacement);
                }
            } catch (IOException e) {
                Main.LOGGER.error("Failed to serialize lumps", e);
            }
            return bos.toByteArray();
        }

        public static List<Lump> fromByteArray(byte[] bytes) {
            final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            final DataInputStream dis = new DataInputStream(bis);
            final List<Lump> lumps = new ArrayList<>();
            try {
                final int size = dis.readInt();
                for (int i = 0; i < size; i++) {
                    final boolean hasState = dis.readBoolean();
                    final BlockState blockState = hasState ? Block.stateById(dis.readInt()) : null;
                    lumps.add(new Lump(blockState, dis.readBoolean()));
                }
            } catch (IOException e) {
                Main.LOGGER.error("Failed to deserialize lumps", e);
            }
            return lumps;
        }

        public static List<Lump> copyFrom(List<Lump> lumps) {
            final List<Lump> result = new ArrayList<>(lumps.size());
            for (Lump lump : lumps) {
                result.add(new Lump(lump));
            }
            return result;
        }
    }

    public static class RailModifierTask extends Task {
        public static final String TYPE = "Modifier";

        public Rail rail;
        public RailType railType;
        public boolean isOneWay;
        public boolean isReversed;

        public static final String TAG_RAIL = "rail";
        public static final String TAG_RAIL_TYPE = "rail_type";
        public static final String TAG_IS_ONE_WAY = "is_one_way";
        public static final String TAG_IS_REVERSED = "is_reversed";

        public RailModifierTask() {
            super(0, TYPE);
            this.rail = new Rail(new BlockPos(0, -1145, 0), RailAngle.N, new BlockPos(0, -1145, 10), RailAngle.S, RailType.IRON, TransportMode.TRAIN);
            this.railType = this.rail.railType;
            this.isOneWay = false;
            this.isReversed = false;
            tryCallRailScript();
        }

        public RailModifierTask(int order, String name, Rail rail, boolean isOneWay, boolean isReversed) {
            super(order, name);
            this.rail = rail;
            this.railType = rail.railType;
            this.isOneWay = isOneWay;
            this.isReversed = isReversed;
            tryCallRailScript();
        }

        public RailModifierTask(CompoundTag compoundTag) {
            super(compoundTag);
            Rail parsedRail;
            try {
                final ByteBuf buf = Unpooled.wrappedBuffer(compoundTag.getByteArray(TAG_RAIL).orElse(new byte[0]));
                final FriendlyByteBuf friendlyBuf = new FriendlyByteBuf(buf);
                parsedRail = new Rail(friendlyBuf);
                friendlyBuf.release();
            } catch (Exception e) {
                Main.LOGGER.error("Failed to deserialize rail of RailModifierTask", e);
                parsedRail = new Rail(new BlockPos(0, -1145, 0), RailAngle.N, new BlockPos(0, -1145, 10), RailAngle.S, RailType.IRON, TransportMode.TRAIN);
            }
            this.rail = parsedRail;
            final String railTypeName = compoundTag.getStringOr(TAG_RAIL_TYPE, "");
            RailType parsedRailType = null;
            try {
                parsedRailType = RailType.valueOf(railTypeName);
            } catch (Exception ignored) {
            }
            this.railType = parsedRailType == null ? this.rail.railType : parsedRailType;
            this.isOneWay = compoundTag.getBooleanOr(TAG_IS_ONE_WAY, false);
            this.isReversed = compoundTag.getBooleanOr(TAG_IS_REVERSED, false);
            tryCallRailScript();
        }

        public RailModifierTask(RailModifierTask other) {
            super(other.order, other.name);
            this.rail = copyRail(other.rail);
            this.railType = other.railType;
            this.isOneWay = other.isOneWay;
            this.isReversed = other.isReversed;
        }

        private static Rail copyRail(Rail rail) {
            final ByteBuf buf = Unpooled.buffer();
            final FriendlyByteBuf friendlyBuf = new FriendlyByteBuf(buf);
            rail.writePacket(friendlyBuf);
            final Rail copy = new Rail(friendlyBuf);
            friendlyBuf.release();
            return copy;
        }

        public void tryCallRailScript() {
            final List<cn.zbx1425.mtrsteamloco.data.RailModelRepeater> repeaters = ((RailExtraSupplier) (Object) rail).getRepeaters();
            for (cn.zbx1425.mtrsteamloco.data.RailModelRepeater repeater : repeaters) {
                final String key = repeater.getPrimaryModelTypeKey();
                if (key.isEmpty() || key.equals("null")) continue;
                final RailModelProperties prop = RailModelRegistry.getProperty(key);
                if (prop == null) continue;
                // This fork has no per-model scripts; the model is rendered straight from the registry.
            }
        }

        public void copyFrom(RailModifierTask other) {
            super.copyFrom(other);
            this.rail = copyRail(other.rail);
            this.railType = other.railType;
            isOneWay = other.isOneWay;
            isReversed = other.isReversed;
        }

        @Override
        public CompoundTag toCompoundTag() {
            final CompoundTag compoundTag = super.toCompoundTag();
            compoundTag.putString(TAG_TYPE, TYPE);
            final ByteBuf buf = Unpooled.buffer();
            final FriendlyByteBuf friendlyBuf = new FriendlyByteBuf(buf);
            rail.writePacket(friendlyBuf);
            compoundTag.putByteArray(TAG_RAIL, ByteBufUtil.getBytes(buf));
            friendlyBuf.release();
            compoundTag.putString(TAG_RAIL_TYPE, railType == null ? "" : railType.name());
            compoundTag.putBoolean(TAG_IS_ONE_WAY, isOneWay);
            compoundTag.putBoolean(TAG_IS_REVERSED, isReversed);
            return compoundTag;
        }
    }

    public static class SliceAction extends Rail.RailActions {
        public final SliceTask task;
        public double[] starts;
        public int index;
        private int width;
        private int height;
        private double distance;
        private final Level world;
        private final UUID uuid;
        private final String playerName;
        private final Rail rail;
        private final double length;
        private final Set<BlockPos> blacklistedPos = new HashSet<>();
        private Vec3 last;

        private final double INCREMENT;

        public SliceAction(Level world, Player player, Rail rail, SliceTask task) {
            super(world, player, null, rail, 0, 0, null);
            this.world = world;
            uuid = player.getUUID();
            playerName = player.getName().getString();
            this.rail = rail;
            this.task = task;
            index = 0;
            length = rail.getLength();
            width = task.width;
            height = task.height;
            INCREMENT = Math.max(task.increment, 0.1D);

            if (task.interval == null || task.length == null) {
                starts = new double[]{task.start};
            } else {
                double temp = task.start;
                final double count = task.length + task.interval;
                final List<Double> list = new ArrayList<>();
                while (temp + count < length) {
                    list.add(temp);
                    temp += count;
                }
                starts = list.stream().mapToDouble(Double::doubleValue).toArray();
            }
            if (starts.length == 0) starts = new double[]{task.start};
            distance = starts[0];
            last = rail.getPosition(starts[0]);
        }

        @Override
        public boolean build() {
            final long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 2) {
                if (index >= starts.length) {
                    showProgressMessage(100);
                    return true;
                }

                if (distance >= starts[index] + (task.length == null ? length : task.length)) {
                    index++;
                    if (index >= starts.length) {
                        showProgressMessage(100);
                        return true;
                    }
                    distance = starts[index];
                    last = rail.getPosition(starts[index]);
                    continue;
                }
                distance += INCREMENT;
                if (distance >= (length - 0.1)) {
                    showProgressMessage(100);
                    return true;
                }
                final Vec3 next = rail.getPosition(distance);

                final Matrix4f mat = new Matrix4f();
                float yaw = 0;
                mat.translate((float) last.x, (float) last.y, (float) last.z);
                if (task.useYaw) {
                    yaw = (float) Mth.atan2(next.x - last.x, next.z - last.z);
                    mat.rotateY(yaw);
                }
                if (task.usePitch) {
                    final float pitch = (float) Mth.atan2(next.y - last.y, (float) Math.sqrt((next.x - last.x) * (next.x - last.x) + (next.z - last.z) * (next.z - last.z)));
                    mat.rotateX(pitch);
                }
                if (task.useRoll) {
                    final float roll = RailExtraSupplier.getRollAngle(rail, distance - INCREMENT / 2);
                    mat.rotateZ(roll);
                }

                last = next;

                mat.translate(width / 2.0F - 0.5F, height / 2.0F - 0.5F, 0);

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        final int index = i * width + j;
                        if (index >= task.lumps.size()) break;
                        final Lump lump = task.lumps.get(index);
                        BlockState state = lump.blockState;
                        final Vector3f pos = mat.getTranslationPart();
                        mat.translate(-1.0F, 0, 0);
                        final BlockPos blockPos = new BlockPos((int) Math.floor(pos.x()), (int) Math.floor(pos.y()), (int) Math.floor(pos.z()));
                        if (!world.getBlockState(blockPos).isAir() && !lump.replacement) continue;
                        if (state == null) continue;
                        if (blacklistedPos.contains(blockPos)) continue;
                        if (!canPlace(world, blockPos)) continue;

                        if (state.hasProperty(BlockStateProperties.FACING)) {
                            state = state.setValue(BlockStateProperties.FACING, rotateDirection(state.getValue(BlockStateProperties.FACING), yaw));
                        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotateDirection(state.getValue(BlockStateProperties.HORIZONTAL_FACING), yaw));
                        } else if (state.hasProperty(BlockStateProperties.FACING_HOPPER)) {
                            state = state.setValue(BlockStateProperties.FACING_HOPPER, rotateDirection(state.getValue(BlockStateProperties.FACING_HOPPER), yaw));
                        }
                        world.setBlockAndUpdate(blockPos, state);
                        blacklistedPos.add(blockPos);
                    }
                    mat.translate(width, -1.0F, 0);
                }
            }

            showProgressMessage(RailwayData.round(100 * distance / length, 1));
            return false;
        }

        private Direction rotateDirection(Direction dir, float yaw) {
            if (dir == Direction.UP || dir == Direction.DOWN) return dir;
            final double d = dir.toYRot() + Math.toDegrees(yaw) + 180;
            return Direction.fromYRot(d);
        }

        private void showProgressMessage(float percentage) {
            final Player player = world.getPlayerByUUID(uuid);
            if (player != null) {
                player.sendOverlayMessage(Text.translatable("gui.mtrsteamloco.percentage_complete_slice", percentage));
            }
        }

        private static boolean canPlace(Level world, BlockPos pos) {
            return world.getBlockEntity(pos) == null && !(world.getBlockState(pos).getBlock() instanceof BlockNode);
        }

        @Override
        public void writePacket(FriendlyByteBuf packet) {
            packet.writeLong(id);
            packet.writeUtf(playerName);
            packet.writeFloat(RailwayData.round(length, 1));
            packet.writeUtf("gui.mtrsteamloco.compound_creator.slice_task");
            packet.writeUtf("rail_action_slice");
            packet.writeInt(0x47fec4);
        }
    }
}
