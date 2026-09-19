package com.lx862.mtrscripting.util;

/* From https://github.com/zbx1425/mtr-nte/blob/master/common/src/main/java/cn/zbx1425/mtrsteamloco/render/scripting/ScriptResourceUtil.java#L44 */

import cn.zbx1425.mtrsteamloco.render.integration.MtrModelRegistryUtil;
import cn.zbx1425.mtrsteamloco.render.scripting.util.GraphicsTexture;
import cn.zbx1425.sowcerext.util.ResourceUtil;
import com.lx862.mtrscripting.api.ScriptingAPI;
import com.lx862.mtrscripting.ScriptManager;

import mtr.MTR;
import mtr.util.Utilities;
import mtr.util.UtilitiesClient;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import vendor.cn.zbx1425.mtrsteamloco.org.mozilla.javascript.Context;
import vendor.cn.zbx1425.mtrsteamloco.org.mozilla.javascript.Scriptable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.text.AttributedString;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

@SuppressWarnings("unused")
public class ScriptResourceUtil {
    public static Context activeContext;
    public static Scriptable activeScope;
    private static final Stack<Identifier> scriptLocationStack = new Stack<>();

    public static void executeScript(Context rhinoCtx, Scriptable scope, Identifier scriptLocation, String script) {
        scriptLocationStack.push(scriptLocation);
        rhinoCtx.evaluateString(scope, script, scriptLocation.getNamespace() + ":" + scriptLocation.getPath(), 1, null);
        scriptLocationStack.pop();
    }

    public static void includeScript(Object pathOrIdentifier) throws IOException {
        if (activeContext == null) throw new RuntimeException(
                "Cannot use include in functions, as by that time scripts are no longer processed."
        );
        Identifier identifier;
        if (pathOrIdentifier instanceof Identifier resourceLocation) {
            identifier = resourceLocation;
        } else {
            identifier = idRelative(pathOrIdentifier.toString());
        }
        executeScript(activeContext, activeScope, identifier, ResourceUtil.readResource(manager(), identifier));
    }

    public static void print(Object... objs) {
        StringBuilder sb = new StringBuilder();
        if(objs == null) {
            sb.append("null");
        } else {
            for(Object obj : objs) {
                sb.append(obj);
            }
        }
        ScriptManager.LOGGER.info("[Scripting] {}", sb.toString());
    }

    public static Identifier identifier(String textForm) {
        return Identifier.parse(textForm);
    }

    public static Identifier id(String textForm) {
        return Identifier.parse(textForm);
    }

    public static Identifier idRelative(String textForm) {
        if (scriptLocationStack.empty()) throw new RuntimeException(
                "Cannot use idRelative in functions."
        );
        return resolveRelativePath(scriptLocationStack.peek(), textForm);
    }

    public static Identifier idr(String textForm) {
        if (scriptLocationStack.empty()) throw new RuntimeException(
                "Cannot use idr in functions."
        );
        Identifier id = scriptLocationStack.peek();
        return resolveRelativePath(id, textForm);
    }

    public static InputStream readStream(Identifier identifier) throws IOException {
        final List<Resource> resources = UtilitiesClient.getResources(manager(), identifier);
        if (resources.isEmpty()) throw new FileNotFoundException(identifier.toString());
        return resources.get(0).open();
    }

    public static String readString(Identifier identifier) {
        try {
            return ResourceUtil.readResource(manager(), identifier);
        } catch (IOException e) {
            return null;
        }
    }

    private static final Identifier NOTO_SANS_CJK_LOCATION = MTR.id("font/noto-sans-cjk-tc-medium.otf");
    private static final Identifier NOTO_SANS_LOCATION = MTR.id("font/noto-sans-semibold.ttf");
    private static final Identifier NOTO_SERIF_LOCATION = MTR.id("font/noto-serif-cjk-tc-semibold.ttf");
    private static boolean hasNotoSansCjk = false;
    private static Font NOTO_SANS_MAYBE_CJK;
    private static Font NOTO_SERIF_CACHE;

    public static Font getSystemFont(String fontName) {
        if(fontName.equals("Noto Sans")) {
            if (NOTO_SANS_MAYBE_CJK == null) {
                if (hasNotoSansCjk) {
                    try {
                        NOTO_SANS_MAYBE_CJK = readFont(NOTO_SANS_CJK_LOCATION);
                    } catch (Exception ex) {
                        ScriptManager.LOGGER.warn("[Scripting] Failed to load font", ex);
                    }
                } else {
                    try {
                        NOTO_SANS_MAYBE_CJK = readFont(NOTO_SANS_LOCATION);
                    } catch (Exception ex) {
                        ScriptManager.LOGGER.warn("[Scripting] Failed to load font", ex);
                    }
                }
            }
            return NOTO_SANS_MAYBE_CJK;
        } else if(fontName.equals("Noto Serif")) {
            if(NOTO_SERIF_CACHE == null) {
                try {
                    NOTO_SERIF_CACHE = readFont(NOTO_SERIF_LOCATION);
                } catch (Exception ex) {
                    ScriptManager.LOGGER.warn("[Scripting] Failed loading font", ex);
                    return null;
                }
            }
            return NOTO_SERIF_CACHE;
        } else {
            return new Font(fontName, Font.PLAIN, 1);
        }
    }

    private static final FontRenderContext FONT_CONTEXT = new FontRenderContext(new AffineTransform(), true, false);

    public static FontRenderContext getFontRenderContext() {
        return FONT_CONTEXT;
    }

    public static AttributedString ensureStrFonts(String text, Font font) {
        AttributedString result = new AttributedString(text);
        if (text.isEmpty()) return result;
        result.addAttribute(TextAttribute.FONT, font, 0, text.length());
        for (int characterIndex = 0; characterIndex < text.length(); characterIndex++) {
            final char character = text.charAt(characterIndex);
            if (!font.canDisplay(character)) {
                Font defaultFont = null;
                for (final Font testFont : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
                    if (testFont.canDisplay(character)) {
                        defaultFont = testFont;
                        break;
                    }
                }
                final Font newFont = (defaultFont == null ? new Font(null) : defaultFont)
                        .deriveFont(font.getStyle(), font.getSize2D());
                result.addAttribute(TextAttribute.FONT, newFont, characterIndex, characterIndex + 1);
            }
        }
        return result;
    }

    public static BufferedImage readBufferedImage(Identifier identifier) throws IOException {
        try (InputStream is = readStream(identifier)) {
            return GraphicsTexture.createArgbBufferedImage(ImageIO.read(is));
        }
    }

    public static Font readFont(Identifier identifier) throws IOException, FontFormatException {
        try (InputStream is = readStream(identifier)) {
            return Font.createFont(Font.TRUETYPE_FONT, is);
        }
    }

    public static String getAddonVersion(String modid) {
        return ScriptingAPI.getAddonVersion(modid);
    }

    public static ResourceManager manager() {
        return MtrModelRegistryUtil.resourceManager;
    }

    public static ResourceManager mgr() {
        return MtrModelRegistryUtil.resourceManager;
    }

    private static Identifier resolveRelativePath(Identifier baseFile, String relative) {
        String result = relative.toLowerCase(Locale.ROOT).replace('\\', '/');
        if (result.contains(":")) {
            result = result.replaceAll("[^a-z0-9/.:_-]", "_");
            return Identifier.parse(result);
        } else {
            result = result.replaceAll("[^a-z0-9/._-]", "_");
            if (result.endsWith(".jpg") || result.endsWith(".bmp") || result.endsWith(".tga")) {
                String var10000 = result.substring(0, result.length() - 4);
                result = var10000 + ".png";
            }

            return Identifier.fromNamespaceAndPath(baseFile.getNamespace(), FileSystems.getDefault().getPath(baseFile.getPath()).getParent().resolve(result).normalize().toString().replace('\\', '/'));
        }
    }
}
