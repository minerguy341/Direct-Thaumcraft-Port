package thaumcraft.common.init;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final DeferredItem<Item> THAUMONOMICON =
            ITEMS.registerSimpleItem("thaumonomicon", new Item.Properties().stacksTo(1));

    // core materials
    public static final DeferredItem<Item> SALIS_MUNDUS = simple("salis_mundus");
    public static final DeferredItem<Item> AMBER = simple("amber");
    public static final DeferredItem<Item> QUICKSILVER = simple("quicksilver");
    public static final DeferredItem<Item> TALLOW = simple("tallow");
    public static final DeferredItem<Item> FABRIC = simple("fabric");
    public static final DeferredItem<Item> FILTER = simple("filter");
    public static final DeferredItem<Item> ALUMENTUM = simple("alumentum");
    public static final DeferredItem<Item> VOID_SEED = simple("void_seed");
    public static final DeferredItem<Item> CRYSTAL_ESSENCE = simple("crystal_essence");
    public static final DeferredItem<Item> PHIAL_EMPTY = simple("phial_empty");

    // metals
    public static final DeferredItem<Item> INGOT_THAUMIUM = simple("ingot_thaumium");
    public static final DeferredItem<Item> INGOT_VOID = simple("ingot_void");
    public static final DeferredItem<Item> INGOT_BRASS = simple("ingot_brass");
    public static final DeferredItem<Item> NUGGET_THAUMIUM = simple("nugget_thaumium");
    public static final DeferredItem<Item> NUGGET_VOID = simple("nugget_void");
    public static final DeferredItem<Item> NUGGET_QUICKSILVER = simple("nugget_quicksilver");

    // tools of the trade (plain items until their systems land)
    public static final DeferredItem<Item> THAUMOMETER =
            ITEMS.registerSimpleItem("thaumometer", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> SCRIBING_TOOLS =
            ITEMS.registerSimpleItem("scribing_tools", new Item.Properties().stacksTo(1).durability(64));

    private static DeferredItem<Item> simple(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    private ModItems() {
    }
}
