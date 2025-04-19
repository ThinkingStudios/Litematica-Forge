package org.thinkingstudio.forgematica;

import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.Reference;
import fi.dy.masa.litematica.compat.modmenu.ModMenuImpl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

import org.thinkingstudio.mafglib.loader.entrypoints.ConfigScreenEntrypoint;
import org.thinkingstudio.mafglib.loader.entrypoints.ModInitializer;

@Mod(value = Reference.MOD_ID, dist = Dist.CLIENT)
public class Forgematica {
    public Forgematica(ModContainer modContainer) {
        if (FMLLoader.getDist().isClient()) {
//            modContainer.registerExtensionPoint(ConfigScreenEntrypoint.class, new ModMenuImpl());
//            modContainer.registerExtensionPoint(ModInitializer.class, new Litematica());
            new Litematica().onInitialize();
        }
    }
}
