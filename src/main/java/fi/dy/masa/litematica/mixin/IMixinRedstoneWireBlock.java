package fi.dy.masa.litematica.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RedstoneWireBlock.class)
public interface IMixinRedstoneWireBlock
{
    @Invoker("getPlacementState")
    BlockState litematicaGetPlacementState(BlockView world, BlockState state, BlockPos pos);
}
