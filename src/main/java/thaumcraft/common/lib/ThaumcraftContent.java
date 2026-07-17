package thaumcraft.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.common.init.ModBlocks;
import thaumcraft.common.init.ModItems;

/**
 * Code-registered content: dust triggers and crucible recipes. Research gates are empty
 * until research content lands in Phase 8; balance numbers are first-pass.
 */
public final class ThaumcraftContent {

    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // salis mundus rites
            IDustTrigger.registerDustTrigger(
                    new DustTriggerConvert("", Blocks.CAULDRON, ModBlocks.CRUCIBLE, null));
            IDustTrigger.registerDustTrigger(
                    new DustTriggerConvert("", Blocks.CRAFTING_TABLE, ModBlocks.ARCANE_WORKBENCH, null));
            IDustTrigger.registerDustTrigger(
                    new DustTriggerConvert("", Blocks.BOOKSHELF, null, ModItems.THAUMONOMICON));

            // crucible recipes
            ThaumcraftApi.addCrucibleRecipe(rl("thaumium"), new CrucibleRecipe("",
                    new ItemStack(ModItems.INGOT_THAUMIUM.get()), new ItemStack(Items.IRON_INGOT),
                    new AspectList().add(Aspect.MAGIC, 10).add(Aspect.METAL, 5)));

            ThaumcraftApi.addCrucibleRecipe(rl("alumentum"), new CrucibleRecipe("",
                    new ItemStack(ModItems.ALUMENTUM.get()), new ItemStack(Items.COAL),
                    new AspectList().add(Aspect.ENERGY, 10).add(Aspect.ENTROPY, 5).add(Aspect.FIRE, 5)));

            ThaumcraftApi.addCrucibleRecipe(rl("salis_mundus"), new CrucibleRecipe("",
                    new ItemStack(ModItems.SALIS_MUNDUS.get(), 2), new ItemStack(Items.REDSTONE),
                    new AspectList().add(Aspect.MAGIC, 5).add(Aspect.EXCHANGE, 2)));
        });
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private ThaumcraftContent() {
    }
}
