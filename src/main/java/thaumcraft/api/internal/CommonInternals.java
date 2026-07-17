package thaumcraft.api.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApi.SmeltBonus;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;

/**
 * @author Azanor
 *
 * Internal variables and methods used by Thaumcraft and the api that should normally not be accessed directly by addon mods.
 */
public class CommonInternals {

    public static HashMap<String, ResourceLocation> jsonLocs = new HashMap<>();
    public static ArrayList<ThaumcraftApi.EntityTags> scanEntities = new ArrayList<>();
    public static HashMap<ResourceLocation, IThaumcraftRecipe> craftingRecipeCatalog = new HashMap<>();
    public static HashMap<ResourceLocation, Object> craftingRecipeCatalogFake = new HashMap<>();
    public static ArrayList<SmeltBonus> smeltingBonus = new ArrayList<>();
    public static ConcurrentHashMap<Integer, AspectList> objectTags = new ConcurrentHashMap<>();
    public static HashMap<Object, Integer> warpMap = new HashMap<>();
    public static HashMap<String, ItemStack> seedList = new HashMap<>();

    public static IThaumcraftRecipe getCatalogRecipe(ResourceLocation key) {
        return craftingRecipeCatalog.get(key);
    }

    public static Object getCatalogRecipeFake(ResourceLocation key) {
        return craftingRecipeCatalogFake.get(key);
    }

    /**
     * Obviously the int generated is not truly unique, but it is unique enough for this purpose.
     * Count-independent; includes data components (the 1.21 equivalent of nbt).
     */
    public static int generateUniqueItemstackId(ItemStack stack) {
        return ItemStack.hashItemAndComponents(stack);
    }

    /**
     * Obviously the int generated is not truly unique, but it is unique enough for this purpose.
     * Ignores all data components on the stack.
     */
    public static int generateUniqueItemstackIdStripped(ItemStack stack) {
        return Objects.hash(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }
}
