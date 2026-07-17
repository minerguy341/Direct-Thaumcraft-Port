package thaumcraft.api.aspects;

/**
 * @author azanor
 *
 * Used by blocks like the crucible and alembic to hold their aspects.
 * Block entities extending this interface will have their aspects show up when viewed by goggles of revealing
 */
public interface IAspectContainer {

    AspectList getAspects();

    void setAspects(AspectList aspects);

    /**
     * This method is used to determine if a specific aspect can be added to this container.
     */
    boolean doesContainerAccept(Aspect tag);

    /**
     * This method is used to add a certain amount of an aspect to the block entity.
     * @return the amount of aspect left over that could not be added.
     */
    int addToContainer(Aspect tag, int amount);

    /**
     * Removes a certain amount of a specific aspect from the block entity
     * @return true if that amount of aspect was available and was removed
     */
    boolean takeFromContainer(Aspect tag, int amount);

    /**
     * removes a bunch of different aspects and amounts from the block entity.
     * @param ot the AspectList that contains the aspects and their amounts.
     * @return true if all the aspects and their amounts were available and successfully removed
     */
    @Deprecated
    boolean takeFromContainer(AspectList ot);

    /**
     * Checks if the block entity contains the listed amount (or more) of the aspect
     */
    boolean doesContainerContainAmount(Aspect tag, int amount);

    /**
     * Checks if the block entity contains all the listed aspects and their amounts
     * @param ot the AspectList that contains the aspects and their amounts.
     */
    @Deprecated
    boolean doesContainerContain(AspectList ot);

    /**
     * Returns how much of the aspect this block entity contains
     * @return the amount of that aspect found
     */
    int containerContains(Aspect tag);

}
