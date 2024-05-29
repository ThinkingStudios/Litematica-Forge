package org.thinkingstudio.forgematica.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraftforge.client.model.ModelDataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.litematica.world.WorldSchematic;

@Mixin(ModelDataManager.class)
public abstract class MixinModelDataManager {

    /**
     * if we don't catch this Forge does stupid things
     * it calls {@code requestModelData} on any client world when adding a BlockEntity
     * but if it's not {@code mc.world} it crashes because model data may only
     * be used on the current client world
     *
     * @author ZacSharp
     * @reason Fix about Forge's ModelData issues. See: <a href="https://github.com/ThinkingStudios/Litematica-Forge/issues/48">ThinkingStudios/Litematica-Forge issue#48</a>
     * @see <a href="https://github.com/ZacSharp/litematica-forge/commit/73c96536d34bbf0612e099211c96ea77bec5e335#diff-99a005b1c70ed23a8a0ff36c1d1bd16d13a194a96e5d1cc0fb8d5eaf77098717">ZacSharp/litematica-forge commit#73c9653</a>
     */
    @Inject(method = "requestModelDataRefresh", at = @At("HEAD"), cancellable = true, remap = false)
    private static void requestModelDataRefresh(BlockEntity blockEntity, CallbackInfo cir) {
        if (blockEntity.getWorld() instanceof WorldSchematic) {
            cir.cancel();
        }
    }
}