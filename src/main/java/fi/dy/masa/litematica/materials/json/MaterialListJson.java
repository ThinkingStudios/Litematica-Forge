package fi.dy.masa.litematica.materials.json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;

import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListEntry;

public class MaterialListJson
{
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final List<MaterialListJsonBase> data;

    public MaterialListJson()
    {
        this.data = new ArrayList<>();
    }

    public List<MaterialListJsonBase> getMaterials()
    {
        return this.data;
    }

    public boolean readMaterialListAll(MaterialListBase materialList, MaterialListJsonCache cache, boolean craftingOnly)
    {
        ImmutableList<MaterialListEntry> materials = materialList.getMaterialsAll();

        if (materials.isEmpty())
        {
            return false;
        }

        this.data.clear();

        materials.forEach(
                (entry) ->
                {
                    RegistryEntry<Item> resultItem = entry.getStack().getRegistryEntry();
                    final int total = (entry.getStack().getCount() * entry.getCountTotal());
                    MaterialListJsonBase base = new MaterialListJsonBase(resultItem, total, null, craftingOnly);

                    this.data.add(base);
                    cache.buildStepsBase(base, new ArrayList<>(), new MaterialListJsonCache.Result(resultItem, total));
                }
        );

        cache.simplifyFlatEntrySteps();
        cache.repackCombinedEntries();

        return true;
    }

    public boolean readMaterialListMissingOnly(MaterialListBase materialList, MaterialListJsonCache cache, boolean craftingOnly)
    {
        List<MaterialListEntry> materials = materialList.getMaterialsMissingOnly(false);

        if (materials.isEmpty())
        {
            return false;
        }

        this.data.clear();

        materials.forEach(
                (entry) ->
                {
                    RegistryEntry<Item> resultItem = entry.getStack().getRegistryEntry();
                    final int total = (entry.getStack().getCount() * entry.getCountTotal());
                    MaterialListJsonBase base = new MaterialListJsonBase(resultItem, total, null, craftingOnly);

                    this.data.add(base);
                    cache.buildStepsBase(base, new ArrayList<>(), new MaterialListJsonCache.Result(resultItem, total));
                }
        );

        cache.simplifyFlatEntrySteps();
        cache.repackCombinedEntries();

        return true;
    }

    public boolean writeRecipeDetailJson(Path file, MinecraftClient mc)
    {
        if (this.data.isEmpty() || mc.world == null)
        {
            return false;
        }

        if (Files.exists(file))
        {
            try
            {
                Files.delete(file);
            }
            catch (IOException err)
            {
                Litematica.LOGGER.error("MaterialListJson#toJson(): Exception deleting file '{}'; {}", file.toAbsolutePath().toString(), err.getLocalizedMessage());
                return false;
            }
        }

        try
        {
            Files.writeString(file, GSON.toJson(this.toJson(mc.world.getRegistryManager())));
            Litematica.LOGGER.info("MaterialListJson#toJson(): Exported Materials file '{}' successfully.", file.toAbsolutePath().toString());
            return true;
        }
        catch (IOException err)
        {
            Litematica.LOGGER.error("MaterialListJson#toJson(): Exception writing file '{}'; {}", file.toAbsolutePath().toString(), err.getLocalizedMessage());
            return false;
        }
    }

    public boolean writeCacheFlatJson(MaterialListJsonCache cache, Path file, MinecraftClient mc)
    {
        if (cache.isEmptyFlat() || mc.world == null)
        {
            return false;
        }

        if (Files.exists(file))
        {
            try
            {
                Files.delete(file);
            }
            catch (IOException err)
            {
                Litematica.LOGGER.error("MaterialListJson#writeCacheFlatJson(): Exception deleting file '{}'; {}", file.toAbsolutePath().toString(), err.getLocalizedMessage());
                return false;
            }
        }

        try
        {
            Files.writeString(file, GSON.toJson(cache.toFlatJson(mc.world.getRegistryManager().getOps(JsonOps.INSTANCE))));
            Litematica.LOGGER.info("MaterialListJson#writeCacheFlatJson(): Exported Materials Cache file '{}' successfully.", file.toAbsolutePath().toString());
            return true;
        }
        catch (IOException err)
        {
            Litematica.LOGGER.error("MaterialListJson#writeCacheFlatJson(): Exception writing file '{}'; {}", file.toAbsolutePath().toString(), err.getLocalizedMessage());
            return false;
        }
    }

    public boolean writeCacheCombinedJson(MaterialListJsonCache cache, Path file, MinecraftClient mc)
    {
        if (cache.isEmptyCombined() || mc.world == null)
        {
            return false;
        }

        if (Files.exists(file))
        {
            try
            {
                Files.delete(file);
            }
            catch (IOException err)
            {
                Litematica.LOGGER.error("MaterialListJson#writeCacheCombinedJson(): Exception deleting file '{}'; {}", file.toAbsolutePath().toString(), err.getLocalizedMessage());
                return false;
            }
        }

        try
        {
            Files.writeString(file, GSON.toJson(cache.toCombinedJson(mc.world.getRegistryManager().getOps(JsonOps.INSTANCE))));
            Litematica.LOGGER.info("MaterialListJson#writeCacheCombinedJson(): Exported Materials Cache file '{}' successfully.", file.toAbsolutePath().toString());
            return true;
        }
        catch (IOException err)
        {
            Litematica.LOGGER.error("MaterialListJson#writeCacheCombinedJson(): Exception writing file '{}'; {}", file.toAbsolutePath().toString(), err.getLocalizedMessage());
            return false;
        }
    }

    public JsonElement toJson(DynamicRegistryManager registry)
    {
        RegistryOps<?> ops = registry.getOps(JsonOps.INSTANCE);
        JsonArray arr = new JsonArray();

        this.data.forEach(
                (entry) ->
                        arr.add(entry.toJson(ops))
        );

        return arr;
    }

    public void clear()
    {
        this.data.clear();
    }
}
