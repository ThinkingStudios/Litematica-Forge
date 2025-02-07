package org.thinkingstudio.forgematica.incompatibility;

import net.minecraft.text.Text;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class IncompatModChecker {
    public static List<String> INCOMPAT_MODID = List.of("valkyrienskies");

    public static boolean isIncompatModLoaded() {
        for (String modId : INCOMPAT_MODID) {
            return ModList.get().isLoaded(modId);
        }
        return false;
    }

    public static String getIncompatModNames() {
        for (String modId : INCOMPAT_MODID) {
            return ModList.get().getModContainerById(modId).orElseThrow().getModInfo().getDisplayName();
        }
        return Text.empty().getString();
    }
}
