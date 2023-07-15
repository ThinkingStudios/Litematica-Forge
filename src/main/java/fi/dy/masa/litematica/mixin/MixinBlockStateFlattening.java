package fi.dy.masa.litematica.mixin;

import fi.dy.masa.litematica.schematic.conversion.SchematicConversionMaps;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockStateFlattening.class)
public abstract class MixinBlockStateFlattening
{
    @Inject(method = "putStates", at = @At("HEAD"))
    private static void onAddEntry(int id, String fixedNBT, String[] sourceNBTs, CallbackInfo ci)
    {
        SchematicConversionMaps.addEntry(id, fixedNBT, sourceNBTs);
    }
}
