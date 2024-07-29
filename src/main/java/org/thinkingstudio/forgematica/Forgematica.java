package org.thinkingstudio.forgematica;

import fi.dy.masa.litematica.InitHandler;
import fi.dy.masa.litematica.Reference;
import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import org.thinkingstudio.mafglib.util.NeoUtils;

@Mod(Reference.MOD_ID)
public class Forgematica {
    public Forgematica(ModContainer modContainer) {
        if (FMLLoader.getDist().isClient()) {
            NeoUtils.getInstance().getClientModIgnoredServerOnly(modContainer);
            InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());

            NeoUtils.getInstance().registerModConfigScreen(modContainer, (screen) -> {
                GuiConfigs gui = new GuiConfigs();
                gui.setParent(screen);
                return gui;
            });
        }
    }
}
