package org.thinkingstudio.forgematica;

import fi.dy.masa.litematica.InitHandler;
import fi.dy.masa.litematica.Reference;
import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.thinkingstudio.mafglib.util.ForgeUtils;

@Mod(Reference.MOD_ID)
public class Forgematica {
    public Forgematica() {
        if (FMLLoader.getDist().isClient()) {
            ModContainer modContainer = ModLoadingContext.get().getActiveContainer();

            ForgeUtils.getInstance().getClientModIgnoredServerOnly(modContainer);
            InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());

            ForgeUtils.getInstance().registerModConfigScreen(modContainer, (screen) -> {
                GuiConfigs gui = new GuiConfigs();
                gui.setParent(screen);
                return gui;
            });
        }
    }
}
