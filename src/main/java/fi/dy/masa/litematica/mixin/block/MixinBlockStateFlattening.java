package fi.dy.masa.litematica.mixin.block;

import java.util.Arrays;
import java.util.List;

import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.litematica.schematic.conversion.SchematicConversionMaps;

@Mixin(BlockStateFlattening.class)
public abstract class MixinBlockStateFlattening
{
    @Inject(method = "putStates", at = @At("HEAD"))
    private static void litematica_onAddEntry(int oldIdAndMeta, Dynamic<?> newStateDynamic, Dynamic<?>[] oldStateDynamics, CallbackInfo ci)
    {
//        String fixedNBT = newStateDynamic.get("Name").asString("");
//        List<String> oldStates = new ArrayList<>();
        List<Dynamic<?>> oldDynamics = Arrays.stream(oldStateDynamics).toList();

//        System.out.printf("putStates - Dynamic<> [%s] // fixedNbt [%s] --> ", newStateDynamic.toString(), fixedNBT);
//
//        for (Dynamic<?> entry : oldStateDynamics)
//        {
//            String result = entry.get("Name").asString("");
//
//            System.out.printf("Dynamic<> [%s] // old result [%s]", entry.toString(), result);
//
//            if (!result.isEmpty())
//            {
//                oldStates.add(result);
//            }
//        }
//
//        System.out.print("\n");

        // TODO we should probably implement using the Dynamic<?> interface directly
//        SchematicConversionMaps.addEntry(oldIdAndMeta, fixedNBT, oldStates.toArray(new String[0]));
        SchematicConversionMaps.addDynamicEntry(oldIdAndMeta, newStateDynamic, oldDynamics);
    }
}
