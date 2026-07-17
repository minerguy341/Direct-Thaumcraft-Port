package thaumcraft.common.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.init.ModDataMaps;

/**
 * Derives aspects for items with no explicit assignment from their crafting recipes:
 * sum of ingredient aspects, dampened by 25% and divided by the output count, capped at
 * 7 aspect types (the TC4 propagation rules). Rebuilt when recipes (re)load; multiple
 * passes let crafted-from-crafted chains resolve.
 */
public final class AspectInference {

    private static final int PASSES = 4;
    private static final Map<Item, AspectList> inferred = new ConcurrentHashMap<>();

    public static AspectList getInferred(Item item) {
        return inferred.get(item);
    }

    public static void rebuild(MinecraftServer server) {
        inferred.clear();
        RegistryAccess registries = server.registryAccess();

        for (int pass = 0; pass < PASSES; pass++) {
            boolean changed = false;
            for (RecipeHolder<CraftingRecipe> holder : server.getRecipeManager()
                    .getAllRecipesFor(RecipeType.CRAFTING)) {
                try {
                    ItemStack result = holder.value().getResultItem(registries);
                    if (result == null || result.isEmpty()) continue;
                    Item out = result.getItem();
                    if (inferred.containsKey(out) || hasDirectAspects(result)) continue;

                    AspectList total = new AspectList();
                    for (Ingredient ingredient : holder.value().getIngredients()) {
                        AspectList part = resolveIngredient(ingredient);
                        if (part != null) total.add(part);
                    }
                    if (total.size() == 0) continue;

                    AspectList scaled = new AspectList();
                    for (Aspect aspect : total.getAspects()) {
                        int amt = (int) Math.floor(total.getAmount(aspect) * 0.75 / result.getCount());
                        if (amt > 0) scaled.add(aspect, amt);
                    }
                    if (scaled.size() == 0) continue;

                    inferred.put(out, AspectHelper.cullTags(scaled));
                    changed = true;
                } catch (Exception ignored) {}
            }
            if (!changed) break;
        }
    }

    private static boolean hasDirectAspects(ItemStack stack) {
        if (CommonInternals.objectTags.containsKey(CommonInternals.generateUniqueItemstackId(stack)))
            return true;
        return stack.getItemHolder().getData(ModDataMaps.ITEM_ASPECTS) != null;
    }

    private static AspectList resolveIngredient(Ingredient ingredient) {
        for (ItemStack stack : ingredient.getItems()) {
            if (stack.isEmpty()) continue;
            AspectList override = CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(stack));
            if (override != null) return override;
            AspectList mapped = stack.getItemHolder().getData(ModDataMaps.ITEM_ASPECTS);
            if (mapped != null) return mapped;
            AspectList inf = inferred.get(stack.getItem());
            if (inf != null) return inf;
        }
        return null;
    }

    /** Direct pass-through of the inference cache in a fresh HashMap, for debugging. */
    public static Map<Item, AspectList> snapshot() {
        return new HashMap<>(inferred);
    }

    private AspectInference() {
    }
}
