package fi.dy.masa.litematica.mixin.render;

import java.util.List;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.litematica.compat.sodium.SodiumCompat;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.litematica.mixin.IMixinProfilerSystem;
import fi.dy.masa.litematica.render.LitematicaRenderer;
import fi.dy.masa.litematica.util.SchematicWorldRefresher;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow private net.minecraft.client.world.ClientWorld world;
    @Shadow @Final private MinecraftClient client;
    @Shadow private Frustum frustum;
    @Unique private Profiler profiler;

    @Inject(method = "reload()V", at = @At("RETURN"))
    private void litematica_onLoadRenderers(CallbackInfo ci)
    {
        // Also (re-)load our renderer when the vanilla renderer gets reloaded
        if (this.world != null && this.world == this.client.world)
        {
            if (this.profiler == null)
            {
                this.profiler = Profilers.get();
            }
            if (this.profiler instanceof ProfilerSystem ps && !((IMixinProfilerSystem) ps).litematica_isStarted())
            {
                this.profiler.startTick();
            }

            LitematicaRenderer.getInstance().loadRenderers(this.profiler);
            SchematicWorldRefresher.INSTANCE.updateAll();
        }
    }

//    @Inject(method = "setupTerrain", at = @At("TAIL"))
//    private void litematica_onPostSetupTerrain(
//            Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator, CallbackInfo ci)
//    {
//        if (this.profiler == null)
//        {
//            this.profiler = Profilers.get();
//        }
//        if (this.profiler instanceof ProfilerSystem ps && !((IMixinProfilerSystem) ps).litematica_isStarted())
//        {
//            this.profiler.startTick();
//        }
//
//        LitematicaRenderer.getInstance().piecewisePrepareAndUpdate(frustum, this.profiler);
//        LitematicaRenderer.getInstance().scheduleTranslucentSorting(camera.getCameraPos(), this.profiler);
//    }

    @Inject(method = "updateChunks",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
                     ordinal = 1)
    )
    private void litematica_onPostUpdateChunks(Camera camera, CallbackInfo ci)
    {
        if (this.profiler == null)
        {
            this.profiler = Profilers.get();
        }
        if (this.profiler instanceof ProfilerSystem ps && !((IMixinProfilerSystem) ps).litematica_isStarted())
        {
            this.profiler.startTick();
        }

        LitematicaRenderer.getInstance().piecewisePrepareAndUpdate(this.frustum, this.profiler);

        if (SodiumCompat.hasSodium())
        {
            LitematicaRenderer.getInstance().scheduleTranslucentSorting(camera.getPos(), this.profiler);
        }
    }

    @Inject(method = "translucencySort", at = @At("TAIL"))
    private void litematica_onScheduleTranslucentSort(Vec3d cameraPos, CallbackInfo ci)
    {
        if (this.profiler == null)
        {
            this.profiler = Profilers.get();
        }

        if (this.profiler instanceof ProfilerSystem ps && !((IMixinProfilerSystem) ps).litematica_isStarted())
        {
            this.profiler.startTick();
        }

        if (!SodiumCompat.hasSodium())
        {
            LitematicaRenderer.getInstance().scheduleTranslucentSorting(cameraPos, this.profiler);
        }
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderMain(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;ZZLnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/util/profiler/Profiler;)V",
                    shift = At.Shift.BEFORE))
    private void litematica_onPreRenderMain(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
                                            Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix,
                                            GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci,
                                            @Local Profiler profiler)
    {
        this.profiler = profiler;
        LitematicaRenderer.getInstance().capturePreMainValues(fog, profiler);
    }

    @Inject(method = "renderBlockLayers", at = @At("TAIL"))
    private void litematica_onPrepareBlockLayers(Matrix4fc matrix4fc, double d, double e, double f, CallbackInfoReturnable<SectionRenderState> cir)
    {
        if (this.profiler == null)
        {
            this.profiler = Profilers.get();
        }
        if (this.profiler instanceof ProfilerSystem ps && !((IMixinProfilerSystem) ps).litematica_isStarted())
        {
            this.profiler.startTick();
        }

//        if (renderLayer == RenderLayer.getSolid())
//        {
//            LitematicaRenderer.getInstance().piecewiseRenderSolid(viewMatrix, posMatrix, this.profiler);
//        }
//        else if (renderLayer == RenderLayer.getCutoutMipped())
//        {
//            LitematicaRenderer.getInstance().piecewiseRenderCutoutMipped(viewMatrix, posMatrix, this.profiler);
//        }
//        else if (renderLayer == RenderLayer.getCutout())
//        {
//            LitematicaRenderer.getInstance().piecewiseRenderCutout(viewMatrix, posMatrix, this.profiler);
//        }
//        else if (renderLayer == RenderLayer.getTranslucent())
//        {
//            LitematicaRenderer.getInstance().piecewiseRenderTranslucent(viewMatrix, posMatrix, this.profiler);
//        }
//        else if (renderLayer == RenderLayer.getTripwire())
//        {
//            LitematicaRenderer.getInstance().piecewiseRenderTripwire(viewMatrix, posMatrix, this.profiler);
//        }

        LitematicaRenderer.getInstance().piecewisePrepareBlockLayers(matrix4fc, d, e, f, this.profiler);

        // Fix Sodium Compat
        if (SodiumCompat.hasSodium())
        {
            LitematicaRenderer.getInstance().piecewiseDrawBlockLayerGroup(BlockRenderLayerGroup.OPAQUE);
            SodiumCompat.startBlockOutlineEnabled();
        }
    }

    @Inject(method = "renderEntities",
            at = @At(value = "RETURN"))
    private void litematica_onPostRenderEntities(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Camera camera, RenderTickCounter tickCounter, List<Entity> entities, CallbackInfo ci)
    {
        if (this.profiler == null)
        {
            this.profiler = Profilers.get();
        }

        if (this.profiler instanceof ProfilerSystem ps && !((IMixinProfilerSystem) ps).litematica_isStarted())
        {
            this.profiler.startTick();
        }

        LitematicaRenderer.getInstance().piecewiseRenderEntities(matrices, immediate, tickCounter.getTickProgress(false), this.profiler);
    }

    @Inject(method = "renderBlockEntities",
            at = @At(value = "RETURN"))
    private void litematica_onPostRenderBlockEntities(MatrixStack matrices, VertexConsumerProvider.Immediate entityVertexConsumers, VertexConsumerProvider.Immediate effectVertexConsumers, Camera camera, float tickProgress, CallbackInfo ci)
    {
        if (this.profiler == null)
        {
            this.profiler = Profilers.get();
        }

        if (this.profiler instanceof ProfilerSystem ps && !((IMixinProfilerSystem) ps).litematica_isStarted())
        {
            this.profiler.startTick();
        }

        LitematicaRenderer.getInstance().piecewiseRenderBlockEntities(matrices, entityVertexConsumers, effectVertexConsumers, tickProgress, this.profiler);
    }

    @Inject(method = "renderTargetBlockOutline(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/util/math/MatrixStack;Z)V",
            at = @At("HEAD"))
    private void litematica_onRenderTargetOutline(Camera camera, VertexConsumerProvider.Immediate vertexConsumers,
                                                  MatrixStack matrices, boolean translucent, CallbackInfo ci)
    {
        // Fix Sodium Compat
        if (SodiumCompat.hasSodium() && translucent)
        {
            LitematicaRenderer.getInstance().piecewiseDrawBlockLayerGroup(BlockRenderLayerGroup.TRANSLUCENT);
            LitematicaRenderer.getInstance().piecewiseDrawBlockLayerGroup(BlockRenderLayerGroup.TRIPWIRE);
            SodiumCompat.endBlockOutlineEnabled();
        }
    }

    /*
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderWorldLast(
            net.minecraft.client.util.math.MatrixStack matrices,
            float tickDelta, long limitTime, boolean renderBlockOutline,
            net.minecraft.client.render.Camera camera,
            net.minecraft.client.render.GameRenderer gameRenderer,
            net.minecraft.client.render.LightmapTextureManager lightmapTextureManager,
            net.minecraft.client.util.math.Matrix4f matrix4f,
            CallbackInfo ci)
    {
        boolean invert = Hotkeys.INVERT_GHOST_BLOCK_RENDER_STATE.getKeybind().isKeybindHeld();

        if (Configs.Visuals.ENABLE_SCHEMATIC_RENDERING.getBooleanValue() != invert &&
            Configs.Generic.BETTER_RENDER_ORDER.getBooleanValue() == false)
        {
            LitematicaRenderer.getInstance().renderSchematicWorld(matrices, matrix4f, tickDelta);
        }
    }
    */
}
