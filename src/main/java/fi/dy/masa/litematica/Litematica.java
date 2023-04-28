package fi.dy.masa.litematica;

import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.malilib.compat.forge.ForgePlatformCompat;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.malilib.event.InitializationHandler;

@Mod(Reference.MOD_ID)
public class Litematica {
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public Litematica() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());

        ForgePlatformCompat.getInstance().getMod(Reference.MOD_ID).registerModConfigScreen((screen) -> {
            GuiConfigs gui = new GuiConfigs();
            gui.setParent(screen);
            return gui;
        });
    }
}
