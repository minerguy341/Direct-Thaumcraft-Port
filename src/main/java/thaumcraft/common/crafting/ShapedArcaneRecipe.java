package thaumcraft.common.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.init.ModRecipeTypes;

/**
 * JSON-driven shaped arcane workbench recipe. Vis and crystal costs are stored and exposed
 * through IArcaneRecipe; charging them starts once wands land (Phase 6).
 */
public class ShapedArcaneRecipe implements IArcaneRecipe {

    final String group;
    final String research;
    final int vis;
    final AspectList crystals;
    final ShapedRecipePattern pattern;
    final ItemStack result;

    public ShapedArcaneRecipe(String group, String research, int vis, AspectList crystals,
            ShapedRecipePattern pattern, ItemStack result) {
        this.group = group;
        this.research = research;
        this.vis = vis;
        this.crystals = crystals;
        this.pattern = pattern;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return pattern.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= pattern.width() && height >= pattern.height();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return pattern.ingredients();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SHAPED_ARCANE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ARCANE_CRAFTING.get();
    }

    @Override
    public int getVis() {
        return vis;
    }

    @Override
    public AspectList getCrystals() {
        return crystals;
    }

    @Override
    public String getResearch() {
        return research;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public static class Serializer implements RecipeSerializer<ShapedArcaneRecipe> {

        public static final MapCodec<ShapedArcaneRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                com.mojang.serialization.Codec.STRING.optionalFieldOf("research", "").forGetter(r -> r.research),
                com.mojang.serialization.Codec.INT.optionalFieldOf("vis", 0).forGetter(r -> r.vis),
                AspectList.CODEC.optionalFieldOf("crystals", new AspectList()).forGetter(r -> r.crystals),
                ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result)
        ).apply(i, ShapedArcaneRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedArcaneRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ShapedArcaneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedArcaneRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ShapedArcaneRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            String research = buf.readUtf();
            int vis = buf.readVarInt();
            AspectList crystals = new AspectList();
            int count = buf.readVarInt();
            for (int x = 0; x < count; x++) {
                Aspect aspect = Aspect.getAspect(buf.readUtf());
                int amount = buf.readVarInt();
                if (aspect != null) crystals.add(aspect, amount);
            }
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            return new ShapedArcaneRecipe(group, research, vis, crystals, pattern, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, ShapedArcaneRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeUtf(recipe.research);
            buf.writeVarInt(recipe.vis);
            buf.writeVarInt(recipe.crystals.size());
            for (Aspect aspect : recipe.crystals.getAspects()) {
                buf.writeUtf(aspect.getTag());
                buf.writeVarInt(recipe.crystals.getAmount(aspect));
            }
            ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }
    }
}
