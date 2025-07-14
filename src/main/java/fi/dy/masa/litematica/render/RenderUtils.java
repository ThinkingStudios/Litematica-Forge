package fi.dy.masa.litematica.render;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.mixin.render.IMixinAbstractTexture;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.InventoryOverlay.InventoryProperties;
import fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.game.BlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.util.BlockInfoAlignment;
import fi.dy.masa.litematica.util.InventoryUtils;
import fi.dy.masa.litematica.util.PositionUtils;

public class RenderUtils
{
    private static final LocalRandom RAND = new LocalRandom(0);

    public static int getMaxStringRenderLength(List<String> list)
    {
        int length = 0;

        for (String str : list)
        {
            length = Math.max(length, StringUtils.getStringWidth(str));
        }

        return length;
    }

    /**
     * Assumes a BufferBuilder in the GL_LINES mode has been initialized
     */
    public static void drawDebugBlockModelOutlinesBatched(List<BlockModelPart> modelParts, BlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        for (final BlockModelPart part : modelParts)
        {
            drawDebugBlockModelOutlinesBatched(part, state, pos, color, expand, buffer);
        }
    }

    public static void drawDebugBlockModelOutlinesBatched(BlockModelPart modelPart, BlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        for (final Direction side : fi.dy.masa.malilib.util.position.PositionUtils.ALL_DIRECTIONS)
        {
            renderDebugModelQuadOutlines(modelPart, state, pos, side, color, expand, buffer);
        }

        renderDebugModelQuadOutlines(modelPart, state, pos, null, color, expand, buffer);
    }

    public static void renderDebugModelQuadOutlines(BlockModelPart modelPart, BlockState state, BlockPos pos, Direction side, Color4f color, double expand, BufferBuilder buffer)
    {
        try
        {
            // model.getQuads(state, side, RAND)
            renderDebugModelQuadOutlines(pos, buffer, color, modelPart.getQuads(side));
        }
        catch (Exception ignore) {}
    }

    public static void renderDebugModelQuadOutlines(BlockPos pos, BufferBuilder buffer, Color4f color, List<BakedQuad> quads)
    {
        final int size = quads.size();

        for (int i = 0; i < size; i++)
        {
            renderDebugQuadOutlinesBatched(pos, buffer, color, quads.get(i).vertexData());
        }
    }

    public static void renderDebugQuadOutlinesBatched(BlockPos pos, BufferBuilder buffer, Color4f color, int[] vertexData)
    {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final int vertexSize = vertexData.length / 4;
        final float fx[] = new float[4];
        final float fy[] = new float[4];
        final float fz[] = new float[4];

        for (int index = 0; index < 4; ++index)
        {
            fx[index] = x + Float.intBitsToFloat(vertexData[index * vertexSize    ]);
            fy[index] = y + Float.intBitsToFloat(vertexData[index * vertexSize + 1]);
            fz[index] = z + Float.intBitsToFloat(vertexData[index * vertexSize + 2]);
        }

        buffer.vertex(fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a);
        buffer.vertex(fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a);

        buffer.vertex(fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a);
        buffer.vertex(fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a);

        buffer.vertex(fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a);
        buffer.vertex(fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a);

        buffer.vertex(fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a);
        buffer.vertex(fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a);
    }

    public static void drawBlockModelOutlinesBatched(List<BlockModelPart> modelParts, BlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer, MatrixStack matricies)
    {
        for (final BlockModelPart part : modelParts)
        {
            drawlockModelOutlinesBatched(part, state, pos, color, expand, buffer, matricies);
        }
    }

    public static void drawlockModelOutlinesBatched(BlockModelPart modelPart, BlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer, MatrixStack matricies)
    {
        for (final Direction side : fi.dy.masa.malilib.util.position.PositionUtils.ALL_DIRECTIONS)
        {
            renderModelQuadOutlines(modelPart, state, pos, side, color, expand, buffer, matricies);
        }

        renderModelQuadOutlines(modelPart, state, pos, null, color, expand, buffer, matricies);
    }

    public static void renderModelQuadOutlines(BlockModelPart modelPart, BlockState state, BlockPos pos, Direction side, Color4f color, double expand, BufferBuilder buffer, MatrixStack matricies)
    {
        try
        {
            // model.getQuads(state, side, RAND)
            renderModelQuadOutlines(pos, buffer, color, modelPart.getQuads(side), matricies);
        }
        catch (Exception ignore) {}
    }

    public static void renderModelQuadOutlines(BlockPos pos, BufferBuilder buffer, Color4f color, List<BakedQuad> quads, MatrixStack matricies)
    {
        final int size = quads.size();

        for (int i = 0; i < size; i++)
        {
            renderQuadOutlinesBatched(pos, buffer, color, quads.get(i).vertexData(), matricies);
        }
    }

    public static void renderQuadOutlinesBatched(BlockPos pos, BufferBuilder buffer, Color4f color, int[] vertexData, MatrixStack matricies)
    {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final int vertexSize = vertexData.length / 4;
        final float fx[] = new float[4];
        final float fy[] = new float[4];
        final float fz[] = new float[4];

        for (int index = 0; index < 4; ++index)
        {
            fx[index] = x + Float.intBitsToFloat(vertexData[index * vertexSize    ]);
            fy[index] = y + Float.intBitsToFloat(vertexData[index * vertexSize + 1]);
            fz[index] = z + Float.intBitsToFloat(vertexData[index * vertexSize + 2]);
        }

        MatrixStack.Entry e = matricies.peek();

        buffer.vertex(e, fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
    }

    public static boolean stateModelHasQuads(BlockState state)
    {
        return modelHasQuads(Objects.requireNonNull(MinecraftClient.getInstance().getBlockRenderManager().getModel(state)));
    }

    public static boolean modelHasQuads(@Nonnull BlockStateModel model)
    {
        return hasQuads(model.getParts(new LocalRandom(0)));
    }

    public static boolean hasQuads(List<BlockModelPart> modelParts)
    {
        if (modelParts.isEmpty()) return false;
        int totalSize = 0;

        for (BlockModelPart part : modelParts)
        {
            for (Direction face : fi.dy.masa.malilib.util.position.PositionUtils.ALL_DIRECTIONS)
            {
                totalSize += part.getQuads(face).size();
            }

            totalSize += part.getQuads(null).size();
        }

        return totalSize > 0;
    }

    public static void drawBlockModelQuadOverlayBatched(List<BlockModelPart> modelParts, BlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
//        System.out.printf("drawBlockModelQuadOverlayBatched - pos [%s], parts [%d], state [%s]\n", pos.toShortString(), modelParts.size(), state.toString());

        for (final BlockModelPart part : modelParts)
        {
            drawBlockModelQuadOverlayBatched(part, state, pos, color, expand, buffer);
        }
    }

    public static void drawBlockModelQuadOverlayBatched(BlockModelPart modelPart, BlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        for (final Direction side : fi.dy.masa.malilib.util.position.PositionUtils.ALL_DIRECTIONS)
        {
            drawBlockModelQuadOverlayBatched(modelPart, state, pos, side, color, expand, buffer);
        }

        drawBlockModelQuadOverlayBatched(modelPart, state, pos, null, color, expand, buffer);
    }

    public static void drawBlockModelQuadOverlayBatched(BlockModelPart modelPart, BlockState state, BlockPos pos, Direction side, Color4f color, double expand, BufferBuilder buffer)
    {
//        System.out.printf("drawBlockModelQuadOverlayBatched - pos [%s], side [%s], state [%s]\n", pos.toShortString(), side != null ? side.asString() : "<null>", state.toString());
        renderModelQuadOverlayBatched(pos, buffer, color, modelPart.getQuads(side));
    }

    private static void renderModelQuadOverlayBatched(BlockPos pos, BufferBuilder buffer, Color4f color, List<BakedQuad> quads)
    {
        //final int size = quads.size();
//        System.out.printf("renderModelQuadOverlayBatched - pos [%s], quads [%d]\n", pos.toShortString(), quads.size());

        for (final BakedQuad quad : quads)
        {
            renderModelQuadOverlayBatched(pos, buffer, color, quad);
        }
    }

    private static void renderModelQuadOverlayBatched(BlockPos pos, BufferBuilder buffer, Color4f color, BakedQuad quad)
    {
        final int[] vertexData = quad.vertexData();
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final int vertexSize = vertexData.length / 4;
        float fx, fy, fz;

        for (int index = 0; index < 4; ++index)
        {
            fx = x + Float.intBitsToFloat(vertexData[index * vertexSize    ]);
            fy = y + Float.intBitsToFloat(vertexData[index * vertexSize + 1]);
            fz = z + Float.intBitsToFloat(vertexData[index * vertexSize + 2]);

            buffer.vertex(fx, fy, fz).color(color.r, color.g, color.b, color.a);
        }
    }

    public static void drawBlockBoxBatchedQuads(BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        for (Direction side : fi.dy.masa.malilib.util.position.PositionUtils.ALL_DIRECTIONS)
        {
            drawBlockBoxSideBatchedQuads(pos, side, color, expand, buffer);
        }
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBlockBoxSideBatchedQuads(BlockPos pos, Direction side, Color4f color, double expand, BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - expand);
        float minY = (float) (pos.getY() - expand);
        float minZ = (float) (pos.getZ() - expand);
        float maxX = (float) (pos.getX() + expand + 1);
        float maxY = (float) (pos.getY() + expand + 1);
        float maxZ = (float) (pos.getZ() + expand + 1);

        switch (side)
        {
            case DOWN:
                buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
                break;

            case UP:
                buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
                break;

            case NORTH:
                buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
                break;

            case SOUTH:
                buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
                break;

            case WEST:
                buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
                break;

            case EAST:
                buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
                buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
                break;
        }
    }

    public static void drawBlockBoxEdgeBatchedLines(BlockPos pos, Direction.Axis axis, int cornerIndex, Color4f color, BufferBuilder buffer)
    {
        Vec3i offset = PositionUtils.getEdgeNeighborOffsets(axis, cornerIndex)[cornerIndex];

        double minX = pos.getX() + offset.getX();
        double minY = pos.getY() + offset.getY();
        double minZ = pos.getZ() + offset.getZ();
        double maxX = pos.getX() + offset.getX() + (axis == Direction.Axis.X ? 1 : 0);
        double maxY = pos.getY() + offset.getY() + (axis == Direction.Axis.Y ? 1 : 0);
        double maxZ = pos.getZ() + offset.getZ() + (axis == Direction.Axis.Z ? 1 : 0);

        buffer.vertex((float) minX, (float) minY, (float) minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex((float) maxX, (float) maxY, (float) maxZ).color(color.r, color.g, color.b, color.a);

        //System.out.printf("pos: %s, axis: %s, ind: %d\n", pos, axis, cornerIndex);
    }

    public static void drawBlockBoxEdgeBatchedDebugLines(BlockPos pos, Direction.Axis axis, int cornerIndex, Color4f color, BufferBuilder buffer)
    {
        Vec3i offset = PositionUtils.getEdgeNeighborOffsets(axis, cornerIndex)[cornerIndex];

        double minX = pos.getX() + offset.getX();
        double minY = pos.getY() + offset.getY();
        double minZ = pos.getZ() + offset.getZ();
        double maxX = pos.getX() + offset.getX() + (axis == Direction.Axis.X ? 1 : 0);
        double maxY = pos.getY() + offset.getY() + (axis == Direction.Axis.Y ? 1 : 0);
        double maxZ = pos.getZ() + offset.getZ() + (axis == Direction.Axis.Z ? 1 : 0);

        //System.out.printf("pos: %s, axis: %s, ind: %d\n", pos, axis, cornerIndex);
        buffer.vertex((float) minX, (float) minY, (float) minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex((float) maxX, (float) maxY, (float) maxZ).color(color.r, color.g, color.b, color.a);
    }

    public static int renderInventoryOverlays(DrawContext drawContext, BlockInfoAlignment align, int offY, World worldSchematic, World worldClient, BlockPos pos, MinecraftClient mc)
    {
        int heightSch = renderInventoryOverlay(drawContext, align, LeftRight.LEFT, offY, worldSchematic, pos, mc);
        int heightCli = renderInventoryOverlay(drawContext, align, LeftRight.RIGHT, offY, worldClient, pos, mc);

        return Math.max(heightSch, heightCli);
    }

    public static int renderInventoryOverlay(DrawContext drawContext, BlockInfoAlignment align, LeftRight side, int offY,
            World world, BlockPos pos, MinecraftClient mc)
    {
        InventoryOverlay.Context ctx = InventoryUtils.getTargetInventory(world, pos);

        if (ctx != null && ctx.inv() != null)
        {
            final InventoryProperties props = fi.dy.masa.malilib.render.InventoryOverlay.getInventoryPropsTemp(ctx.type(), ctx.inv().size());

//            Litematica.LOGGER.error("render(): type [{}], inv [{}], be [{}], nbt [{}]", ctx.type().name(), ctx.inv().size(), ctx.be() != null, ctx.nbt() != null ? ctx.nbt().getString("id") : new NbtCompound());

            // Try to draw Locked Slots on Crafter Grid
            if (ctx.type() == InventoryRenderType.CRAFTER)
            {
                Set<Integer> disabledSlots = new HashSet<>();

                if (ctx.nbt() != null && !ctx.nbt().isEmpty())
                {
                    disabledSlots = NbtBlockUtils.getDisabledSlotsFromNbt(ctx.nbt());
                }
                else if (ctx.be() instanceof CrafterBlockEntity cbe)
                {
                    disabledSlots = BlockUtils.getDisabledSlots(cbe);
                }

                return renderInventoryOverlay(drawContext, align, side, offY, ctx.inv(), ctx.type(), props, disabledSlots, mc);
            }
            else
            {
                return renderInventoryOverlay(drawContext, align, side, offY, ctx.inv(), ctx.type(), props, mc);
            }
        }

        return 0;
    }

    public static int renderInventoryOverlay(DrawContext drawContext, BlockInfoAlignment align, LeftRight side, int offY,
                                             Inventory inv, InventoryRenderType type, InventoryProperties props,
                                             MinecraftClient mc)
    {
        return renderInventoryOverlay(drawContext, align, side, offY, inv, type, props, Set.of(), mc);
    }

    public static int renderInventoryOverlay(DrawContext drawContext, BlockInfoAlignment align, LeftRight side, int offY,
                                             Inventory inv, InventoryRenderType type, InventoryProperties props, Set<Integer> disabledSlots,
                                             MinecraftClient mc)
    {
        int xInv = 0;
        int yInv = 0;
        int compatShift = OverlayRenderer.calculateCompatYShift();

        switch (align)
        {
            case CENTER:
                xInv = GuiUtils.getScaledWindowWidth() / 2 - (props.width / 2);
                yInv = GuiUtils.getScaledWindowHeight() / 2 - props.height - offY;
                break;
            case TOP_CENTER:
                xInv = GuiUtils.getScaledWindowWidth() / 2 - (props.width / 2);
                yInv = offY + compatShift;
                break;
        }

        if      (side == LeftRight.LEFT)  { xInv -= (props.width / 2 + 4); }
        else if (side == LeftRight.RIGHT) { xInv += (props.width / 2 + 4); }

//        fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryBackground(drawContext, type, xInv, yInv, props.slotsPerRow, props.totalSlots, mc);
        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryStacks(drawContext, type, inv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, 0, inv.size(), disabledSlots, mc);

        return props.height + compatShift;
    }

    public static void renderBackgroundMask(DrawContext drawContext, int startX, int startY, int width, int height)
    {
        fi.dy.masa.malilib.render.RenderUtils.drawTexturedRect(drawContext, GuiBase.BG_TEXTURE, startX, startY, 0, 0, width, height);
    }

    /*
    private static void renderModelBrightnessColor(IBlockState state, IBakedModel model, float brightness, float r, float g, float b)
    {
        for (EnumFacing facing : EnumFacing.values())
        {
            renderModelBrightnessColorQuads(brightness, r, g, b, model.getQuads(state, facing, 0L));
        }

        renderModelBrightnessColorQuads(brightness, r, g, b, model.getQuads(state, null, 0L));
    }

    private static void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int i = 0;

        for (int j = listQuads.size(); i < j; ++i)
        {
            BakedQuad quad = listQuads.get(i);
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
            bufferbuilder.addVertexData(quad.getVertexData());

            if (quad.hasTintIndex())
            {
                bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            }
            else
            {
                bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
            }

            Vec3i direction = quad.getFace().getDirectionVec();
            bufferbuilder.putNormal(direction.getX(), direction.getY(), direction.getZ());

            tessellator.draw();
        }
    }
    */

    /*
    private static void renderModel(final IBlockState state, final IBakedModel model, final BlockPos pos, final int alpha)
    {
        //BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        //dispatcher.getBlockModelRenderer().renderModelBrightnessColor(model, 1f, 1f, 1f, 1f);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

        for (final EnumFacing facing : EnumFacing.values())
        {
            renderQuads(state, pos, buffer, model.getQuads(state, facing, 0), alpha);
        }

        renderQuads(state, pos, buffer, model.getQuads(state, null, 0), alpha);
        tessellator.draw();
    }

    private static void renderQuads(final IBlockState state, final BlockPos pos, final BufferBuilder buffer, final List<BakedQuad> quads, final int alpha)
    {
        final int size = quads.size();

        for (int i = 0; i < size; i++)
        {
            final BakedQuad quad = quads.get(i);
            final int color = quad.getTintIndex() == -1 ? alpha | 0xffffff : getTint(state, pos, alpha, quad.getTintIndex());
            //LightUtil.renderQuadColor(buffer, quad, color);
            renderQuad(buffer, quad, color);
        }
    }

    public static void renderQuad(BufferBuilder buffer, BakedQuad quad, int auxColor)
    {
        buffer.addVertexData(quad.getVertexData());
        putQuadColor(buffer, quad, auxColor);
    }

    private static int getTint(final IBlockState state, final BlockPos pos, final int alpha, final int tintIndex)
    {
        Minecraft mc = Minecraft.getMinecraft();
        return alpha | mc.getBlockColors().colorMultiplier(state, null, pos, tintIndex);
    }

    private static void putQuadColor(BufferBuilder buffer, BakedQuad quad, int color)
    {
        float cb = color & 0xFF;
        float cg = (color >>> 8) & 0xFF;
        float cr = (color >>> 16) & 0xFF;
        float ca = (color >>> 24) & 0xFF;
        VertexFormat format = DefaultVertexFormats.ITEM; //quad.getFormat();
        int size = format.getIntegerSize();
        int offset = format.getColorOffset() / 4; // assumes that color is aligned

        for (int i = 0; i < 4; i++)
        {
            int vc = quad.getVertexData()[offset + size * i];
            float vcr = vc & 0xFF;
            float vcg = (vc >>> 8) & 0xFF;
            float vcb = (vc >>> 16) & 0xFF;
            float vca = (vc >>> 24) & 0xFF;
            int ncr = Math.min(0xFF, (int)(cr * vcr / 0xFF));
            int ncg = Math.min(0xFF, (int)(cg * vcg / 0xFF));
            int ncb = Math.min(0xFF, (int)(cb * vcb / 0xFF));
            int nca = Math.min(0xFF, (int)(ca * vca / 0xFF));

            IBufferBuilder bufferMixin = (IBufferBuilder) buffer;
            bufferMixin.putColorRGBA(bufferMixin.getColorIndexAccessor(4 - i), ncr, ncg, ncb, nca);
        }
    }
    */

    /*
    public static void renderQuadColorSlow(BufferBuilder wr, BakedQuad quad, int auxColor)
    {
        ItemConsumer cons;

        if(wr == Tessellator.getInstance().getBuffer())
        {
            cons = getItemConsumer();
        }
        else
        {
            cons = new ItemConsumer(new VertexBufferConsumer(wr));
        }

        float b = (float)  (auxColor & 0xFF) / 0xFF;
        float g = (float) ((auxColor >>>  8) & 0xFF) / 0xFF;
        float r = (float) ((auxColor >>> 16) & 0xFF) / 0xFF;
        float a = (float) ((auxColor >>> 24) & 0xFF) / 0xFF;

        cons.setAuxColor(r, g, b, a);
        quad.pipe(cons);
    }

    public static void renderQuadColor(BufferBuilder wr, BakedQuad quad, int auxColor)
    {
        if (quad.getFormat().equals(wr.getVertexFormat())) 
        {
            wr.addVertexData(quad.getVertexData());
            ForgeHooksClient.putQuadColor(wr, quad, auxColor);
        }
        else
        {
            renderQuadColorSlow(wr, quad, auxColor);
        }
    }
    */

    public static void drawBlockBoundingBoxOutlinesBatchedDebugLines(BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        drawBoxAllEdgesBatchedDebugLines(pos, Vec3d.ZERO, color, expand, buffer);
    }

    public static void drawBoxAllEdgesBatchedDebugLines(BlockPos pos, Vec3d cameraPos, Color4f color, double expand, BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - expand - cameraPos.x);
        float minY = (float) (pos.getY() - expand - cameraPos.y);
        float minZ = (float) (pos.getZ() - expand - cameraPos.z);
        float maxX = (float) (pos.getX() + expand - cameraPos.x + 1);
        float maxY = (float) (pos.getY() + expand - cameraPos.y + 1);
        float maxZ = (float) (pos.getZ() + expand - cameraPos.z + 1);

        drawBoxAllEdgesBatchedDebugLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    public static void drawBoxAllEdgesBatchedDebugLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);

        // East side
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
    }
}
