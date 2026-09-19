package com.lx862.jcm.mod.data.pids.preset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.JCMClient;
import com.lx862.jcm.mod.block.entity.PIDSBlockEntity;
import com.lx862.jcm.mod.scripting.jcm.JCMScripting;
import com.lx862.jcm.mod.scripting.jcm.pids.PIDSScriptContext;
import com.lx862.jcm.mod.scripting.jcm.pids.PIDSScriptInstance;
import com.lx862.jcm.mod.scripting.jcm.pids.PIDSWrapper;
import com.lx862.jcm.mod.util.JCMLogger;
import com.lx862.mtrscripting.api.ScriptResultCall;
import com.lx862.mtrscripting.core.ParsedScript;
import com.lx862.mtrscripting.core.ScriptInstance;
import com.lx862.mtrscripting.data.ScriptContent;
import com.lx862.mtrscripting.data.UniqueKey;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mtr.data.ScheduleEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.Level;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptPIDSPreset extends PIDSPresetBase {
    private static final Identifier DEFAULT_THUMBNAIL = Constants.id("textures/gui/pids/thumbnail/generic_js.png");
    public final ParsedScript parsedScripts;

    public ScriptPIDSPreset(String id, String name, Identifier thumbnail, List<String> blacklist, boolean builtin, ParsedScript parsedScripts) {
        super(id, name, thumbnail, blacklist, builtin);
        this.parsedScripts = parsedScripts;
    }

    public static ScriptPIDSPreset parse(JsonObject rootJsonObject) throws Exception {
        final String id = rootJsonObject.get("id").getAsString();
        final String name = rootJsonObject.has("name") ? rootJsonObject.get("name").getAsString() : null;
        final boolean builtin = rootJsonObject.has("builtin") && rootJsonObject.get("builtin").getAsBoolean();
        final Identifier thumbnail = rootJsonObject.has("thumbnail") ? Identifier.parse(rootJsonObject.get("thumbnail").getAsString()) : DEFAULT_THUMBNAIL;

        final List<ScriptContent> scripts = new ObjectArrayList<>();
        if(rootJsonObject.has("scriptFiles")) {
            JsonArray scriptFilesArray = rootJsonObject.get("scriptFiles").getAsJsonArray();
            for (int i = 0; i < scriptFilesArray.size(); i++) {
                Identifier scriptLocation = Identifier.parse(scriptFilesArray.get(i).getAsString());
                Optional<Resource> scriptResource = Minecraft.getInstance().getResourceManager().getResource(scriptLocation);
                if (scriptResource.isPresent()) {
                    try (InputStream is = scriptResource.get().open()) {
                        String scriptText = IOUtils.toString(is, StandardCharsets.UTF_8);
                        scripts.add(new ScriptContent(scriptLocation, scriptText));
                    }
                } else {
                    JCMLogger.warn("Cannot find script in location {}:{}!", scriptLocation.getNamespace(), scriptLocation.getPath());
                }
            }
        }

        if(rootJsonObject.has("scriptTexts")) {
            JsonArray scriptTextArray = rootJsonObject.get("scriptTexts").getAsJsonArray();
            for(int i = 0; i < scriptTextArray.size(); i++) {
                Identifier scriptLocation = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "script_texts/jcm/pids/" + id + "/line" + i);
                String scriptText = scriptTextArray.get(i).getAsString();
                scripts.add(new ScriptContent(scriptLocation, scriptText));
            }
        }

        List<String> blackList = new ArrayList<>();
        if(rootJsonObject.has("blacklist")) {
            JsonArray blacklistedPIDS = rootJsonObject.getAsJsonArray("blacklist");
            for(int i = 0; i < blacklistedPIDS.size(); i++) {
                blackList.add(blacklistedPIDS.get(i).getAsString());
            }
        }

        ParsedScript parsedScripts = JCMScripting.getScriptManager().parseScript("PIDS", scripts);
        return new ScriptPIDSPreset(id, name, thumbnail, blackList, builtin, parsedScripts);
    }

    @Override
    public void render(PIDSBlockEntity be, PoseStack poseStack, MultiBufferSource bufferSource, Level world, BlockPos pos, Direction facing, List<ScheduleEntry> arrivals, boolean[] rowHidden, float tickDelta, int x, int y, int width, int height) {
        if(!JCMClient.getConfig().enableScripting) return;

        PIDSWrapper pidsState = new PIDSWrapper(be, arrivals, width, height);
        ScriptInstance<PIDSWrapper> scriptInstance = JCMScripting.getScriptManager().getInstanceManager().getInstance(new UniqueKey("jcm", "pids", getId(), pos), () -> new PIDSScriptInstance(be, parsedScripts, getId(), pidsState));

        if(scriptInstance instanceof PIDSScriptInstance) {
            PIDSScriptInstance pidsScriptInstance = (PIDSScriptInstance) scriptInstance;

            scriptInstance.setWrapperObject(pidsState);
            scriptInstance.getScript().invokeRenderFunctions(scriptInstance, () -> {
                pidsScriptInstance.drawCalls.clear();
                pidsScriptInstance.drawCalls.addAll(((PIDSScriptContext)scriptInstance.getScriptContext()).getDrawCalls());
                scriptInstance.getScriptContext().reset();
            });

            poseStack.translate(0, 0, -0.5);
            for(ScriptResultCall resultCalls : new ArrayList<>(pidsScriptInstance.drawCalls)) {
                if(resultCalls == null) continue;
                poseStack.translate(0, 0, -0.02);
                poseStack.pushPose();
                resultCalls.run(world, poseStack, bufferSource, facing, MAX_RENDER_LIGHT);
                poseStack.popPose();
            }
        }
    }

    @Override
    public int getTextColor() {
        return 0;
    }

    @Override
    public boolean isRowHidden(int row) {
        return false;
    }
}
