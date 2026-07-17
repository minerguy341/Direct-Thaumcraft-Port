package thaumcraft.api;

import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.Part;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.internal.DummyInternalMethodHandler;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.api.internal.WeightedRandomLoot;

/**
 * @author Azanor
 *
 * IMPORTANT: If you are adding your own aspects to items it is a good idea to do it AFTER Thaumcraft adds its aspects, otherwise odd things may happen.
 *
 * Port note: the arcane/crucible/infusion recipe registration methods return with the crafting
 * package port - recipes are codec-based JSON recipe types in 1.21.1.
 */
public class ThaumcraftApi {

    /**
     * Calling methods from this will only work properly once Thaumcraft's implementation has
     * initialized. This is used to access the various methods described in <code>IInternalMethodHandler</code>
     * @see IInternalMethodHandler
     */
    public static IInternalMethodHandler internalMethods = new DummyInternalMethodHandler();

    //RESEARCH/////////////////////////////////////////

    /**
     * Allows you to register the location of a json file in your assets folder that contains your research.
     * For example: <code>"thaumcraft:research/basics"</code>
     * @param loc the resourcelocation of the json file
     */
    public static void registerResearchLocation(ResourceLocation loc) {
        if (!CommonInternals.jsonLocs.containsKey(loc.toString())) {
            CommonInternals.jsonLocs.put(loc.toString(), loc);
        }
    }

    //RECIPES/////////////////////////////////////////

    public static class SmeltBonus {
        /** Either an ItemStack or a TagKey&lt;Item&gt; (the ore-dictionary replacement). */
        public Object in;
        public ItemStack out;
        public float chance;

        public SmeltBonus(Object in, ItemStack out, float chance) {
            this.in = in;
            this.out = out;
            this.chance = chance;
        }
    }

    /**
     * This method is used to determine what bonus items are generated when the infernal furnace smelts items
     * @param in The input of the smelting operation. Can either be an itemstack or an item tag
     * @param out The bonus item that can be produced from the smelting operation.
     * @param chance the base chance of the item being produced as a bonus. Default value is .33f
     */
    public static void addSmeltingBonus(Object in, ItemStack out, float chance) {
        CommonInternals.smeltingBonus.add(new SmeltBonus(in, out, chance));
    }

    public static void addSmeltingBonus(Object in, ItemStack out) {
        addSmeltingBonus(in, out, .33f);
    }

    public static HashMap<ResourceLocation, IThaumcraftRecipe> getCraftingRecipes() {
        return CommonInternals.craftingRecipeCatalog;
    }

    public static HashMap<ResourceLocation, Object> getCraftingRecipesFake() {
        return CommonInternals.craftingRecipeCatalogFake;
    }

    /**
     * This adds recipes to the 'fake' recipe catalog. These recipes won't be craftable, but are useful for display in the thaumonomicon if
     * they are dynamic recipes like infusion enchantment or runic infusion.
     */
    public static void addFakeCraftingRecipe(ResourceLocation registry, Object recipe) {
        getCraftingRecipesFake().put(registry, recipe);
    }

    /**
     * Use this method to add a multiblock blueprint recipe to the thaumcraft recipe catalog. This is used for display purposes in the thaumonomicon
     * @param registry unique identifier for this recipe. I advise making your mod-id part of this.
     * Recipes grouped under the same name will be displayed under one bookmark in thaumonomicon.
     * @param recipe a matrix of placable objects and what they will turn into
     */
    public static void addMultiblockRecipeToCatalog(ResourceLocation registry, BluePrint recipe) {
        getCraftingRecipes().put(registry, recipe);
    }

    public static class BluePrint implements IThaumcraftRecipe {
        Part[][][] parts;
        String research;
        ItemStack displayStack;
        ItemStack[] ingredientList;

        public BluePrint(String research, Part[][][] parts, ItemStack... ingredientList) {
            this.parts = parts;
            this.research = research;
            this.ingredientList = ingredientList;
        }

        public BluePrint(String research, ItemStack display, Part[][][] parts, ItemStack... ingredientList) {
            this.parts = parts;
            this.research = research;
            this.displayStack = display;
            this.ingredientList = ingredientList;
        }

        public Part[][][] getParts() {
            return parts;
        }

        @Override
        public String getResearch() {
            return research;
        }

        /**
         * the items needed to craft this block - used for listing in the thaumonomicon and does not influence the actual recipe
         */
        public ItemStack[] getIngredientList() {
            return ingredientList;
        }

        /**
         * This stack will be displayed instead of multipart object - used for recipe bookmark display in thaumonomicon only.
         */
        public ItemStack getDisplayStack() {
            return displayStack;
        }

        private String group;

        @Override
        public String getGroup() {
            return group;
        }

        public BluePrint setGroup(ResourceLocation loc) {
            group = loc.toString();
            return this;
        }
    }

    //ASPECTS////////////////////////////////////////

    /**
     * Checks to see if the passed item/block already has aspects associated with it.
     */
    public static boolean exists(ItemStack item) {
        return CommonInternals.objectTags.containsKey(CommonInternals.generateUniqueItemstackId(item))
                || CommonInternals.objectTags.containsKey(CommonInternals.generateUniqueItemstackIdStripped(item));
    }

    /**
     * @deprecated Use the methods exposed via the thaumcraft.api.aspects.AspectRegistryEvent event instead.
     */
    @Deprecated
    public static void registerObjectTag(ItemStack item, AspectList aspects) {
        (new AspectEventProxy()).registerObjectTag(item, aspects);
    }

    /**
     * @deprecated Use the methods exposed via the thaumcraft.api.aspects.AspectRegistryEvent events instead.
     */
    @Deprecated
    public static void registerComplexObjectTag(ItemStack item, AspectList aspects) {
        (new AspectEventProxy()).registerComplexObjectTag(item, aspects);
    }

    public static class EntityTagsNBT {
        public EntityTagsNBT(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String name;
        public Object value;
    }

    public static class EntityTags {
        public EntityTags(String entityName, AspectList aspects, EntityTagsNBT... nbts) {
            this.entityName = entityName;
            this.nbts = nbts;
            this.aspects = aspects;
        }

        /** The entity type id, e.g. "minecraft:skeleton" */
        public String entityName;
        public EntityTagsNBT[] nbts;
        public AspectList aspects;
    }

    /**
     * This is used to add aspects to entities which you can then scan using a thaumometer.
     * Also used to calculate vis drops from mobs.
     * @param entityName the entity type id, e.g. "minecraft:skeleton"
     * @param nbt you can specify certain nbt keys and their values
     *            to differentiate between mobs.
     */
    public static void registerEntityTag(String entityName, AspectList aspects, EntityTagsNBT... nbt) {
        CommonInternals.scanEntities.add(new EntityTags(entityName, aspects, nbt));
    }

    //WARP/////////////////////////////////////////

    /**
     * This method is used to determine how much warp is gained if the item is crafted. The warp
     * added is "sticky" warp
     * @param craftresult The item crafted
     * @param amount how much warp is gained
     */
    public static void addWarpToItem(ItemStack craftresult, int amount) {
        CommonInternals.warpMap.put(craftresult.getItem(), amount);
    }

    /**
     * Returns how much warp is gained from the item passed in
     */
    public static int getWarp(ItemStack in) {
        if (in == null || in.isEmpty()) return 0;
        return CommonInternals.warpMap.getOrDefault(in.getItem(), 0);
    }

    // LOOT BAGS

    /**
     * Used to add possible loot to treasure bags. As a reference, the weight of gold coins are 2000
     * and a diamond is 50.
     * The weights are the same for all loot bag types - the only difference is how many items the bag
     * contains.
     * @param bagTypes array of which type of bag to add this loot to. Multiple types can be specified
     * 0 = common, 1 = uncommon, 2 = rare
     */
    public static void addLootBagItem(ItemStack item, int weight, int... bagTypes) {
        if (bagTypes == null || bagTypes.length == 0)
            WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item, weight));
        else {
            for (int rarity : bagTypes) {
                switch (rarity) {
                    case 0 -> WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item, weight));
                    case 1 -> WeightedRandomLoot.lootBagUncommon.add(new WeightedRandomLoot(item, weight));
                    case 2 -> WeightedRandomLoot.lootBagRare.add(new WeightedRandomLoot(item, weight));
                }
            }
        }
    }

    // CROPS

    /**
     * This method is used to register an item that will act as a seed for the specified block.
     * If your seed items use IPlantable it might not be necessary to do this as I
     * attempt to automatically detect such links.
     */
    public static void registerSeed(Block block, ItemStack seed) {
        CommonInternals.seedList.put(block.getDescriptionId(), seed);
    }

    public static ItemStack getSeed(Block block) {
        return CommonInternals.seedList.get(block.getDescriptionId());
    }

}
