package fi.dy.masa.litematica;

//import fi.dy.masa.litematica.gui.GuiConfigs;
//import net.minecraftforge.client.ConfigScreenHandler;
//import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Reference.MOD_ID)
public class LitematicaForge {
    public LitematicaForge() {
        // Really need ?
        /*
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraftClient, screen) -> {
                    GuiConfigs gui = new GuiConfigs();
                    gui.setParent(screen);
                    return gui;
                })
        );
         */
    }
}
