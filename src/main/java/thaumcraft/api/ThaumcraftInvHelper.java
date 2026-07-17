package thaumcraft.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class ThaumcraftInvHelper {

    public static class InvFilter {
        public boolean igDmg;
        public boolean igNBT;
        public boolean useOre;
        public boolean useMod;
        public boolean relaxedNBT = false;

        public InvFilter(boolean ignoreDamage, boolean ignoreNBT, boolean useOre, boolean useMod) {
            this.igDmg = ignoreDamage;
            this.igNBT = ignoreNBT;
            this.useOre = useOre;
            this.useMod = useMod;
        }

        public InvFilter setRelaxedNBT() {
            relaxedNBT = true;
            return this;
        }

        public static final InvFilter STRICT = new InvFilter(false, false, false, false);
        public static final InvFilter BASEORE = new InvFilter(false, false, true, false);
    }

    public static IItemHandler getItemHandlerAt(Level world, BlockPos pos, Direction side) {
        IItemHandler handler = world.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
        if (handler != null) {
            return handler;
        }
        if (world.getBlockEntity(pos) instanceof Container container) {
            return wrapInventory(container, side);
        }
        return null;
    }

    public static IItemHandler wrapInventory(Container inventory, Direction side) {
        return inventory instanceof WorldlyContainer worldly
                ? new SidedInvWrapper(worldly, side) : new InvWrapper(inventory);
    }

    /**
     * Matches stacks the way TC recipes and golem filters expect: same item, and if `in` carries
     * data components they must all be present and equal on the checked stack (extra components on
     * the checked stack are ignored). `in` may also be a TagKey&lt;Item&gt; (the ore-dictionary
     * replacement) or Object[] as a wildcard.
     */
    @SuppressWarnings("unchecked")
    public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in) {
        if (stack0 == null && in != null) return false;
        if (stack0 != null && in == null) return false;
        if (stack0 == null) return true;

        if (in instanceof Object[]) return true;

        if (in instanceof TagKey<?> tag) {
            return stack0.is((TagKey<Item>) tag);
        }

        if (in instanceof ItemStack stack1) {
            return stack0.is(stack1.getItem()) && componentsMatchForCrafting(stack0, stack1);
        }

        return false;
    }

    /**
     * True if every data component set on the recipe stack is present with an equal value on the
     * slot stack. Components only present on the slot stack are ignored.
     */
    public static boolean componentsMatchForCrafting(ItemStack slotItem, ItemStack recipeItem) {
        if (recipeItem == null || slotItem == null) return false;
        return recipeItem.getComponentsPatch().entrySet().stream().allMatch(entry ->
                java.util.Objects.equals(
                        java.util.Optional.ofNullable(slotItem.getComponents().get(entry.getKey())),
                        entry.getValue()));
    }

    /**
     * Only checks if all the tags in prime are present and equal in other. Any extra tags in other
     * are ignored. Retained for block-entity NBT comparisons.
     */
    public static boolean compareTagsRelaxed(CompoundTag prime, CompoundTag other) {
        for (String key : prime.getAllKeys()) {
            if (!other.contains(key) || !prime.get(key).equals(other.get(key))) {
                return false;
            }
        }
        return true;
    }

    public static boolean areItemsEqual(ItemStack s1, ItemStack s2) {
        return s1.is(s2.getItem());
    }

    public static ItemStack insertStackAt(Level world, BlockPos pos, Direction side, ItemStack stack, boolean simulate) {
        IItemHandler inventory = getItemHandlerAt(world, pos, side);
        if (inventory != null) {
            return ItemHandlerHelper.insertItemStacked(inventory, stack, simulate);
        }
        return stack;
    }

    public static ItemStack hasRoomFor(Level world, BlockPos pos, Direction side, ItemStack stack) {
        ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
        if (testStack.isEmpty()) {
            return stack.copy();
        }
        testStack.setCount(stack.getCount() - testStack.getCount());
        return testStack;
    }

    public static boolean hasRoomForSome(Level world, BlockPos pos, Direction side, ItemStack stack) {
        ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
        return stack.getCount() == 0 || testStack.getCount() != stack.getCount();
    }

    public static boolean hasRoomForAll(Level world, BlockPos pos, Direction side, ItemStack stack) {
        return insertStackAt(world, pos, side, stack.copy(), true).isEmpty();
    }

    public static int countTotalItemsIn(IItemHandler inventory, ItemStack stack, InvFilter filter) {
        int count = 0;
        if (inventory != null) {
            for (int a = 0; a < inventory.getSlots(); a++) {
                if (matchesFilter(stack, inventory.getStackInSlot(a), filter)) {
                    count += inventory.getStackInSlot(a).getCount();
                }
            }
        }
        return count;
    }

    public static int countTotalItemsIn(Level world, BlockPos pos, Direction side, ItemStack stack, InvFilter filter) {
        return countTotalItemsIn(getItemHandlerAt(world, pos, side), stack, filter);
    }

    /**
     * Stack comparison honoring the InvFilter flags. Tag-based (useOre) matching is a TODO until
     * golem filters are implemented with item tags.
     */
    public static boolean matchesFilter(ItemStack a, ItemStack b, InvFilter filter) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return false;
        if (filter.useMod) {
            return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(a.getItem()).getNamespace()
                    .equals(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(b.getItem()).getNamespace());
        }
        if (!a.is(b.getItem())) return false;
        if (!filter.igDmg && a.isDamageableItem() && a.getDamageValue() != b.getDamageValue()) return false;
        if (!filter.igNBT && !filter.relaxedNBT && !ItemStack.isSameItemSameComponents(a, b)) return false;
        if (!filter.igNBT && filter.relaxedNBT && !componentsMatchForCrafting(b, a)) return false;
        return true;
    }

}
