package com.lx862.jcm.mod.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.AbstractCollection;
import java.util.function.Consumer;

public abstract class JCMPacketHandlerHelper {
    public static void readArray(FriendlyByteBuf packetBufferReceiver, Consumer<Integer> callback) {
        int size = packetBufferReceiver.readInt();
        for(int i = 0; i < size; i++) {
            callback.accept(i);
        }
    }

    public static <T> void writeArray(FriendlyByteBuf packetBufferSender, T[] arr, Consumer<Integer> callback) {
        packetBufferSender.writeInt(arr.length);
        for(int i = 0; i < arr.length; i++) {
            callback.accept(i);
        }
    }

    public static void writeArray(FriendlyByteBuf packetBufferSender, boolean[] arr, Consumer<Integer> callback) {
        packetBufferSender.writeInt(arr.length);
        for(int i = 0; i < arr.length; i++) {
            callback.accept(i);
        }
    }

    public static <T> void writeArray(FriendlyByteBuf packetBufferSender, AbstractCollection<T> arr, Consumer<T> callback) {
        packetBufferSender.writeInt(arr.size());
        for(T item : arr) {
            callback.accept(item);
        }
    }
}
