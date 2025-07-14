package fi.dy.masa.litematica.compat.lwgl;

import fi.dy.masa.malilib.compat.lwgl.GpuCompat;
import fi.dy.masa.litematica.config.Configs;

/**
 * Makes an attempt to adjust Visual Configs for different GPU Models to help reduce crashes
 */
public class RenderCompat
{
    public static void checkGpuVisuals()
    {
        // TODO
//        if (Configs.Visuals.SCHEMATIC_OVERLAY_ENABLE_RESORTING.getBooleanValue() && !GpuCompat.isNvidiaGpu())
//        {
//            Configs.Visuals.SCHEMATIC_OVERLAY_ENABLE_RESORTING.setBooleanValue(false);
//        }
    }
}
