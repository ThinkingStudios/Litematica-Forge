package org.thinkingstudio.forgematica;

import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.Reference;
import fi.dy.masa.litematica.gui.GuiConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.thinkingstudio.forgematica.incompatibility.IncompatModChecker;
import org.thinkingstudio.forgematica.incompatibility.IncompatModScreen;
import org.thinkingstudio.mafglib.util.ForgeUtils;

@Mod(Reference.MOD_ID)
public class Forgematica {
    public static boolean firstTitleScreenShown = false;

    public Forgematica(FMLJavaModLoadingContext context) {
        if (FMLLoader.getDist().isClient()) {
            ModContainer modContainer = context.getContainer();

            ForgeUtils.getInstance().getClientModIgnoredServerOnly(modContainer);
            Litematica.onInitialize();

            ForgeUtils.getInstance().registerModConfigScreen(modContainer, (screen) -> {
                GuiConfigs gui = new GuiConfigs();
                gui.setParent(screen);
                return gui;
            });

            MinecraftForge.EVENT_BUS.<ScreenEvent.Init.Pre>addListener(EventPriority.HIGHEST, event -> {
                var screen = event.getScreen();

                if (firstTitleScreenShown || !(screen instanceof TitleScreen)) {
                    return;
                }

                if (IncompatModChecker.isIncompatModLoaded()) {
                    MinecraftClient.getInstance().setScreen(new IncompatModScreen(screen));
                }

                firstTitleScreenShown = true;
            });
        }
    }
}
