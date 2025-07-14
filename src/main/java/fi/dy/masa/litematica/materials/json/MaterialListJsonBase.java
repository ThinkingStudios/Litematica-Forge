package fi.dy.masa.litematica.materials.json;

import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;

import fi.dy.masa.malilib.mixin.recipe.IMixinIngredient;
import fi.dy.masa.malilib.util.game.RecipeBookUtils;

public class MaterialListJsonBase
{
    private final RegistryEntry<Item> input;
    private final int count;
    private @Nullable MaterialListJsonEntry materialsCrafting;
    private @Nullable MaterialListJsonEntry materialsStonecutter;
    private @Nullable MaterialListJsonEntry materialsFurnace;
    private @Nullable MaterialListJsonEntry materialsRemaining;

    public MaterialListJsonBase(final RegistryEntry<Item> input, final int count, @Nullable RegistryEntry<Item> prevItem, boolean craftingOnly)
    {
        this.input = input;
        this.count = count;
        boolean matched = false;

        MaterialListJsonEntry entryStonecutter = MaterialListJsonEntry.build(input, count, List.of(RecipeBookUtils.Type.STONECUTTER), prevItem, craftingOnly);
        if (entryStonecutter != null && entryStonecutter.hasOutput())
        {
            if (this.checkIfLoop(entryStonecutter, input, prevItem))
            {
                this.materialsRemaining = MaterialListJsonEntry.build(input, count, List.of(), prevItem, craftingOnly);
                return;
            }

            this.materialsStonecutter = entryStonecutter;
            matched = true;
        }

        MaterialListJsonEntry entryCrafting = MaterialListJsonEntry.build(input, count, List.of(RecipeBookUtils.Type.SHAPED, RecipeBookUtils.Type.SHAPELESS), prevItem, craftingOnly);
        if (entryCrafting != null && entryCrafting.hasOutput())
        {
            if (this.checkIfLoop(entryCrafting, input, prevItem))
            {
                this.materialsRemaining = MaterialListJsonEntry.build(input, count, List.of(), prevItem, craftingOnly);
                return;
            }

            this.materialsCrafting = entryCrafting;
            matched = true;
        }

        if (matched && this.materialsCrafting != null && this.materialsStonecutter != null)
        {
            if (craftingOnly)
            {
                this.materialsStonecutter = null;
            }
            else
            {
                this.materialsCrafting = null;
            }
        }

        if (!matched)
        {
            MaterialListJsonEntry entryFurnace = MaterialListJsonEntry.build(input, count, List.of(RecipeBookUtils.Type.FURNACE), prevItem, craftingOnly);
            if (entryFurnace != null && entryFurnace.hasOutput())
            {
                if (this.checkIfLoop(entryFurnace, input, prevItem))
                {
                    this.materialsRemaining = MaterialListJsonEntry.build(input, count, List.of(), prevItem, craftingOnly);
                    return;
                }

                this.materialsFurnace = entryFurnace;
                matched = true;
            }
        }

        // No matches, so add it to the remaining.
        if (!matched)
        {
            MaterialListJsonEntry entryRemaining = MaterialListJsonEntry.build(input, count, List.of(), prevItem, craftingOnly);

            if (entryRemaining != null)
            {
                this.materialsRemaining = entryRemaining;
            }
        }
    }

    @Nullable
    public MaterialListJsonEntry getMaterialsCrafting()
    {
        return this.materialsCrafting;
    }

    @Nullable
    public MaterialListJsonEntry getMaterialsStonecutter()
    {
        return this.materialsStonecutter;
    }

    @Nullable
    public MaterialListJsonEntry getMaterialsFurnace()
    {
        return this.materialsFurnace;
    }

    @Nullable
    public MaterialListJsonEntry getMaterialsRemaining()
    {
        return this.materialsRemaining;
    }

    public RegistryEntry<Item> getInput()
    {
        return this.input;
    }

    public int getCount()
    {
        return this.count;
    }

    /**
     * Stop a looping state; ie Redstone Dust -> Redstone Block -> Redstone Dust.
     * Return True if a looped result.
     * @param entry ()
     * @param prevItem ()
     * @return (True|False)
     */
    private boolean checkIfLoop(MaterialListJsonEntry entry, RegistryEntry<Item> inputItem, RegistryEntry<Item> prevItem)
    {
        HashMap<NetworkRecipeId, List<Ingredient>> recipeReq = entry.getRecipeRequirements();
        HashMap<NetworkRecipeId, RecipeBookUtils.Type> recipeTypes = entry.getRecipeTypes();

//        Litematica.LOGGER.warn("checkIfLoop(): input: [{}], prev: [{}]", inputItem.getIdAsString(), prevItem != null ? prevItem.getIdAsString() : "<>");

        if (!recipeReq.isEmpty() && !recipeTypes.isEmpty())
        {
            NetworkRecipeId firstId = recipeTypes.keySet().stream().toList().getFirst();
            List<Ingredient> ingredients = recipeReq.get(firstId);
            Ingredient ingredient = ingredients.getFirst();
            List<RegistryEntry<Item>> ingItems = ((IMixinIngredient) (Object) ingredient).malilib_getEntries().stream().toList();

            if (ingItems.contains(inputItem) || ingItems.contains(prevItem))
            {
                return true;
            }
        }

        List<MaterialListJsonBase> list = entry.getRequirements();

        for (MaterialListJsonBase eachItem : list)
        {
            if (eachItem.getInput() == inputItem)
            {
                return true;
            }
            else if (eachItem.getInput() == prevItem)
            {
                return true;
            }
        }

        return (inputItem == prevItem);
    }

    public JsonElement toJson(RegistryOps<?> ops)
    {
        JsonObject obj = new JsonObject();

        obj.add("Item", new JsonPrimitive(this.getInput().getIdAsString()));
        obj.add("Count", new JsonPrimitive(this.getCount()));

        if (this.materialsCrafting != null)
        {
            obj.add("CraftingMaterials", this.materialsCrafting.toJson(ops));
        }
        if (this.materialsStonecutter != null)
        {
            obj.add("StonecutterMaterials", this.materialsStonecutter.toJson(ops));
        }
        if (this.materialsFurnace != null)
        {
            obj.add("FurnaceMaterials", this.materialsFurnace.toJson(ops));
        }
        if (this.materialsRemaining != null)
        {
            obj.add("RemainingMaterials", this.materialsRemaining.toJson(ops));
        }

        return obj;
    }
}
