package fi.dy.masa.litematica.scheduler.tasks;

import fi.dy.masa.litematica.materials.IMaterialList;
import fi.dy.masa.litematica.selection.AreaSelection;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class TaskCountBlocksArea extends TaskCountBlocksBase
{
    public TaskCountBlocksArea(AreaSelection selection, IMaterialList materialList)
    {
        super(materialList, "litematica.gui.label.task_name.area_analyzer");

        this.addPerChunkBoxes(selection.getAllSubRegionBoxes());
    }

    @Override
    protected void countAtPosition(BlockPos pos)
    {
        BlockState stateClient = this.clientWorld.getBlockState(pos);
        this.countsTotal.addTo(stateClient, 1);
    }
}
