package fi.dy.masa.litematica.compat.modmenu;

import fi.dy.masa.litematica.gui.GuiConfigs;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.thinkingstudio.mafglib.loader.entrypoints.ConfigScreenEntrypoint;

public class ModMenuImpl implements ConfigScreenEntrypoint
{
    @Override
    public IConfigScreenFactory getModConfigScreenFactory()
    {
        return (modContainer, screen) -> {
            GuiConfigs gui = new GuiConfigs();
            gui.setParent(screen);
            return gui;
        };
    }
}
