package fi.dy.masa.litematica.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.util.WorldUtils;

@Mixin(value = AbstractSignEditScreen.class, priority = 990)
public class MixinAbstractSignEditScreen
{
    @Shadow @Final private SignBlockEntity blockEntity;

    @Shadow @Final private String[] messages;
    @Shadow @Final private boolean front;

    @Inject(method = "init", at = @At("HEAD"))
    private void insertSignText(CallbackInfo ci)
    {
        if (Configs.Generic.SIGN_TEXT_PASTE.getBooleanValue())
        {
            WorldUtils.insertSignTextFromSchematic(this.blockEntity, this.messages, this.front);
        }
    }
}
