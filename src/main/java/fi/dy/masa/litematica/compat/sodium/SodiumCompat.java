package fi.dy.masa.litematica.compat.sodium;

import fi.dy.masa.litematica.mixin.render.IMixinGameRenderer;
import net.minecraft.client.MinecraftClient;
import org.thinkingstudio.mafglib.loader.FoxifiedLoader;

public class SodiumCompat
{
    private static boolean hasSodium;
    private static boolean wasBlockOutlineEnabled;

    public static void checkForSodium()
    {
        hasSodium = FoxifiedLoader.isModLoaded("sodium");
    }

    public static boolean hasSodium() { return hasSodium; }

    public static void startBlockOutlineEnabled()
    {
        wasBlockOutlineEnabled = ((IMixinGameRenderer) MinecraftClient.getInstance().gameRenderer).litematica_isBlockOutlineEnabled();

        if (!wasBlockOutlineEnabled())
        {
            MinecraftClient.getInstance().gameRenderer.setBlockOutlineEnabled(true);
        }
    }

    public static void endBlockOutlineEnabled()
    {
        MinecraftClient.getInstance().gameRenderer.setBlockOutlineEnabled(wasBlockOutlineEnabled());
    }

    public static boolean wasBlockOutlineEnabled() { return wasBlockOutlineEnabled; }

    static
    {
        checkForSodium();
    }
}
