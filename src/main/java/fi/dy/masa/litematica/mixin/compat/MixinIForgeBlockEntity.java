package fi.dy.masa.litematica.mixin.compat;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;

import net.minecraftforge.client.model.data.ModelDataManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = IForgeBlockEntity.class, priority = 0, remap = false)
public interface MixinIForgeBlockEntity extends ICapabilitySerializable<NbtCompound> {
    /**
     * @author Kasualix
     * @reason correct impl
     **/
    @Overwrite
    default void requestModelDataUpdate() {
        BlockEntity be = (BlockEntity) this;
        if (be.getWorld() instanceof ClientWorld) {
            ModelDataManager modelDataManager = be.getWorld().getModelDataManager();
            modelDataManager.requestRefresh(be);
        }
    }
}
