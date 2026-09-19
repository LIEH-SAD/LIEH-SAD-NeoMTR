package com.lx862.jcm.loader;

import com.lx862.jcm.mod.registry.ItemGroups;
import dev.architectury.injectables.annotations.ExpectPlatform;
import mtr.BrandNewEpicRegistryObject;
import mtr.RegistryObject;
import mtr.mappings.NetworkUtilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JCMRegistry {
    @ExpectPlatform
    public static void registerBlock(String id, BrandNewEpicRegistryObject<Block> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerBlockAndItem(String id, BrandNewEpicRegistryObject<Block> block, ItemGroups.Wrapper tab) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerItem(String id, BrandNewEpicRegistryObject<Item> item, ItemGroups.Wrapper creativeTabs) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerBlockEntityType(String id, RegistryObject<? extends BlockEntityType<? extends BlockEntity>> blockEntityType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Supplier<CreativeModeTab> registerCreativeModeTab(Identifier id, Supplier<ItemStack> supplier) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerNetworkPacket(Identifier resourceLocation) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerNetworkReceiver(Identifier resourceLocation, NetworkUtilities.PacketCallback packetCallback) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayer(ServerPlayer player, Identifier id, FriendlyByteBuf packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerTickEvent(Consumer<MinecraftServer> consumer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigPath() {
        throw new AssertionError();
    }
}
