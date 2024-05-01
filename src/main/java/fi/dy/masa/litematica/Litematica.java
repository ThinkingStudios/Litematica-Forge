package fi.dy.masa.litematica;

import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.malilib.compat.forge.ForgePlatformUtils;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class Litematica {
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public Litematica() {
        if (FMLLoader.getDist().isClient()) {
            ForgePlatformUtils.getInstance().getClientModIgnoredServerOnly();
            InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
            ForgePlatformUtils.getInstance().getMod(Reference.MOD_ID).registerModConfigScreen((screen) -> {
                GuiConfigs gui = new GuiConfigs();
                gui.setParent(screen);
                return gui;
            });
        }
    }
}
