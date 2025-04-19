package org.thinkingstudio.forgematica;

import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.Reference;
import fi.dy.masa.litematica.gui.GuiConfigs;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingstudio.forgematica.incompatibility.IncompatModChecker;
import org.thinkingstudio.mafglib.util.ForgeUtils;

@Mod(Reference.MOD_ID)
public class Forgematica {
    public static final Logger LOGGER = LoggerFactory.getLogger(Reference.MOD_ID);

    @SuppressWarnings("removal")
    public Forgematica() {
        if (FMLLoader.getDist().isClient()) {
            ModContainer modContainer = ModLoadingContext.get().getActiveContainer();

            ForgeUtils.getInstance().getClientModIgnoredServerOnly(modContainer);
            Litematica.onInitialize();

            ForgeUtils.getInstance().registerModConfigScreen(modContainer, (screen) -> {
                GuiConfigs gui = new GuiConfigs();
                gui.setParent(screen);
                return gui;
            });

            if (IncompatModChecker.isValkyrienSkiesLoaded()) {
                LOGGER.warn("Forgematica has detected that ValkyrienSkies (2.4.0 below) is loaded, this mod is reported by issue to be potentially incompatible with Forgematica.");
                LOGGER.warn("Forgematica is not compatible with ValkyrienSkies (2.4.0 below) related issue: https://github.com/ThinkingStudios/Litematica-Forge/issues/69");
            }
        }
    }
}
