package fi.dy.masa.litematica.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import fi.dy.masa.litematica.scheduler.TaskScheduler;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.UserCache;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.net.Proxy;
import java.util.function.BooleanSupplier;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MinecraftServer
{
    private MixinIntegratedServer(Thread serverThread,
                                  LevelStorage.Session session,
                                  ResourcePackManager dataPackManager,
                                  SaveLoader saveLoader,
                                  Proxy proxy,
                                  DataFixer dataFixer,
                                  @Nullable MinecraftSessionService sessionService,
                                  @Nullable GameProfileRepository gameProfileRepo,
                                  @Nullable UserCache userCache,
                                  WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory)
    {
        super(serverThread, session, dataPackManager, saveLoader, proxy, dataFixer, sessionService, gameProfileRepo, userCache, worldGenerationProgressListenerFactory);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", shift = Shift.AFTER,
            target = "Lnet/minecraft/server/MinecraftServer;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void onPostTick(BooleanSupplier supplier, CallbackInfo ci)
    {
        TaskScheduler.getInstanceServer().runTasks();
    }
}
