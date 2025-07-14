package fi.dy.masa.litematica.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Caches Block/Item Tags as if they are real Vanilla Block/Item tags.
 */
public class CachedTagManager
{
    public static final String GLASS_ITEMS_KEY = "glass_items";
    public static final String GLASS_PANE_ITEMS_KEY = "glass_pane_items";
    public static final String CONCRETE_POWDER_ITEMS_KEY = "concrete_powder_items";
    public static final String CONCRETE_ITEMS_KEY = "concrete_items";
    public static final String GLAZED_TERRACOTTA_ITEMS_KEY = "glazed_terracotta_items";
    public static final String PACKED_BLOCK_ITEMS_KEY = "packed_block_items";
    public static final String UNPACKED_BLOCK_ITEMS_KEY = "unpacked_block_items";

    public static void startCache()
    {
        clearCache();

        CachedItemTags.getInstance().build(GLASS_ITEMS_KEY, buildGlassItemCache());
        CachedItemTags.getInstance().build(GLASS_PANE_ITEMS_KEY, buildGlassPanesItemCache());
        CachedItemTags.getInstance().build(CONCRETE_POWDER_ITEMS_KEY, buildConcretePowderItemCache());
        CachedItemTags.getInstance().build(CONCRETE_ITEMS_KEY, buildConcreteItemCache());
        CachedItemTags.getInstance().build(GLAZED_TERRACOTTA_ITEMS_KEY, buildGlazedTerracottaItemCache());
        CachedItemTags.getInstance().build(PACKED_BLOCK_ITEMS_KEY, buildPackedBlockItemCache());
        CachedItemTags.getInstance().build(UNPACKED_BLOCK_ITEMS_KEY, buildUnpackedBlockItemCache());
    }

    private static List<String> buildGlassItemCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.ITEM.getId(Items.GLASS).toString());
        list.add(Registries.ITEM.getId(Items.BLACK_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.BLUE_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.BROWN_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.CYAN_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.GRAY_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.GREEN_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_BLUE_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_GRAY_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.LIME_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.MAGENTA_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.ORANGE_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.PINK_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.PURPLE_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.RED_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.YELLOW_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.WHITE_STAINED_GLASS).toString());
        list.add(Registries.ITEM.getId(Items.TINTED_GLASS).toString());

        return list;
    }

    private static List<String> buildGlassPanesItemCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.ITEM.getId(Items.GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.BLACK_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.BLUE_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.BROWN_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.CYAN_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.GRAY_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.GREEN_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_BLUE_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_GRAY_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.LIME_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.MAGENTA_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.ORANGE_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.PINK_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.PURPLE_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.RED_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.YELLOW_STAINED_GLASS_PANE).toString());
        list.add(Registries.ITEM.getId(Items.WHITE_STAINED_GLASS_PANE).toString());

        return list;
    }

    private static List<String> buildConcretePowderItemCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.ITEM.getId(Items.BLACK_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.BLUE_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.BROWN_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.CYAN_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.GRAY_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.GREEN_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_BLUE_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_GRAY_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.LIME_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.MAGENTA_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.ORANGE_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.PINK_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.PURPLE_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.RED_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.YELLOW_CONCRETE_POWDER).toString());
        list.add(Registries.ITEM.getId(Items.WHITE_CONCRETE_POWDER).toString());

        return list;
    }

    private static List<String> buildConcreteItemCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.ITEM.getId(Items.BLACK_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.BLUE_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.BROWN_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.CYAN_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.GRAY_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.GREEN_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_BLUE_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_GRAY_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.LIME_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.MAGENTA_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.ORANGE_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.PINK_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.PURPLE_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.RED_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.YELLOW_CONCRETE).toString());
        list.add(Registries.ITEM.getId(Items.WHITE_CONCRETE).toString());

        return list;
    }

    private static List<String> buildGlazedTerracottaItemCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.ITEM.getId(Items.BLACK_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.BLUE_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.BROWN_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.CYAN_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.GRAY_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.GREEN_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_BLUE_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.LIGHT_GRAY_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.LIME_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.MAGENTA_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.ORANGE_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.PINK_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.PURPLE_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.RED_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.YELLOW_GLAZED_TERRACOTTA).toString());
        list.add(Registries.ITEM.getId(Items.WHITE_GLAZED_TERRACOTTA).toString());

        return list;
    }

    private static List<String> buildPackedBlockItemCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.ITEM.getId(Items.BONE_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.CLAY).toString());
        list.add(Registries.ITEM.getId(Items.COAL_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.COPPER_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.DIAMOND_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.EMERALD_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.GOLD_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.HAY_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.HONEY_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.IRON_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.LAPIS_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.MELON).toString());
        list.add(Registries.ITEM.getId(Items.NETHERITE_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.RAW_COPPER_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.RAW_GOLD_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.RAW_IRON_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.REDSTONE_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.RESIN_BLOCK).toString());
        list.add(Registries.ITEM.getId(Items.RESIN_BRICKS).toString());
        list.add(Registries.ITEM.getId(Items.SLIME_BLOCK).toString());

        return list;
    }

    private static List<String> buildUnpackedBlockItemCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.ITEM.getId(Items.BONE).toString());
        list.add(Registries.ITEM.getId(Items.CLAY_BALL).toString());
        list.add(Registries.ITEM.getId(Items.COAL).toString());
        list.add(Registries.ITEM.getId(Items.COPPER_INGOT).toString());
        list.add(Registries.ITEM.getId(Items.DIAMOND).toString());
        list.add(Registries.ITEM.getId(Items.EMERALD).toString());
        list.add(Registries.ITEM.getId(Items.GLOWSTONE_DUST).toString());
        list.add(Registries.ITEM.getId(Items.GOLD_INGOT).toString());
        list.add(Registries.ITEM.getId(Items.GOLD_NUGGET).toString());
        list.add(Registries.ITEM.getId(Items.HONEY_BOTTLE).toString());
        list.add(Registries.ITEM.getId(Items.ICE).toString());
        list.add(Registries.ITEM.getId(Items.IRON_INGOT).toString());
        list.add(Registries.ITEM.getId(Items.IRON_NUGGET).toString());
        list.add(Registries.ITEM.getId(Items.LAPIS_LAZULI).toString());
        list.add(Registries.ITEM.getId(Items.MELON_SLICE).toString());
        list.add(Registries.ITEM.getId(Items.NETHERITE_INGOT).toString());
        list.add(Registries.ITEM.getId(Items.NETHER_WART).toString());
        list.add(Registries.ITEM.getId(Items.PACKED_ICE).toString());
        list.add(Registries.ITEM.getId(Items.REDSTONE).toString());
        list.add(Registries.ITEM.getId(Items.RESIN_BRICK).toString());
        list.add(Registries.ITEM.getId(Items.RESIN_CLUMP).toString());
        list.add(Registries.ITEM.getId(Items.SLIME_BALL).toString());
        list.add(Registries.ITEM.getId(Items.WHEAT).toString());

        return list;
    }

    private static void clearCache()
    {
        CachedBlockTags.getInstance().clear();
        CachedItemTags.getInstance().clear();
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param block (Block Entry)
     * @return ()
     */
    public static boolean matchBlockTag(String key, RegistryEntry<Block> block)
    {
        return CachedBlockTags.getInstance().match(key, block);
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param block (Block)
     * @return ()
     */
    public static boolean matchBlockTag(String key, Block block)
    {
        return CachedBlockTags.getInstance().match(key, block);
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param state (Block State)
     * @return ()
     */
    public static boolean matchBlockTag(String key, BlockState state)
    {
        return CachedBlockTags.getInstance().match(key, state);
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param item (Item Entry)
     * @return ()
     */
    public static boolean matchItemTag(String key, RegistryEntry<Item> item)
    {
        return CachedItemTags.getInstance().match(key, item);
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param item (Item)
     * @return ()
     */
    public static boolean matchItemTag(String key, Item item)
    {
        return CachedItemTags.getInstance().match(key, item);
    }
}
