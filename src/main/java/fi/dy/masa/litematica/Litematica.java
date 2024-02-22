package fi.dy.masa.litematica;

import fi.dy.masa.malilib.compat.neoforge.ForgePlatformUtils;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.litematica.config.Configs;

@Mod(Reference.MOD_ID)
public class Litematica {
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public Litematica() {
        if (FMLLoader.getDist().isClient()) {
            this.onInitializeClient();
        }
    }

    public void onInitializeClient() {
        ForgePlatformUtils.getInstance().getClientModIgnoredServerOnly();
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        ForgePlatformUtils.getInstance().getMod(Reference.MOD_ID).registerModConfigScreen((screen) -> {
            GuiConfigs gui = new GuiConfigs();
            gui.setParent(screen);
            return gui;
        });
    }

    public static void debugLog(String msg, Object... args) {
        if (Configs.Generic.DEBUG_LOGGING.getBooleanValue()) {
            Litematica.logger.info(msg, args);
        }
    }
}
