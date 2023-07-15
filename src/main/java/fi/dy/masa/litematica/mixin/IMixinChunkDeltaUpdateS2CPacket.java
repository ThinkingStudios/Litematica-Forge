package fi.dy.masa.litematica.mixin;

import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public interface IMixinChunkDeltaUpdateS2CPacket
{
    @Accessor("sectionPos")
    ChunkSectionPos litematica_getSection();
}
