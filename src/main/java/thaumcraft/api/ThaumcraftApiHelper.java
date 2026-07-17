package thaumcraft.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Trimmed port of the 1.12.2 helper: only the members used by the API itself have been carried
 * over so far; the rest return with the systems that need them.
 */
public class ThaumcraftApiHelper {

    /**
     * The champion-mob modifier attribute. Registered and injected by the Thaumcraft
     * implementation; null until then.
     */
    public static Holder<Attribute> CHAMPION_MOD = null;

    /**
     * Builds an Ingredient from the loose-typed recipe inputs TC recipes accept:
     * an Ingredient (passed through), ItemStack, Item/Block (ItemLike), or TagKey&lt;Item&gt;
     * (the ore-dictionary-name replacement). Returns null for anything else.
     */
    @SuppressWarnings("unchecked")
    public static Ingredient getIngredient(Object obj) {
        if (obj instanceof Ingredient ing) return ing;
        if (obj instanceof ItemStack stack) return Ingredient.of(stack);
        if (obj instanceof ItemLike itemLike) return Ingredient.of(itemLike);
        if (obj instanceof TagKey<?> tag) return Ingredient.of((TagKey<Item>) tag);
        return null;
    }

    /**
     * All items in the given tag as stacks - the ore-dictionary-with-wildcards replacement.
     */
    public static List<ItemStack> getItemsFromTag(TagKey<Item> tag) {
        List<ItemStack> out = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            out.add(new ItemStack(holder.value()));
        }
        return out;
    }

    /**
     * Returns the value of the named tag in a comparison-friendly form (Number for numeric tags,
     * String for string tags, the raw Tag otherwise). Replaces the 1.12.2 id-switch helper.
     */
    public static Object getNBTDataFromId(CompoundTag nbt, String name) {
        Tag t = nbt.get(name);
        if (t == null) return null;
        if (t instanceof NumericTag num) return num.getAsNumber();
        if (t instanceof StringTag str) return str.getAsString();
        return t;
    }

    /**
     * Compares an EntityTagsNBT-style expected value against the tag on the compound. Numbers are
     * compared by value so a byte 1 matches an int 1.
     */
    public static boolean nbtMatches(CompoundTag nbt, String name, Object expected) {
        Object actual = getNBTDataFromId(nbt, name);
        if (actual == null) return expected == null;
        if (actual instanceof Number an && expected instanceof Number en) {
            return an.doubleValue() == en.doubleValue();
        }
        return actual.equals(expected) || actual.toString().equals(expected.toString());
    }

}
