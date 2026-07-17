package thaumcraft.api.crafting;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class InfusionRecipe implements IThaumcraftRecipe {

    public AspectList aspects;
    public String research;
    protected NonNullList<Ingredient> components = NonNullList.create();
    public Ingredient sourceInput; //Use Ingredient.EMPTY if the source item can be anything
    public Object recipeOutput;
    public int instability;

    public InfusionRecipe(String research, Object outputResult, int inst, AspectList aspects2, Object centralItem, Object... recipe) {
        this.research = research;
        this.recipeOutput = outputResult;
        this.aspects = aspects2;
        this.instability = inst;
        this.sourceInput = ThaumcraftApiHelper.getIngredient(centralItem);
        if (sourceInput == null) {
            throw new RuntimeException("Invalid infusion central item: " + centralItem);
        }
        for (Object in : recipe) {
            Ingredient ing = ThaumcraftApiHelper.getIngredient(in);
            if (ing != null) {
                components.add(ing);
            } else {
                StringBuilder ret = new StringBuilder("Invalid infusion recipe: ");
                for (Object tmp : recipe) {
                    ret.append(tmp).append(", ");
                }
                ret.append(outputResult);
                throw new RuntimeException(ret.toString());
            }
        }
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(List<ItemStack> input, ItemStack central, Level world, Player player) {
        if (getRecipeInput() == null) return false;
        if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
            return false;
        }
        return (getRecipeInput() == Ingredient.EMPTY || this.getRecipeInput().test(central))
                && RecipeMatcher.findMatches(input, getComponents()) != null;
    }

    @Override
    public String getResearch() {
        return research;
    }

    public Ingredient getRecipeInput() {
        return sourceInput;
    }

    public NonNullList<Ingredient> getComponents() {
        return components;
    }

    public Object getRecipeOutput() {
        return recipeOutput;
    }

    public AspectList getAspects() {
        return aspects;
    }

    public Object getRecipeOutput(Player player, ItemStack input, List<ItemStack> comps) {
        return recipeOutput;
    }

    public AspectList getAspects(Player player, ItemStack input, List<ItemStack> comps) {
        return aspects;
    }

    public int getInstability(Player player, ItemStack input, List<ItemStack> comps) {
        return instability;
    }

    private String group = "";

    @Override
    public String getGroup() {
        return group;
    }

    public InfusionRecipe setGroup(ResourceLocation s) {
        this.group = s.toString();
        return this;
    }
}
