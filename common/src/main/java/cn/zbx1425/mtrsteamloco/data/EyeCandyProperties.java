package cn.zbx1425.mtrsteamloco.data;

import cn.zbx1425.mtrsteamloco.render.scripting.ScriptHolder;
import cn.zbx1425.sowcerext.model.ModelCluster;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.Closeable;
import java.io.IOException;

public class EyeCandyProperties implements Closeable {

    public Component name;

    public ModelCluster model;
    public ScriptHolder script;

    public int[] voxelShape;

    /** Item model Identifier used as the icon in the creative tab, or null to use the block item's default thumbnail. */
    public Identifier icon;

    public EyeCandyProperties(Component name, ModelCluster model, ScriptHolder script, int[] voxelShape) {
        this(name, model, script, voxelShape, null);
    }

    public EyeCandyProperties(Component name, ModelCluster model, ScriptHolder script, int[] voxelShape, Identifier icon) {
        this.name = name;
        this.model = model;
        this.script = script;
        this.voxelShape = voxelShape;
        this.icon = icon;
        if(voxelShape != null && voxelShape.length != 6) throw new IllegalStateException("voxelShape expected to have 6 values!");
    }

    @Override
    public void close() throws IOException {
        if (model != null) model.close();
    }
}
