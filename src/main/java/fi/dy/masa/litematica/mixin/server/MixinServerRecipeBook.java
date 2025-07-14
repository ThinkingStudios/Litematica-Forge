package fi.dy.masa.litematica.mixin.server;

import java.util.*;

import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.config.Configs;

@Mixin(ServerRecipeBook.class)
public abstract class MixinServerRecipeBook
{
    @Shadow @Final protected Set<RegistryKey<Recipe<?>>> unlocked;
    @Shadow @Final private ServerRecipeBook.DisplayCollector collector;

    @Inject(method = "sendInitRecipesPacket", at = @At("TAIL"))
    private void litematica_updateRemainingLockedRecipesToClient(ServerPlayerEntity player, CallbackInfo ci)
    {
        if (Configs.Generic.RECIPE_BOOK_SP_SHADOW_UNLOCK.getBooleanValue())
        {
            Collection<RecipeEntry<?>> serverRecipes = Objects.requireNonNull(player.getServer()).getRecipeManager().values();
            Set<RegistryKey<Recipe<?>>> append = new HashSet<>();

            for (RecipeEntry<?> entry : serverRecipes)
            {
                RegistryKey<Recipe<?>> key = entry.id();

                if (!this.unlocked.contains(key))
                {
                    append.add(key);
                }
            }

            List<RecipeBookAddS2CPacket.Entry> list = new ArrayList<>(append.size());

            for (RegistryKey<Recipe<?>> key : append)
            {
                this.collector.displaysForRecipe(
                        key, (display) ->
                                list.add(
                                        new RecipeBookAddS2CPacket.Entry(display, false, false)
                                )
                );
            }

            if (!list.isEmpty())
            {
                Litematica.LOGGER.info("RecipeBookShadowUnlock: Shadow-unlocking [{}] additional server recipes.", list.size());
                player.networkHandler.send(new RecipeBookAddS2CPacket(list, true));
            }
        }
    }
}
