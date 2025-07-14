package fi.dy.masa.litematica.mixin.render;

import fi.dy.masa.litematica.compat.sodium.SodiumCompat;
import net.minecraft.client.render.BlockRenderLayerGroup;
import net.minecraft.client.render.SectionRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.litematica.render.LitematicaRenderer;

@Mixin(SectionRenderState.class)
public class MixinSectionRenderState
{
    @Inject(method = "renderSection", at = @At("TAIL"))
    private void litematica_drawBlockLayerGroup(BlockRenderLayerGroup blockRenderLayerGroup, CallbackInfo ci)
    {
        if (!SodiumCompat.hasSodium())
        {
            LitematicaRenderer.getInstance().piecewiseDrawBlockLayerGroup(blockRenderLayerGroup);
        }
    }
}
