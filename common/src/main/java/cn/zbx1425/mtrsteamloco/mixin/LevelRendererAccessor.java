package cn.zbx1425.mtrsteamloco.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
//
//    @Accessor
//    Frustum getCullingFrustum();
}
