package fi.dy.masa.litematica;

import net.minecraft.MinecraftVersion;
import fi.dy.masa.malilib.util.StringUtils;

public class Reference
{
    public static final String PORT_ID = "forgematica";
    public static final String MOD_ID = "litematica";
    public static final String MOD_NAME = "Litematica";
    public static final String MOD_VERSION = StringUtils.getModVersionString(PORT_ID);
    public static final String MC_VERSION = MinecraftVersion.CURRENT.getName();
    public static final String MOD_TYPE = "neoforge";
    public static final String MOD_STRING = MOD_ID+"-"+MOD_TYPE+"-"+MC_VERSION+"-"+MOD_VERSION;
}
