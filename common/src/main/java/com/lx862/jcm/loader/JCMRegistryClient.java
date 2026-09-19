package com.lx862.jcm.loader;

import dev.architectury.injectables.annotations.ExpectPlatform;
import mtr.BrandNewEpicRegistryObject;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JCMRegistryClient {
    public static void registerStationColoredBlock(BrandNewEpicRegistryObject<Block>... blocks) {
        for (BrandNewEpicRegistryObject<Block> block : blocks) {
            registerBlockColors(block);
        }
    }

    @ExpectPlatform
    public static void registerBlockColors(Supplier<Block> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntityMapper, S extends BlockEntityRenderState> void registerTileEntityRenderer(Supplier<BlockEntityType<T>> type, Function<BlockEntityRenderDispatcher, BlockEntityRendererMapper<T, S>> function) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerKeyBinding(KeyMapping keyMapping) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerNetworkReceiver(Identifier resourceLocation, Consumer<FriendlyByteBuf> consumer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerPlayerJoinEvent(Consumer<LocalPlayer> consumer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerTickEvent(Consumer<Minecraft> consumer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToServer(Identifier id, FriendlyByteBuf packet) {
        throw new AssertionError();
    }
}
