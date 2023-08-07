package fi.dy.masa.litematica.mixin.compat;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.client.model.data.ModelDataManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IForgeBlockEntity.class, priority = 0, remap = false)
public interface MixinIForgeBlockEntity extends ICapabilitySerializable<NbtCompound> {
    @Shadow
    private BlockEntity self() { return (BlockEntity) this; }

    /**
     * @author Kasualix
     * @reason correct impl
     **/
    @Overwrite
    default void requestModelDataUpdate() {
        BlockEntity te = self();
        if (te.getWorld() instanceof ClientWorld) {
            ModelDataManager modelDataManager = te.getWorld().getModelDataManager();
            modelDataManager.requestRefresh(te);
        }
    }
}
