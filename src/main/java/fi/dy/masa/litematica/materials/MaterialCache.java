package fi.dy.masa.litematica.materials;

import java.util.IdentityHashMap;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import fi.dy.masa.litematica.mixin.block.IMixinAbstractBlock;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;

public class MaterialCache
{
    private static final MaterialCache INSTANCE = new MaterialCache();

    protected final IdentityHashMap<BlockState, ItemStack> buildItemsForStates = new IdentityHashMap<>();
    protected final IdentityHashMap<BlockState, ItemStack> displayItemsForStates = new IdentityHashMap<>();
    protected final WorldSchematic tempWorld;
    protected final BlockPos checkPos;

    private MaterialCache()
    {
        this.tempWorld = SchematicWorldHandler.createSchematicWorld(null);
        this.checkPos = new BlockPos(8, 0, 8);

        WorldUtils.loadChunksSchematicWorld(this.tempWorld, this.checkPos, new Vec3i(1, 1, 1));
    }

    public static MaterialCache getInstance()
    {
        return INSTANCE;
    }

    public void clearCache()
    {
        this.buildItemsForStates.clear();
        this.displayItemsForStates.clear();
    }

    public ItemStack getRequiredBuildItemForState(BlockState state)
    {
        return this.getRequiredBuildItemForState(state, this.tempWorld, this.checkPos);
    }

    public ItemStack getRequiredBuildItemForState(BlockState state, World world, BlockPos pos)
    {
        ItemStack stack = this.buildItemsForStates.get(state);

        if (stack == null)
        {
            stack = this.getItemForStateFromWorld(state, world, pos, true);
        }

        return stack;
    }

    public ItemStack getItemForDisplayNameForState(BlockState state)
    {
        ItemStack stack = this.displayItemsForStates.get(state);

        if (stack == null)
        {
            stack = this.getItemForStateFromWorld(state, this.tempWorld, this.checkPos, false);
        }

        return stack;
    }

    protected ItemStack getItemForStateFromWorld(BlockState state, World world, BlockPos pos, boolean isBuildItem)
    {
        ItemStack stack = isBuildItem ? this.getStateToItemOverride(state) : null;

        if (stack == null)
        {
            world.setBlockState(pos, state, 0x14);
            stack = ((IMixinAbstractBlock) state.getBlock()).litematica_getPickStack(world, pos, state, false);
        }

        if (stack == null || stack.isEmpty())
        {
            stack = ItemStack.EMPTY;
        }
        else
        {
            this.overrideStackSize(state, stack);
        }

        if (isBuildItem)
        {
            this.buildItemsForStates.put(state, stack);
        }
        else
        {
            this.displayItemsForStates.put(state, stack);
        }

        return stack;
    }

    public boolean requiresMultipleItems(BlockState state)
    {
        Block block = state.getBlock();
        if (block instanceof FlowerPotBlock && block != Blocks.FLOWER_POT)
        {
            return true;
        }
        // Block Entity Stuff
//        else if (block instanceof LecternBlock && state.get(LecternBlock.HAS_BOOK))
//        {
//            return true;
//        }
//        else if (block instanceof ChiseledBookshelfBlock)
//        {
//            for (BooleanProperty prop : ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES)
//            {
//                if (state.get(prop))
//                {
//                    return true;
//                }
//            }
//
//            return false;
//        }
        else return block instanceof AbstractCauldronBlock && block != Blocks.CAULDRON;
    }

    public ImmutableList<ItemStack> getItems(BlockState state)
    {
        return this.getItems(state, this.tempWorld, this.checkPos);
    }

    public ImmutableList<ItemStack> getItems(BlockState state, World world, BlockPos pos)
    {
        Block block = state.getBlock();

        if (block instanceof FlowerPotBlock && block != Blocks.FLOWER_POT)
        {
            return ImmutableList.of(new ItemStack(Blocks.FLOWER_POT), ((IMixinAbstractBlock) block).litematica_getPickStack(world, pos, state, false));
        }
//        else if (block instanceof LecternBlock && state.get(LecternBlock.HAS_BOOK))
//        {
//            return ImmutableList.of(new ItemStack(Blocks.LECTERN), ((IMixinAbstractBlock) block).litematica_getPickStack(world, pos, state, false));
//        }
//        else if (block instanceof ChiseledBookshelfBlock)
//        {
//            // Block Entity Stuff
//        }
        else if (block instanceof AbstractCauldronBlock && block != Blocks.CAULDRON)
        {
            if (block instanceof LavaCauldronBlock)
            {
                return ImmutableList.of(new ItemStack(Blocks.CAULDRON), new ItemStack(Items.LAVA_BUCKET));
            }
            else if (block == Blocks.POWDER_SNOW_CAULDRON)
            {
                return ImmutableList.of(new ItemStack(Blocks.CAULDRON), new ItemStack(Items.POWDER_SNOW_BUCKET));
            }
            else if (block == Blocks.WATER_CAULDRON)
            {
                final int level = state.get(LeveledCauldronBlock.LEVEL);

                return switch (level)
                {
                    case 1 -> ImmutableList.of(new ItemStack(Blocks.CAULDRON), PotionContentsComponent.createStack(Items.POTION, Potions.WATER));
                    case 2 -> ImmutableList.of(new ItemStack(Blocks.CAULDRON), PotionContentsComponent.createStack(Items.POTION, Potions.WATER), PotionContentsComponent.createStack(Items.POTION, Potions.WATER));
                    case 3 -> ImmutableList.of(new ItemStack(Blocks.CAULDRON), new ItemStack(Items.WATER_BUCKET));
                    default -> ImmutableList.of(new ItemStack(Blocks.CAULDRON));
                };
            }
        }

        return ImmutableList.of(this.getRequiredBuildItemForState(state, world, pos));
    }

    @Nullable
    protected ItemStack getStateToItemOverride(BlockState state)
    {
        Block block = state.getBlock();

        if (block == Blocks.PISTON_HEAD ||
            block == Blocks.MOVING_PISTON ||
            block == Blocks.NETHER_PORTAL ||
            block == Blocks.END_PORTAL ||
            block == Blocks.END_GATEWAY)
        {
            return ItemStack.EMPTY;
        }
        else if (block == Blocks.FARMLAND)
        {
            return new ItemStack(Blocks.DIRT);
        }
        else if (block == Blocks.BROWN_MUSHROOM_BLOCK)
        {
            return new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK);
        }
        else if (block == Blocks.RED_MUSHROOM_BLOCK)
        {
            return new ItemStack(Blocks.RED_MUSHROOM_BLOCK);
        }
        else if (block == Blocks.LAVA)
        {
            if (state.get(FluidBlock.LEVEL) == 0)
            {
                return new ItemStack(Items.LAVA_BUCKET);
            }
            else
            {
                return ItemStack.EMPTY;
            }
        }
        else if (block == Blocks.WATER)
        {
            if (state.get(FluidBlock.LEVEL) == 0)
            {
                return new ItemStack(Items.WATER_BUCKET);
            }
            else
            {
                return ItemStack.EMPTY;
            }
        }
        else if (block instanceof DoorBlock && state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
        {
            return ItemStack.EMPTY;
        }
        else if (block instanceof BedBlock && state.get(BedBlock.PART) == BedPart.HEAD)
        {
            return ItemStack.EMPTY;
        }
        else if (block instanceof TallPlantBlock && state.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER)
        {
            return ItemStack.EMPTY;
        }

        return null;
    }

    protected void overrideStackSize(BlockState state, ItemStack stack)
    {
        Block block = state.getBlock();

        if (block instanceof SlabBlock && state.get(SlabBlock.TYPE) == SlabType.DOUBLE)
        {
            stack.setCount(2);
        }
        else if (block == Blocks.SNOW)
        {
            stack.setCount(state.get(SnowBlock.LAYERS));
        }
        else if (block instanceof TurtleEggBlock)
        {
            stack.setCount(state.get(TurtleEggBlock.EGGS));
        }
        else if (block instanceof SeaPickleBlock)
        {
            stack.setCount(state.get(SeaPickleBlock.PICKLES));
        }
        else if (block instanceof CandleBlock)
        {
            stack.setCount(state.get(CandleBlock.CANDLES));
        }
        else if (block instanceof MultifaceGrowthBlock)
        {
            stack.setCount(MultifaceGrowthBlock.collectDirections(state).size());
        }
    }
}
