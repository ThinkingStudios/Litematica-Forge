package fi.dy.masa.litematica.materials;

import fi.dy.masa.litematica.util.BlockInfoListType;

import java.util.List;

public interface IMaterialList
{
    BlockInfoListType getMaterialListType();

    void setMaterialListEntries(List<MaterialListEntry> list);
}
