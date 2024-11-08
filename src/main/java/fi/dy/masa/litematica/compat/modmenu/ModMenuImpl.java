package fi.dy.masa.litematica.compat.modmenu;

import fi.dy.masa.litematica.gui.GuiConfigs;
import org.thinkingstudio.mafglib.loader.gui.ModConfigScreenFactory;
import org.thinkingstudio.mafglib.loader.gui.ModConfigScreenInitializer;

public class ModMenuImpl implements ModConfigScreenInitializer
{
    @Override
    public ModConfigScreenFactory getModConfigScreenFactory()
    {
        return (modContainer, screen) -> {
            GuiConfigs gui = new GuiConfigs();
            gui.setParent(screen);
            return gui;
        };
    }
}
