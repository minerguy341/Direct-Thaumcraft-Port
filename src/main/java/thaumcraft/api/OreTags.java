package thaumcraft.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Item tags for Thaumcraft's materials - the modern replacement for the 1.12.2
 * OreDictionaryEntries class. Common ("c") namespace tags are used where conventions
 * exist so other mods' equivalents match automatically.
 */
public final class OreTags {

    public static final TagKey<Item> AMBER = common("gems/amber");
    public static final TagKey<Item> QUICKSILVER = common("gems/quicksilver");
    public static final TagKey<Item> CINNABAR_ORE = common("ores/cinnabar");
    public static final TagKey<Item> AMBER_ORE = common("ores/amber");
    public static final TagKey<Item> QUARTZ_ORE = common("ores/quartz");
    public static final TagKey<Item> THAUMIUM_INGOT = common("ingots/thaumium");
    public static final TagKey<Item> VOID_INGOT = common("ingots/void_metal");
    public static final TagKey<Item> BRASS_INGOT = common("ingots/brass");
    public static final TagKey<Item> THAUMIUM_NUGGET = common("nuggets/thaumium");
    public static final TagKey<Item> VOID_NUGGET = common("nuggets/void_metal");
    public static final TagKey<Item> GREATWOOD_LOG = thaumcraft("logs/greatwood");
    public static final TagKey<Item> SILVERWOOD_LOG = thaumcraft("logs/silverwood");
    public static final TagKey<Item> SALIS_MUNDUS = thaumcraft("salis_mundus");
    public static final TagKey<Item> TAINTED = thaumcraft("tainted");
    public static final TagKey<Item> PRIMAL_CRYSTALS = thaumcraft("crystals/primal");

    private static TagKey<Item> common(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Item> thaumcraft(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("thaumcraft", path));
    }

    private OreTags() {
    }
}
