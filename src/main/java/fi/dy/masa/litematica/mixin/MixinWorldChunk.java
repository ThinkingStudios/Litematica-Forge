package fi.dy.masa.litematica.mixin;

import fi.dy.masa.litematica.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(value = WorldChunk.class, priority = 999)
public abstract class MixinWorldChunk implements Chunk {
    @Shadow @Final private ChunkSection[] sections;
    @Shadow @Final @Nullable public static ChunkSection EMPTY_SECTION;
    @Shadow @Final private Map<Heightmap.Type, Heightmap> heightmaps;
    @Shadow @Final private World world;
    @Shadow private volatile boolean shouldSave;
    @Shadow @Nullable public abstract BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType);

    /**
     * @author Kasualix
     * @reason fuck setBlockState
     */
    @Overwrite
    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        ChunkSection chunksection = this.sections[j >> 4];
        if (chunksection == EMPTY_SECTION) {
            if (state.isAir()) return null;
            chunksection = new ChunkSection(j >> 4 << 4);
            this.sections[j >> 4] = chunksection;
        }

        boolean flag = chunksection.isEmpty();
        BlockState blockstate = chunksection.setBlockState(i, j & 15, k, state);
        if (blockstate == state) {
            return null;
        } else {
            Block block = state.getBlock();
            Block block1 = blockstate.getBlock();
            (this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING)).trackUpdate(i, j, k, state);
            (this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).trackUpdate(i, j, k, state);
            (this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR)).trackUpdate(i, j, k, state);
            (this.heightmaps.get(Heightmap.Type.WORLD_SURFACE)).trackUpdate(i, j, k, state);
            boolean flag1 = chunksection.isEmpty();
            if (flag != flag1) {
                this.world.getChunkManager().getLightingProvider().setSectionStatus(pos, flag1);
            }

            if (!this.world.isClient) {
                blockstate.onStateReplaced(this.world, pos, state, moved);
            } else if ((block1 != block || !state.hasTileEntity()) && blockstate.hasTileEntity()) {
                this.world.removeBlockEntity(pos);
            }

            if (!chunksection.getBlockState(i, j & 15, k).isOf(block)) {
                return null;
            } else {
                BlockEntity tileentity1;
                if (blockstate.hasTileEntity()) {
                    tileentity1 = this.getBlockEntity(pos, WorldChunk.CreationType.CHECK);
                    if (tileentity1 != null) {
                        tileentity1.resetBlock();
                    }
                }

                if (!(WorldUtils.shouldPreventBlockUpdates(world) || this.world.isClient) && !this.world.captureBlockSnapshots) {
                    state.onBlockAdded(this.world, pos, blockstate, moved);
                }

                if (state.hasTileEntity()) {
                    tileentity1 = this.getBlockEntity(pos, WorldChunk.CreationType.CHECK);
                    if (tileentity1 == null) {
                        tileentity1 = state.createTileEntity(this.world);
                        this.world.setBlockEntity(pos, tileentity1);
                    } else {
                        tileentity1.resetBlock();
                    }
                }

                this.shouldSave = true;
                return blockstate;
            }
        }
    }
}