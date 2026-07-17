package thaumcraft.api.crafting;

import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import thaumcraft.api.aspects.AspectList;

/**
 * Arcane workbench recipes are real recipe-manager recipes in this port: shaped/shapeless
 * variants with codec-based serializers arrive with the crafting-station implementation.
 */
public interface IArcaneRecipe extends Recipe<CraftingInput>, IThaumcraftRecipe {

    int getVis();

    AspectList getCrystals();

    @Override
    String getGroup();
}
