package fi.dy.masa.litematica.item;

import fi.dy.masa.litematica.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LitematicaItem {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Reference.MOD_ID);

    public static final DeferredItem<Item> TOOL_ITEM = ITEMS.registerSimpleItem("matica_tool", new Item.Settings().setNoRepair().maxCount(1));

    public static void registerItem() {
        NeoForge.EVENT_BUS.addListener(BuildCreativeModeTabContentsEvent.class, event -> {
            if (event.getTabKey() == ItemGroups.TOOLS) {
                event.add(TOOL_ITEM);
            }
        });
    }
}
