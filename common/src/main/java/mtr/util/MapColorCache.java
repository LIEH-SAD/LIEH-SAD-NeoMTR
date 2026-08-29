package mtr.util;

import mtr.MTR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapColorCache {

	private static final String CACHE_FILE_MAGIC = "MTRMC1";
	private static final Map<Long, Integer> CACHE = new ConcurrentHashMap<>();
	private static boolean dirty = false;

	public static Integer getColor(int x, int z) {
		return CACHE.get(pack(x, z));
	}

	public static void putColor(int x, int z, int color) {
		CACHE.put(pack(x, z), color);
		dirty = true;
	}

	public static void load() {
		CACHE.clear();
		dirty = false;
		final Path file = getCacheFile();
		if (file == null || !Files.exists(file)) {
			return;
		}
		try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
			if (!CACHE_FILE_MAGIC.equals(in.readUTF())) {
				return;
			}
			while (true) {
				try {
					CACHE.put(in.readLong(), in.readInt());
				} catch (EOFException e) {
					break;
				}
			}
		} catch (IOException e) {
			MTR.LOGGER.error("Failed to load map color cache", e);
		}
	}

	public static void save() {
		if (!dirty) {
			return;
		}
		final Path file = getCacheFile();
		if (file == null) {
			return;
		}
		try {
			Files.createDirectories(file.getParent());
			try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
				out.writeUTF(CACHE_FILE_MAGIC);
				for (final Map.Entry<Long, Integer> entry : CACHE.entrySet()) {
					out.writeLong(entry.getKey());
					out.writeInt(entry.getValue());
				}
			}
			dirty = false;
		} catch (IOException e) {
			MTR.LOGGER.error("Failed to save map color cache", e);
		}
	}

	private static Path getCacheFile() {
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return null;
		}
		final String worldKey;
		if (minecraft.getSingleplayerServer() != null) {
			worldKey = minecraft.getSingleplayerServer().getWorldData().getLevelName();
		} else {
			final ServerData serverData = minecraft.getCurrentServer();
			worldKey = serverData == null ? "unknown" : serverData.ip;
		}
		final String fileName = "map_cache_" + sanitize(worldKey) + "_" + sanitize(minecraft.level.dimension().identifier().toString()) + ".txt";
		return minecraft.gameDirectory.toPath().resolve("config").resolve("mtr").resolve(fileName);
	}

	private static String sanitize(String s) {
		return s.replaceAll("[^a-zA-Z0-9_.-]", "_");
	}

	private static long pack(int x, int z) {
		return ((long) x << 32) | (z & 0xFFFFFFFFL);
	}
}
