package fi.dy.masa.litematica.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FenceGateBlock.class)
public interface IMixinFenceGateBlock
{
    @Invoker("isWall")
    boolean invokeIsWall(BlockState state);
}
