package com.lx862.jcm.mod.resources.mcmeta;

import com.google.gson.JsonParser;
import com.lx862.jcm.mod.data.Pair;
import com.lx862.jcm.mod.util.JCMLogger;
import mtr.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Class to handle McMeta animated textures, as I couldn't figure out how MC does it :D
 */
public class McMetaManager {
    private final HashMap<Identifier, McMeta> mcMetaList = new HashMap<>();

    public void reset() {
        mcMetaList.clear();
    }

    /**
     * Try to load an animated mcmeta file if it exists, will do nothing otherwise
     * @param imagePath The identifier path that leads to the png file
     */
    public void load(Identifier imagePath) {
        if(mcMetaList.containsKey(imagePath)) return;
        Identifier mcmetaFileId = Identifier.fromNamespaceAndPath(imagePath.getNamespace(), imagePath.getPath() + ".mcmeta");
        Optional<Resource> mcmetaFile = Minecraft.getInstance().getResourceManager().getResource(mcmetaFileId);
        if(mcmetaFile.isPresent()) {
            try (InputStream is = Utilities.getInputStream(mcmetaFile)) {
                JCMLogger.debug("[McMetaManager] Loading mcmeta file: " + imagePath.getPath());
                String str = IOUtils.toString(is);
                McMeta mcMeta = McMeta.parse(JsonParser.parseString(str).getAsJsonObject());

                readImage(mcMeta, imagePath, mcMeta1 -> {
                    mcMetaList.put(imagePath, mcMeta1);
                });
            } catch (IOException e) {
                JCMLogger.error("[McMetaManager] Failed to read mcmeta file {}!", imagePath.toString(), e);
            }
        }
    }

    /**
     * Call to increment tick counter to update animated mcmeta texture.
     */
    public void tick() {
        for(McMeta mcMeta : mcMetaList.values()) {
            mcMeta.tick();
        }
    }

    /**
     * The most vital method, it returns the start and end V for use when rendering with UV, so only 1 part of the image will be shown
     * @param id Identifier of the texture
     * @return The starting and the end V, default to 0.0 and 1.0 (Full Texture) if the texture is not a mcmeta animation loaded to this manager
     */
    public Pair<Float, Float> getUV(Identifier id) {
        if(!mcMetaList.containsKey(id)) return new Pair<>(0F, 1F);
        return mcMetaList.get(id).getUV();
    }

    private static void readImage(McMeta mcMeta, Identifier imageFile, Consumer<McMeta> callback) {
        Resource imageResource = Minecraft.getInstance().getResourceManager().getResource(imageFile).orElseThrow();

        try(InputStream is = imageResource.open()) {
            BufferedImage bufferedImage = ImageIO.read(is);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            JCMLogger.debug("[McMetaManager] Loaded mcmeta image metadata: {} ({})", imageFile.getPath(), width + "x" + height);
            mcMeta.setVerticalPart((height / width));
            callback.accept(mcMeta);
        } catch (Exception e) {
            JCMLogger.error( "[McMetaManager] Failed to read image metadata from {}!", imageFile.getPath());
        }
    }
}
