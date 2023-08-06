package fi.dy.masa.litematica.mixin.compat;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IForgeTileEntity.class, priority = 0, remap = false)
public interface MixinIForgeTileEntity extends ICapabilitySerializable<NbtCompound> {
    @Shadow
    default BlockEntity getTileEntity() {
        return (BlockEntity) this;
    }

    /**
     * @author Kasualix
     * @reason correct impl
     **/
    @Overwrite
    default void requestModelDataUpdate() {
        BlockEntity te = getTileEntity();
        if (te.getWorld() instanceof ClientWorld) {
            ModelDataManager.requestModelDataRefresh(te);
        }
    }
}
