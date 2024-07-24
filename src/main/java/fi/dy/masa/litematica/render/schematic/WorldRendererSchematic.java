package fi.dy.masa.litematica.render.schematic;

import javax.annotation.Nullable;
import java.util.*;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.LayerRange;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.config.Hotkeys;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.util.OverlayType;
import fi.dy.masa.litematica.world.ChunkSchematic;
import fi.dy.masa.litematica.world.WorldSchematic;

public class WorldRendererSchematic
{
    private final MinecraftClient mc;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final BlockRenderManager blockRenderManager;
    private final BlockModelRendererSchematic blockModelRenderer;
    private final Set<BlockEntity> blockEntities = new HashSet<>();
    private final List<ChunkRendererSchematicVbo> renderInfos = new ArrayList<>(1024);
    private final BufferBuilderStorage bufferBuilders;
    private Set<ChunkRendererSchematicVbo> chunksToUpdate = new LinkedHashSet<>();
    private WorldSchematic world;
    private ChunkRenderDispatcherSchematic chunkRendererDispatcher;
    private double lastCameraChunkUpdateX = Double.MIN_VALUE;
    private double lastCameraChunkUpdateY = Double.MIN_VALUE;
    private double lastCameraChunkUpdateZ = Double.MIN_VALUE;
    private double lastCameraX = Double.MIN_VALUE;
    private double lastCameraY = Double.MIN_VALUE;
    private double lastCameraZ = Double.MIN_VALUE;
    private float lastCameraPitch = Float.MIN_VALUE;
    private float lastCameraYaw = Float.MIN_VALUE;
    private ChunkRenderDispatcherLitematica renderDispatcher;
    private final IChunkRendererFactory renderChunkFactory;
    //private ShaderGroup entityOutlineShader;
    //private boolean entityOutlinesRendered;

    private int renderDistanceChunks = -1;
    private int renderEntitiesStartupCounter = 2;
    private int countEntitiesTotal;
    private int countEntitiesRendered;
    private int countEntitiesHidden;

    private double lastTranslucentSortX;
    private double lastTranslucentSortY;
    private double lastTranslucentSortZ;
    private boolean displayListEntitiesDirty = true;

    public WorldRendererSchematic(MinecraftClient mc)
    {
        this.mc = mc;
        this.entityRenderDispatcher = mc.getEntityRenderDispatcher();
        this.bufferBuilders = mc.getBufferBuilders();

        this.renderChunkFactory = (world1, worldRenderer) -> new ChunkRendererSchematicVbo(world1, worldRenderer);

        this.blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        this.blockModelRenderer = new BlockModelRendererSchematic(mc.getBlockColors());
    }

    public void markNeedsUpdate()
    {
        this.displayListEntitiesDirty = true;
    }

    public boolean hasWorld()
    {
        return this.world != null;
    }

    public String getDebugInfoRenders()
    {
        int rcTotal = this.chunkRendererDispatcher != null ? this.chunkRendererDispatcher.getRendererCount() : 0;
        int rcRendered = this.chunkRendererDispatcher != null ? this.getRenderedChunks() : 0;
        return String.format("C: %d/%d %sD: %d, L: %d, %s", rcRendered, rcTotal, this.mc.chunkCullingEnabled ? "(s) " : "", this.renderDistanceChunks, 0, this.renderDispatcher == null ? "null" : this.renderDispatcher.getDebugInfo());
    }

    public String getDebugInfoEntities()
    {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden;
    }

    protected int getRenderedChunks()
    {
        int count = 0;

        for (ChunkRendererSchematicVbo chunkRenderer : this.renderInfos)
        {
            // Threaded Code
            //ChunkRenderDataSchematic data = chunkRenderer.chunkRenderData.get();
            ChunkRenderDataSchematic data = chunkRenderer.chunkRenderData;

            if (data != ChunkRenderDataSchematic.EMPTY && !data.isEmpty())
            {
                ++count;
            }
        }

        return count;
    }

    public void setWorldAndLoadRenderers(@Nullable WorldSchematic worldSchematic)
    {
        this.lastCameraChunkUpdateX = Double.MIN_VALUE;
        this.lastCameraChunkUpdateY = Double.MIN_VALUE;
        this.lastCameraChunkUpdateZ = Double.MIN_VALUE;
        //this.renderManager.setWorld(worldClientIn);
        this.world = worldSchematic;

        if (worldSchematic != null)
        {
            this.loadRenderers();
        }
        else
        {
            this.chunksToUpdate.forEach(ChunkRendererSchematicVbo::deleteGlResources);
            this.chunksToUpdate.clear();
            this.renderInfos.forEach(ChunkRendererSchematicVbo::deleteGlResources);
            this.renderInfos.clear();

            if (this.chunkRendererDispatcher != null)
            {
                this.chunkRendererDispatcher.delete();
                this.chunkRendererDispatcher = null;
            }

            if (this.renderDispatcher != null)
            {
                this.renderDispatcher.stopWorkerThreads();
            }

            this.renderDispatcher = null;
            this.blockEntities.clear();
        }
    }

    public void loadRenderers()
    {
        if (this.hasWorld())
        {
            this.world.getProfiler().push("litematica_load_renderers");

            if (this.renderDispatcher == null)
            {
                this.renderDispatcher = new ChunkRenderDispatcherLitematica();
            }

            this.displayListEntitiesDirty = true;
            this.renderDistanceChunks = this.mc.options.getViewDistance().getValue() + 2;

            if (this.chunkRendererDispatcher != null)
            {
                this.chunkRendererDispatcher.delete();
            }

            this.stopChunkUpdates();

            synchronized (this.blockEntities)
            {
                this.blockEntities.clear();
            }

            this.chunkRendererDispatcher = new ChunkRenderDispatcherSchematic(this.world, this.renderDistanceChunks, this, this.renderChunkFactory);
            this.renderEntitiesStartupCounter = 2;

            this.world.getProfiler().pop();
        }
    }

    protected void stopChunkUpdates()
    {
        if (this.chunksToUpdate.isEmpty() == false)
        {
            this.chunksToUpdate.forEach(ChunkRendererSchematicVbo::deleteGlResources);
        }
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }

    public void setupTerrain(Camera camera, Frustum frustum, int frameCount, boolean playerSpectator)
    {
        this.world.getProfiler().push("setup_terrain");

        if (this.chunkRendererDispatcher == null ||
            this.mc.options.getViewDistance().getValue() + 2 != this.renderDistanceChunks)
        {
            this.loadRenderers();
        }

        Entity entity = EntityUtils.getCameraEntity();

        if (entity == null)
        {
            entity = this.mc.player;
        }

        //camera.update(this.world, entity, this.mc.options.perspective > 0, this.mc.options.perspective == 2, this.mc.getTickDelta());

        this.world.getProfiler().push("camera");

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        double diffX = entityX - this.lastCameraChunkUpdateX;
        double diffY = entityY - this.lastCameraChunkUpdateY;
        double diffZ = entityZ - this.lastCameraChunkUpdateZ;

        if (diffX * diffX + diffY * diffY + diffZ * diffZ > 256.0)
        {
            this.lastCameraChunkUpdateX = entityX;
            this.lastCameraChunkUpdateY = entityY;
            this.lastCameraChunkUpdateZ = entityZ;
            this.chunkRendererDispatcher.removeOutOfRangeRenderers();
        }

        this.world.getProfiler().swap("renderlist_camera");

        Vec3d cameraPos = camera.getPos();
        double cameraX = cameraPos.x;
        double cameraY = cameraPos.y;
        double cameraZ = cameraPos.z;

        this.renderDispatcher.setCameraPosition(cameraPos);

        this.world.getProfiler().swap("culling");
        BlockPos viewPos = BlockPos.ofFloored(cameraX, cameraY + (double) entity.getStandingEyeHeight(), cameraZ);
        final int centerChunkX = (viewPos.getX() >> 4);
        final int centerChunkZ = (viewPos.getZ() >> 4);
        final int renderDistance = this.mc.options.getViewDistance().getValue() + 2;
        ChunkPos viewChunk = new ChunkPos(viewPos);

        this.displayListEntitiesDirty = this.displayListEntitiesDirty || this.chunksToUpdate.isEmpty() == false ||
                entityX != this.lastCameraX ||
                entityY != this.lastCameraY ||
                entityZ != this.lastCameraZ ||
                entity.getPitch() != this.lastCameraPitch ||
                entity.getYaw() != this.lastCameraYaw;
        this.lastCameraX = cameraX;
        this.lastCameraY = cameraY;
        this.lastCameraZ = cameraZ;
        this.lastCameraPitch = camera.getPitch();
        this.lastCameraYaw = camera.getYaw();

        this.world.getProfiler().swap("update");

        if (this.displayListEntitiesDirty)
        {
            this.world.getProfiler().push("fetch");

            this.displayListEntitiesDirty = false;
            this.renderInfos.clear();

            this.world.getProfiler().swap("sort");
            List<ChunkPos> positions = DataManager.getSchematicPlacementManager().getAndUpdateVisibleChunks(viewChunk);
            //positions.sort(new SubChunkPos.DistanceComparator(viewSubChunk));

            //Queue<SubChunkPos> queuePositions = new PriorityQueue<>(new SubChunkPos.DistanceComparator(viewSubChunk));
            //queuePositions.addAll(set);

            //if (GuiBase.isCtrlDown()) System.out.printf("sorted positions: %d\n", positions.size());

            this.world.getProfiler().swap("iteration");

            //while (queuePositions.isEmpty() == false)
            for (ChunkPos chunkPos : positions)
            {
                //SubChunkPos subChunk = queuePositions.poll();
                int cx = chunkPos.x;
                int cz = chunkPos.z;
                //Litematica.logger.warn("setupTerrain() [WorldRenderer] positions[{}] chunkPos: {} // isLoaded: {}", i, chunkPos.toString(), this.world.getChunkProvider().isChunkLoaded(cx, cz));
                // Only render sub-chunks that are within the client's render distance, and that
                // have been already properly loaded on the client
                if (Math.abs(cx - centerChunkX) <= renderDistance &&
                    Math.abs(cz - centerChunkZ) <= renderDistance &&
                    this.world.getChunkProvider().isChunkLoaded(cx, cz))
                {
                    ChunkRendererSchematicVbo chunkRenderer = this.chunkRendererDispatcher.getChunkRenderer(cx, cz);

                    if (chunkRenderer != null && frustum.isVisible(chunkRenderer.getBoundingBox()))
                    {
                        //if (GuiBase.isCtrlDown()) System.out.printf("add @ %s\n", subChunk);
                        if (chunkRenderer.needsUpdate() && chunkPos.equals(viewChunk))
                        {
                            chunkRenderer.setNeedsUpdate(true);
                        }

                        this.renderInfos.add(chunkRenderer);
                    }
                }
            }

            this.world.getProfiler().pop(); // fetch
        }

        this.world.getProfiler().swap("rebuild_near");
        Set<ChunkRendererSchematicVbo> set = this.chunksToUpdate;
        this.chunksToUpdate = new LinkedHashSet<>();

        for (ChunkRendererSchematicVbo chunkRendererTmp : this.renderInfos)
        {
            if (chunkRendererTmp.needsUpdate() || set.contains(chunkRendererTmp))
            {
                this.displayListEntitiesDirty = true;
                BlockPos pos = chunkRendererTmp.getOrigin().add(8, 8, 8);
                boolean isNear = pos.getSquaredDistance(viewPos) < 1024.0D;

                if (chunkRendererTmp.needsImmediateUpdate() == false && isNear == false)
                {
                    this.chunksToUpdate.add(chunkRendererTmp);
                }
                else
                {
                    //if (GuiBase.isCtrlDown()) System.out.printf("====== update now\n");
                    this.world.getProfiler().push("build_near");

                    this.renderDispatcher.updateChunkNow(chunkRendererTmp);
                    chunkRendererTmp.clearNeedsUpdate();

                    this.world.getProfiler().pop();
                }
            }
        }

        this.chunksToUpdate.addAll(set);

        this.world.getProfiler().pop();
        this.world.getProfiler().pop();
    }

    public void updateChunks(long finishTimeNano)
    {
        this.mc.getProfiler().push("litematica_run_chunk_uploads");
        this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);

        this.mc.getProfiler().swap("litematica_check_update");

        if (this.chunksToUpdate.isEmpty() == false)
        {
            Iterator<ChunkRendererSchematicVbo> iterator = this.chunksToUpdate.iterator();
            int index = 0;

            while (iterator.hasNext())
            {
                ChunkRendererSchematicVbo renderChunk = iterator.next();
                boolean flag;

                if (renderChunk.needsImmediateUpdate())
                {
                    this.mc.getProfiler().push("litematica_update_now");
                    flag = this.renderDispatcher.updateChunkNow(renderChunk);
                }
                else
                {
                    this.mc.getProfiler().push("litematica_update_later");
                    flag = this.renderDispatcher.updateChunkLater(renderChunk);
                }

                this.mc.getProfiler().pop();

                if (!flag)
                {
                    break;
                }

                renderChunk.clearNeedsUpdate();
                iterator.remove();
                long i = finishTimeNano - System.nanoTime();

                if (i < 0L)
                {
                    break;
                }
                index++;
            }
        }

        this.mc.getProfiler().pop();
    }

    public int renderBlockLayer(RenderLayer renderLayer, Matrix4f matrices, Camera camera, Matrix4f projMatrix)
    {
        RenderSystem.assertOnRenderThread();
        this.world.getProfiler().push("render_block_layer_" + renderLayer.toString());

        boolean isTranslucent = renderLayer == RenderLayer.getTranslucent();

        renderLayer.startDrawing();
        //RenderUtils.disableDiffuseLighting();
        Vec3d cameraPos = camera.getPos();
        double x = cameraPos.x;
        double y = cameraPos.y;
        double z = cameraPos.z;

        if (isTranslucent)
        {
            this.world.getProfiler().push("translucent_sort");
            double diffX = x - this.lastTranslucentSortX;
            double diffY = y - this.lastTranslucentSortY;
            double diffZ = z - this.lastTranslucentSortZ;

            if (diffX * diffX + diffY * diffY + diffZ * diffZ > 1.0D)
            {
                //int i = ChunkSectionPos.getSectionCoord(x);
                //int j = ChunkSectionPos.getSectionCoord(y);
                //int k = ChunkSectionPos.getSectionCoord(z);
                //boolean block = i != ChunkSectionPos.getSectionCoord(this.lastTranslucentSortX) || k != ChunkSectionPos.getSectionCoord(this.lastTranslucentSortZ) || j != ChunkSectionPos.getSectionCoord(this.lastTranslucentSortY);
                this.lastTranslucentSortX = x;
                this.lastTranslucentSortY = y;
                this.lastTranslucentSortZ = z;
                int h = 0;

                for (ChunkRendererSchematicVbo chunkRenderer : this.renderInfos)
                {
                    //if ((chunkRenderer.getChunkRenderData().isBlockLayerStarted(renderLayer) || !block  && !chunkRenderer.isAxisAlignedWith(i, j, k) ||
                    if ((chunkRenderer.getChunkRenderData().isBlockLayerStarted(renderLayer) ||
                        (chunkRenderer.getChunkRenderData() != ChunkRenderDataSchematic.EMPTY && chunkRenderer.hasOverlay())) && h++ < 15)
                    {
                        this.renderDispatcher.updateTransparencyLater(chunkRenderer);
                    }
                }
            }

            this.world.getProfiler().pop();
        }

        this.world.getProfiler().push("filter_empty");
        this.world.getProfiler().swap("render");

        boolean reverse = isTranslucent;
        int startIndex = reverse ? this.renderInfos.size() - 1 : 0;
        int stopIndex = reverse ? -1 : this.renderInfos.size();
        int increment = reverse ? -1 : 1;
        int count = 0;

        ShaderProgram shader = RenderSystem.getShader();
        BufferRenderer.reset();

        boolean renderAsTranslucent = Configs.Visuals.RENDER_BLOCKS_AS_TRANSLUCENT.getBooleanValue();

        if (renderAsTranslucent)
        {
            float alpha = (float) Configs.Visuals.GHOST_BLOCK_ALPHA.getDoubleValue();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        }

        initShader(shader, matrices, projMatrix);
        RenderSystem.setupShaderLights(shader);
        shader.bind();

        GlUniform chunkOffsetUniform = shader.chunkOffset;
        boolean startedDrawing = false;

        for (int i = startIndex; i != stopIndex; i += increment)
        {
            ChunkRendererSchematicVbo renderer = this.renderInfos.get(i);

            if (renderer.getChunkRenderData().isBlockLayerEmpty(renderLayer) == false)
            {
                BlockPos chunkOrigin = renderer.getOrigin();
                VertexBuffer buffer = renderer.getBlocksVertexBufferByLayer(renderLayer);

                if (buffer == null || buffer.isClosed())
                {
                    continue;
                }

                if (renderer.getChunkRenderData().getBuiltBufferCache().hasBuiltBufferByLayer(renderLayer) == false)
                {
                    continue;
                }

                if (chunkOffsetUniform != null)
                {
                    chunkOffsetUniform.set((float)(chunkOrigin.getX() - x), (float)(chunkOrigin.getY() - y), (float)(chunkOrigin.getZ() - z));
                    chunkOffsetUniform.upload();
                }

                buffer.bind();
                buffer.draw();
                VertexBuffer.unbind();
                startedDrawing = true;
                ++count;
            }
        }

        if (renderAsTranslucent)
        {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        if (chunkOffsetUniform != null)
        {
            chunkOffsetUniform.set(0.0F, 0.0F, 0.0F);
        }

        shader.unbind();

        if (startedDrawing)
        {
            renderLayer.getVertexFormat().clearState();
        }

        VertexBuffer.unbind();
        renderLayer.endDrawing();

        this.world.getProfiler().pop();
        this.world.getProfiler().pop();

        return count;
    }

    public void renderBlockOverlays(Matrix4f matrix4f, Camera camera, Matrix4f projMatrix)
    {
        this.renderBlockOverlay(OverlayRenderType.OUTLINE, matrix4f, camera, projMatrix);
        this.renderBlockOverlay(OverlayRenderType.QUAD, matrix4f, camera, projMatrix);
    }

    protected static void initShader(ShaderProgram shader, Matrix4f matrix4f, Matrix4f projMatrix)
    {
        for (int i = 0; i < 12; ++i) shader.addSampler("Sampler" + i, RenderSystem.getShaderTexture(i));

        if (shader.modelViewMat != null) shader.modelViewMat.set(matrix4f);
        if (shader.projectionMat != null) shader.projectionMat.set(projMatrix);
        if (shader.colorModulator != null) shader.colorModulator.set(RenderSystem.getShaderColor());
        if (shader.fogStart != null) shader.fogStart.set(RenderSystem.getShaderFogStart());
        if (shader.fogEnd != null) shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        if (shader.fogColor != null) shader.fogColor.set(RenderSystem.getShaderFogColor());
        if (shader.textureMat != null) shader.textureMat.set(RenderSystem.getTextureMatrix());
        if (shader.gameTime != null) shader.gameTime.set(RenderSystem.getShaderGameTime());
    }

    protected void renderBlockOverlay(OverlayRenderType type, Matrix4f matrix4f, Camera camera, Matrix4f projMatrix)
    {
        RenderLayer renderLayer = RenderLayer.getTranslucent();
        renderLayer.startDrawing();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vec3d cameraPos = camera.getPos();
        double x = cameraPos.x;
        double y = cameraPos.y;
        double z = cameraPos.z;

        this.world.getProfiler().push("overlay_" + type.name());
        this.world.getProfiler().swap("render");

        boolean renderThrough = Configs.Visuals.SCHEMATIC_OVERLAY_RENDER_THROUGH.getBooleanValue() || Hotkeys.RENDER_OVERLAY_THROUGH_BLOCKS.getKeybind().isKeybindHeld();

        if (renderThrough)
        {
            RenderSystem.disableDepthTest();
        }
        else
        {
            RenderSystem.enableDepthTest();
        }

        ShaderProgram originalShader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        ShaderProgram shader = RenderSystem.getShader();
        BufferRenderer.reset();
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();

        for (int i = this.renderInfos.size() - 1; i >= 0; --i)
        {
            ChunkRendererSchematicVbo renderer = this.renderInfos.get(i);

            if (renderer.getChunkRenderData() != ChunkRenderDataSchematic.EMPTY && renderer.hasOverlay())
            {
                ChunkRenderDataSchematic compiledChunk = renderer.getChunkRenderData();

                if (compiledChunk.isOverlayTypeEmpty(type) == false)
                {
                    VertexBuffer buffer = renderer.getOverlayVertexBuffer(type);
                    BlockPos chunkOrigin = renderer.getOrigin();

                    if (buffer == null || buffer.isClosed() || renderer.getChunkRenderData().getBuiltBufferCache().hasBuiltBufferByType(type) == false)
                    {
                        continue;
                    }

                    matrix4fStack.pushMatrix();
                    matrix4fStack.translate((float) (chunkOrigin.getX() - x), (float) (chunkOrigin.getY() - y), (float) (chunkOrigin.getZ() - z));
                    buffer.bind();
                    buffer.draw(matrix4fStack, projMatrix, shader);

                    VertexBuffer.unbind();
                    matrix4fStack.popMatrix();
                }
            }
        }

        renderLayer.endDrawing();

        RenderSystem.setShader(() -> originalShader);
        RenderSystem.disableBlend();

        this.world.getProfiler().pop();
    }

    public boolean renderBlock(BlockRenderView world, BlockState state, BlockPos pos, MatrixStack matrixStack, BufferBuilder bufferBuilderIn)
    {
        try
        {
            BlockRenderType renderType = state.getRenderType();

            if (renderType == BlockRenderType.INVISIBLE)
            {
                return false;
            }
            else
            {
                return renderType == BlockRenderType.MODEL &&
                       this.blockModelRenderer.renderModel(world, this.getModelForState(state), state, pos, matrixStack, bufferBuilderIn, state.getRenderingSeed(pos));
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.create(throwable, "Tesselating block in world");
            CrashReportSection crashreportcategory = crashreport.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(crashreportcategory, world, pos, state);
            throw new CrashException(crashreport);
        }
    }

    public void renderFluid(BlockRenderView world, BlockState blockState, FluidState fluidState, BlockPos pos, BufferBuilder bufferBuilderIn)
    {
        // Sometimes this collides with FAPI
        try
        {
            this.blockRenderManager.renderFluid(pos, world, bufferBuilderIn, blockState, fluidState);
        }
        catch (Exception ignored) { }
    }

    public BakedModel getModelForState(BlockState state)
    {
        if (state.getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED)
        {
            return this.blockRenderManager.getModels().getModelManager().getMissingModel();
        }

        return this.blockRenderManager.getModel(state);
    }

    public void renderEntities(Camera camera, Frustum frustum, Matrix4f matrix4f, float partialTicks)
    {
        if (this.renderEntitiesStartupCounter > 0)
        {
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            this.world.getProfiler().push("prepare");

            double cameraX = camera.getPos().x;
            double cameraY = camera.getPos().y;
            double cameraZ = camera.getPos().z;

            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().configure(this.world, camera, this.mc.crosshairTarget);
            this.entityRenderDispatcher.configure(this.world, camera, this.mc.targetedEntity);

            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;

            this.countEntitiesTotal = this.world.getRegularEntityCount();

            this.world.getProfiler().swap("regular_entities");
            //List<Entity> entitiesMultipass = Lists.<Entity>newArrayList();

            // TODO --> Convert Matrix4f back to to MatrixStack?
            //  Causes strange entity behavior (translations not applied)
            //  if this is missing ( Including the push() and pop() ... ?)
            //  Doing this restores the expected behavior of Entity Rendering in the Schematic World

            MatrixStack matrixStack = new MatrixStack();
            matrixStack.push();
            matrixStack.multiplyPositionMatrix(matrix4f);
            matrixStack.pop();

            VertexConsumerProvider.Immediate entityVertexConsumers = this.bufferBuilders.getEntityVertexConsumers();
            LayerRange layerRange = DataManager.getRenderLayerRange();

            for (ChunkRendererSchematicVbo chunkRenderer : this.renderInfos)
            {
                BlockPos pos = chunkRenderer.getOrigin();
                ChunkSchematic chunk = this.world.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
                List<Entity> list = chunk.getEntityList();

                if (list.isEmpty() == false)
                {
                    for (Entity entityTmp : list)
                    {
                        if (layerRange.isPositionWithinRange((int) entityTmp.getX(), (int) entityTmp.getY(), (int) entityTmp.getZ()) == false)
                        {
                            continue;
                        }

                        boolean shouldRender = this.entityRenderDispatcher.shouldRender(entityTmp, frustum, cameraX, cameraY, cameraZ);

                        if (shouldRender)
                        {
                            double x = entityTmp.getX() - cameraX;
                            double y = entityTmp.getY() - cameraY;
                            double z = entityTmp.getZ() - cameraZ;

                            matrixStack.push();

                            // TODO --> this render() call does not seem to have a push() and pop(),
                            //  and does not accept Matrix4f/Matrix4fStack as a parameter
                            this.entityRenderDispatcher.render(entityTmp, x, y, z, entityTmp.getYaw(), 1.0f, matrixStack, entityVertexConsumers, this.entityRenderDispatcher.getLight(entityTmp, partialTicks));
                            ++this.countEntitiesRendered;

                            matrixStack.pop();
                        }
                    }
                }
            }

            this.world.getProfiler().swap("block_entities");
            BlockEntityRenderDispatcher renderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();

            for (ChunkRendererSchematicVbo chunkRenderer : this.renderInfos)
            {
                ChunkRenderDataSchematic data = chunkRenderer.getChunkRenderData();
                List<BlockEntity> tiles = data.getBlockEntities();

                if (tiles.isEmpty() == false)
                {
                    BlockPos chunkOrigin = chunkRenderer.getOrigin();
                    ChunkSchematic chunk = this.world.getChunkProvider().getChunk(chunkOrigin.getX() >> 4, chunkOrigin.getZ() >> 4);

                    if (chunk != null && data.getTimeBuilt() >= chunk.getTimeCreated())
                    {
                        for (BlockEntity te : tiles)
                        {
                            try
                            {
                                BlockPos pos = te.getPos();
                                matrixStack.push();
                                matrixStack.translate(pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);

                                // TODO --> this render() call does not seem to have a push() and pop(),
                                //  and does not accept Matrix4f/Matrix4fStack as a parameter
                                renderer.render(te, partialTicks, matrixStack, entityVertexConsumers);

                                matrixStack.pop();
                            }
                            catch (Exception ignore)
                            {
                            }
                        }
                    }
                }
            }

            synchronized (this.blockEntities)
            {
                for (BlockEntity te : this.blockEntities)
                {
                    try
                    {
                        BlockPos pos = te.getPos();
                        matrixStack.push();
                        matrixStack.translate(pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);

                        // TODO --> this render() call does not seem to have a push() and pop(),
                        //  and does not accept Matrix4f/Matrix4fStack as a parameter
                        renderer.render(te, partialTicks, matrixStack, entityVertexConsumers);

                        matrixStack.pop();
                    }
                    catch (Exception ignore)
                    {
                    }
                }
            }

            this.world.getProfiler().pop();
        }
    }

    /*
    private boolean isOutlineActive(Entity entityIn, Entity viewer, Camera camera)
    {
        boolean sleeping = viewer instanceof LivingEntity && ((LivingEntity) viewer).isSleeping();

        if (entityIn == viewer && this.mc.options.perspective == 0 && sleeping == false)
        {
            return false;
        }
        else if (entityIn.isGlowing())
        {
            return true;
        }
        else if (this.mc.player.isSpectator() && this.mc.options.keySpectatorOutlines.isPressed() && entityIn instanceof PlayerEntity)
        {
            return entityIn.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entityIn.getBoundingBox()) || entityIn.isRidingOrBeingRiddenBy(this.mc.player);
        }
        else
        {
            return false;
        }
    }
    */

    public void updateBlockEntities(Collection<BlockEntity> toRemove, Collection<BlockEntity> toAdd)
    {
        synchronized (this.blockEntities)
        {
            this.blockEntities.removeAll(toRemove);
            this.blockEntities.addAll(toAdd);
        }
    }

    public void scheduleChunkRenders(int chunkX, int chunkZ)
    {
        if (Configs.Visuals.ENABLE_RENDERING.getBooleanValue() &&
            Configs.Visuals.ENABLE_SCHEMATIC_RENDERING.getBooleanValue())
        {
            this.chunkRendererDispatcher.scheduleChunkRender(chunkX, chunkZ);
        }
    }
}
