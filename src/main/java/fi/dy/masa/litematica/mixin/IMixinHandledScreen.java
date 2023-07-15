package fi.dy.masa.litematica.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface IMixinHandledScreen
{
    @Accessor("x")
    int litematica_getX();

    @Accessor("y")
    int litematica_getY();
}
