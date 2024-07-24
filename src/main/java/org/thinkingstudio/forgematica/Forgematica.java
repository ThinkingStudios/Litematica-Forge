package org.thinkingstudio.forgematica;

import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.Reference;
import fi.dy.masa.litematica.gui.GuiConfigs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import org.thinkingstudio.mafglib.util.NeoUtils;

@Mod(value = Reference.MOD_ID, dist = Dist.CLIENT)
public class Forgematica {
    public Forgematica(ModContainer modContainer) {
        if (FMLLoader.getDist().isClient()) {
            Litematica.onInitialize();

            NeoUtils.getInstance().registerModConfigScreen(modContainer, (screen) -> {
                GuiConfigs gui = new GuiConfigs();
                gui.setParent(screen);
                return gui;
            });
        }
    }
}
