package com.lx862.jcm.mod.block.entity;

import com.lx862.jcm.mod.data.JCMServerStats;
import com.lx862.jcm.mod.registry.BlockEntities;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.atomic.AtomicBoolean;

public class AutoIronDoorBlockEntity extends JCMBlockEntity {
    public static final int DETECT_RADIUS = 3;

    public AutoIronDoorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.AUTO_IRON_DOOR.get(), blockPos, blockState);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if(world != null && !world.isClientSide() && JCMServerStats.getGameTick() % 5 == 0) {
            AABB box = new AABB(pos.getX() - DETECT_RADIUS, pos.getY() - DETECT_RADIUS, pos.getZ() - DETECT_RADIUS, pos.getX() + DETECT_RADIUS, pos.getY() + DETECT_RADIUS, pos.getZ() + DETECT_RADIUS);
            AtomicBoolean haveNearbyPlayer = new AtomicBoolean(false);

            for(Player player : world.players()) {
                if(box.contains(player.position())) {
                    boolean alreadyOpened = IBlock.getStatePropertySafe(state, DoorBlock.OPEN);

                    if(!alreadyOpened) {
                        world.playSound(null, pos, SoundEvent.createVariableRangeEvent(Identifier.parse("minecraft:block.iron_door.open")), SoundSource.BLOCKS, 1, 1);
                        world.setBlockAndUpdate(pos, state.setValue(DoorBlock.OPEN, true));
                    }
                    haveNearbyPlayer.set(true);
                }
            }

            if(!haveNearbyPlayer.get() && IBlock.getStatePropertySafe(state, DoorBlock.OPEN)) {
                world.playSound(null, pos, SoundEvent.createVariableRangeEvent(Identifier.parse("minecraft:block.iron_door.close")), SoundSource.BLOCKS, 1, 1);
                world.setBlockAndUpdate(pos, state.setValue(DoorBlock.OPEN, false));
            }
        }
    }
}