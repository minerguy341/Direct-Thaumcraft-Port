package thaumcraft.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.init.ModBlockEntities;

public class CrucibleBlockEntity extends BlockEntity implements IAspectContainer {

    private AspectList aspects = new AspectList();
    private int heat;

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity be) {
        boolean heated = CrucibleBlock.isHeated(level, pos);
        int old = be.heat;
        if (heated && be.heat < 40) be.heat++;
        if (!heated && be.heat > 0) be.heat--;
        if ((old >= 20) != (be.heat >= 20)) be.setChanged();
    }

    public boolean isBoiling() {
        return heat >= 20 && getBlockState().getValue(CrucibleBlock.FULL);
    }

    /**
     * Called when an item entity falls into a boiling crucible: match a crucible recipe using
     * the item as catalyst, otherwise dissolve it into its aspects.
     */
    public void itemDropped(ItemEntity entity) {
        if (level == null || level.isClientSide || !isBoiling()) return;
        ItemStack stack = entity.getItem();

        boolean worked = false;
        while (!stack.isEmpty()) {
            CrucibleRecipe recipe = findRecipe(stack);
            if (recipe != null) {
                aspects = recipe.removeMatching(aspects);
                ItemStack result = recipe.getRecipeOutput().copy();
                Block.popResource(level, worldPosition.above(), result);
                stack.shrink(1);
                worked = true;
                continue;
            }
            AspectList dissolved = AspectHelper.getObjectAspects(stack);
            if (dissolved != null && dissolved.size() > 0) {
                aspects.add(dissolved);
                stack.shrink(1);
                worked = true;
            } else {
                break; // no aspects and no recipe: spit it back out untouched
            }
        }

        if (worked) {
            level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.5F, 1.2F);
            setChanged();
        }
        if (stack.isEmpty()) {
            entity.discard();
        } else {
            entity.setItem(stack);
        }
    }

    private CrucibleRecipe findRecipe(ItemStack catalyst) {
        for (Object recipe : ThaumcraftApi.getCraftingRecipes().values()) {
            if (recipe instanceof CrucibleRecipe cr && cr.matches(aspects, catalyst)) {
                return cr;
            }
        }
        return null;
    }

    /** Dump contents (block broken or emptied). Excess essentia becomes flux once aura lands. */
    public void spill() {
        aspects = new AspectList();
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        aspects.writeToNBT(tag);
        tag.putInt("heat", heat);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        aspects = new AspectList();
        aspects.readFromNBT(tag);
        heat = tag.getInt("heat");
    }

    // IAspectContainer

    @Override
    public AspectList getAspects() {
        return aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.aspects = aspects;
        setChanged();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        aspects.add(tag, amount);
        setChanged();
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (aspects.getAmount(tag) < amount) return false;
        aspects.remove(tag, amount);
        setChanged();
        return true;
    }

    @Override
    @Deprecated
    public boolean takeFromContainer(AspectList ot) {
        for (Aspect tag : ot.getAspects()) {
            if (aspects.getAmount(tag) < ot.getAmount(tag)) return false;
        }
        aspects.remove(ot);
        setChanged();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return aspects.getAmount(tag) >= amount;
    }

    @Override
    @Deprecated
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tag : ot.getAspects()) {
            if (aspects.getAmount(tag) < ot.getAmount(tag)) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return aspects.getAmount(tag);
    }
}
