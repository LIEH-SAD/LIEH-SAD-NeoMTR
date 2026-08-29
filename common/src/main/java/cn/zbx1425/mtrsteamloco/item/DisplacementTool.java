package cn.zbx1425.mtrsteamloco.item;

import cn.zbx1425.mtrsteamloco.mixin.RailAccessor;
import cn.zbx1425.mtrsteamloco.mixin.RailwayDataAccessor;
import mtr.block.BlockNode;
import mtr.data.Rail;
import mtr.data.RailAngle;
import mtr.data.RailwayData;
import mtr.data.TransportMode;
import mtr.item.ItemNodeModifierBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DisplacementTool extends ItemNodeModifierBase {


    public DisplacementTool(Item.Properties properties) {
        super(properties, true, true, true, false);
    }

    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        Level world = ctx.getLevel();
        if (player == null || world == null) return InteractionResult.PASS;
        BlockPos pos = ctx.getClickedPos();
        BlockState blockState = world.getBlockState(pos);
        if (!(blockState.getBlock() instanceof BlockNode)) return InteractionResult.PASS;

        RailwayData railwayData = RailwayData.getInstance(world);

        if (railwayData == null) return InteractionResult.PASS;

        Map<BlockPos, Map<BlockPos, Rail>> railMap = ((RailwayDataAccessor) railwayData).getRails();

        if (railMap.get(pos) == null) return InteractionResult.SUCCESS;

        Optional<Map.Entry<BlockPos, Rail>> closestEntry = railMap.get(pos).entrySet().stream().min(Comparator.comparingDouble(entry ->
                Mth.degreesDifferenceAbs((float) -Math.toDegrees(Math.atan2(entry.getKey().getX() - pos.getX(), entry.getKey().getZ() - pos.getZ())), player.getYRot())
        ));
        if (closestEntry.isEmpty()) return InteractionResult.SUCCESS;
        BlockPos target = closestEntry.get().getKey();
        Rail rail = closestEntry.get().getValue();
        RailAccessor ra = (RailAccessor) rail;

        Vec3 playerPos = player.getPosition(1);
        Vec3 start = rail.getPosition(0);
        Vec3 diff = playerPos.subtract(start);

        double rot = ra.invokeGetRailAngle(true).getOpposite().angleRadians - ra.invokeGetRailAngle(false).angleRadians;

        diff = diff.yRot((float) -rot);

        Vec3 t = diff.add(rail.getPosition(rail.getLength()));

        // Clamp the destination to the world's buildable height limit so the player is never
        // teleported above it. Otherwise the vanilla server rejects the next item use with the
        // misleading "build.tooHigh" ("超出建筑高度为319格") overlay.
        final Vec3 destination = new Vec3(t.x, Math.min(t.y, world.getMaxY() + 1), t.z);

        rot = Math.toDegrees(rot);

        if (player instanceof ServerPlayer sp) {
            if (world instanceof ServerLevel sw) {
                // Skip if the player is already at the destination: a redundant teleport would
                // put the server into a pending-teleport state that makes the next right-click
                // get rejected with "build.tooHigh" before the client confirms the new position.
                if (destination.distanceToSqr(player.getPosition(1)) < 1e-4) {
                    return InteractionResult.SUCCESS;
                }
                final float fr = (float) rot;
                sw.getServer().execute(() -> {
                    sp.teleportTo(sw, destination.x, destination.y, destination.z, Set.of(), player.getYRot() + fr, player.getXRot(), false);
                });
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onConnect(Level world, ItemStack stack, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, RailAngle facingStart, RailAngle facingEnd, Player player, RailwayData railwayData) {
    }

    @Override
    protected void onRemove(Level world, BlockPos posStart, BlockPos posEnd, Player player, RailwayData railwayData) {
    }
}