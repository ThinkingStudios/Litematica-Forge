package fi.dy.masa.litematica.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.storage.ReadView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IMixinEntity
{
    @Accessor("world")
    void litematica_setWorld(World world);

    @Invoker("readCustomData")
    void litematica_readCustomData(ReadView view);

    @Accessor("touchingWater")
    boolean litematica_isTouchingWater();
}
