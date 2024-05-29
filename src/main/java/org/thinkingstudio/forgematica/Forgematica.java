package org.thinkingstudio.forgematica;

import fi.dy.masa.litematica.InitHandler;
import fi.dy.masa.litematica.Reference;
import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.thinkingstudio.mafglib.util.ForgePlatformUtils;

@Mod(Reference.MOD_ID)
public class Forgematica {
    public Forgematica() {
        if (FMLLoader.getDist().isClient()) {
            ForgePlatformUtils.getInstance().getClientModIgnoredServerOnly();
            InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
            ForgePlatformUtils.getInstance().registerModConfigScreen(Reference.MOD_ID, (screen) -> {
                GuiConfigs gui = new GuiConfigs();
                gui.setParent(screen);
                return gui;
            });
        }
    }
}
