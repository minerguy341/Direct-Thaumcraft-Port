package thaumcraft.api.aspects;

import net.minecraft.world.item.ItemStack;

/**
 * @author azanor
 *
 * Used by wispy essences and essentia phials to hold their aspects.
 * Useful for similar item containers that store their aspect information on the stack
 * (via a data component in this port) so TC automatically picks up the aspects they contain.
 */
public interface IEssentiaContainerItem {

    AspectList getAspects(ItemStack itemstack);

    void setAspects(ItemStack itemstack, AspectList aspects);

    /**
     * Return true if the contained aspect should not be used to calculate the actual item aspects. For example: jar labels.
     */
    boolean ignoreContainedAspects();
}
