package mtr.util;

import com.mojang.blaze3d.platform.NativeImage;
import mtr.MTR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ChunkThumbnailCache {

	private static final int CHUNK_SIZE = 16;
	private static final int SAVE_PER_SCAN = 2;
	private static final int SCAN_INTERVAL_MS = 2000;
	private static final int SAVE_RADIUS_CHUNKS = 8;

	private static final Map<Long, Identifier> THUMBNAILS = new ConcurrentHashMap<>();
	private static final Set<Long> KNOWN = ConcurrentHashMap.newKeySet();
	private static final Map<Long, Boolean> SAVED = new ConcurrentHashMap<>();
	private static long lastScanTime = 0;
	private static String currentWorldKey = null;
	private static int lastScanPlayerChunkX = Integer.MIN_VALUE;
	private static int lastScanPlayerChunkZ = Integer.MIN_VALUE;

	public static Identifier getThumbnail(int chunkX, int chunkZ) {
		return THUMBNAILS.get(pack(chunkX, chunkZ));
	}

	public static void scanFiles() {
		releaseTextures();
		THUMBNAILS.clear();
		KNOWN.clear();
		final Path dir = getThumbnailDir();
		if (dir == null || !Files.exists(dir)) {
			return;
		}
		try (Stream<Path> stream = Files.list(dir)) {
			stream.forEach(path -> {
				final Long key = parseKey(path.getFileName().toString());
				if (key != null) {
					KNOWN.add(key);
				}
			});
		} catch (IOException e) {
			MTR.LOGGER.error("Failed to list chunk thumbnails", e);
		}
	}

	public static Identifier loadThumbnail(int chunkX, int chunkZ) {
		final long key = pack(chunkX, chunkZ);
		if (!KNOWN.contains(key) || THUMBNAILS.containsKey(key)) {
			return null;
		}
		final Path file = getChunkFile(chunkX, chunkZ);
		if (file == null || !Files.exists(file)) {
			return null;
		}
		try {
			final Identifier id = registerTexture(key, NativeImage.read(Files.newInputStream(file)));
			THUMBNAILS.put(key, id);
			return id;
		} catch (Exception e) {
			MTR.LOGGER.error("Failed to load chunk thumbnail " + file, e);
			return null;
		}
	}

	public static Identifier generateThumbnail(int chunkX, int chunkZ) {
		final long key = pack(chunkX, chunkZ);
		final Identifier existing = THUMBNAILS.get(key);
		if (existing != null) {
			return existing;
		}
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return null;
		}
		try {
			final NativeImage image = generateImage(chunkX, chunkZ, minecraft.level);
			final Path file = getChunkFile(chunkX, chunkZ);
			if (file != null) {
				Files.createDirectories(file.getParent());
				image.writeToFile(file);
			}
			final Identifier id = registerTexture(key, image);
			THUMBNAILS.put(key, id);
			return id;
		} catch (Exception e) {
			MTR.LOGGER.error("Failed to generate chunk thumbnail", e);
			return null;
		}
	}

	public static void tickSave() {
		final Minecraft minecraft = Minecraft.getInstance();
		final ClientLevel world = minecraft.level;
		if (world == null || minecraft.player == null) {
			return;
		}
		final String worldKey = getWorldKey(minecraft);
		if (!Objects.equals(worldKey, currentWorldKey)) {
			currentWorldKey = worldKey;
			SAVED.clear();
		}
		final long now = System.currentTimeMillis();
		if (now - lastScanTime < SCAN_INTERVAL_MS) {
			return;
		}
		lastScanTime = now;

		final int playerChunkX = minecraft.player.blockPosition().getX() >> 4;
		final int playerChunkZ = minecraft.player.blockPosition().getZ() >> 4;
		if (playerChunkX == lastScanPlayerChunkX && playerChunkZ == lastScanPlayerChunkZ) {
			return;
		}
		lastScanPlayerChunkX = playerChunkX;
		lastScanPlayerChunkZ = playerChunkZ;

		int savedCount = 0;
		for (int radius = 0; radius <= SAVE_RADIUS_CHUNKS && savedCount < SAVE_PER_SCAN; radius++) {
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dz = -radius; dz <= radius; dz++) {
					if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
						continue;
					}
					final int chunkX = playerChunkX + dx;
					final int chunkZ = playerChunkZ + dz;
					final long key = pack(chunkX, chunkZ);
					if (!SAVED.containsKey(key) && world.hasChunk(chunkX, chunkZ)) {
						SAVED.put(key, Boolean.TRUE);
						try {
							final Path file = getChunkFile(chunkX, chunkZ);
							if (file != null && !Files.exists(file)) {
								final NativeImage image = generateImage(chunkX, chunkZ, world);
								Files.createDirectories(file.getParent());
								image.writeToFile(file);
								image.close();
								savedCount++;
							}
						} catch (Exception e) {
							MTR.LOGGER.error("Failed to save chunk thumbnail", e);
						}
						if (savedCount >= SAVE_PER_SCAN) {
							break;
						}
					}
				}
				if (savedCount >= SAVE_PER_SCAN) {
					break;
				}
			}
		}
	}

	private static NativeImage generateImage(int chunkX, int chunkZ, Level world) {
		final NativeImage image = new NativeImage(NativeImage.Format.RGBA, CHUNK_SIZE, CHUNK_SIZE, false);
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				final int worldX = chunkX * CHUNK_SIZE + x;
				final int worldZ = chunkZ * CHUNK_SIZE + z;
				final int color = divideColorRGB(world.getBlockState(new BlockPos(worldX, world.getHeight(Heightmap.Types.MOTION_BLOCKING, worldX, worldZ) - 1, worldZ)).getBlock().defaultMapColor().col, 2);
				image.setPixel(x, z, 0xFF000000 | color);
			}
		}
		return image;
	}

	private static Identifier registerTexture(long key, NativeImage image) {
		final Identifier id = MTR.id("chunk_thumbnail_" + (key >> 32) + "_" + (key & 0xFFFFFFFFL));
		Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(id::toString, image));
		return id;
	}

	private static void releaseTextures() {
		final TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		for (final Identifier id : THUMBNAILS.values()) {
			textureManager.release(id);
		}
	}

	private static Path getChunkFile(int chunkX, int chunkZ) {
		final Path dir = getThumbnailDir();
		return dir == null ? null : dir.resolve("chunk_" + chunkX + "_" + chunkZ + ".png");
	}

	private static Path getThumbnailDir() {
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return null;
		}
		final Path base = minecraft.gameDirectory.toPath().resolve("config").resolve("mtr").resolve("map");
		return base.resolve(sanitize(getWorldKey(minecraft))).resolve(sanitize(minecraft.level.dimension().identifier().toString()));
	}

	private static String getWorldKey(Minecraft minecraft) {
		if (minecraft.getSingleplayerServer() != null) {
			return minecraft.getSingleplayerServer().getWorldData().getLevelName();
		}
		final ServerData serverData = minecraft.getCurrentServer();
		return serverData == null ? "unknown" : serverData.ip;
	}

	private static String sanitize(String s) {
		return s.replaceAll("[^a-zA-Z0-9_.-]", "_");
	}

	private static Long parseKey(String name) {
		if (!name.startsWith("chunk_") || !name.endsWith(".png")) {
			return null;
		}
		try {
			final String[] parts = name.substring("chunk_".length(), name.length() - ".png".length()).split("_");
			return pack(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		} catch (Exception e) {
			return null;
		}
	}

	private static int divideColorRGB(int color, int amount) {
		final int r = ((color >> 16) & 0xFF) / amount;
		final int g = ((color >> 8) & 0xFF) / amount;
		final int b = (color & 0xFF) / amount;
		return (r << 16) + (g << 8) + b;
	}

	private static long pack(int chunkX, int chunkZ) {
		return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
	}
}
