package thaumcraft.api.aspects;

import net.minecraft.core.Direction;

/**
 * @author Azanor
 * This interface is used by block entities that use or transport essentia.
 * Only block entities that implement this interface will be able to connect to essentia tubes or other thaumic devices
 */
public interface IEssentiaTransport {

    /**
     * Is this block entity able to connect to other essentia users/sources on the specified side?
     */
    boolean isConnectable(Direction face);

    /**
     * Is this side used to input essentia?
     */
    boolean canInputFrom(Direction face);

    /**
     * Is this side used to output essentia?
     */
    boolean canOutputTo(Direction face);

    /**
     * Sets the amount of suction this block will apply
     */
    void setSuction(Aspect aspect, int amount);

    /**
     * Returns the type of suction this block is applying.
     * A return type of null indicates the suction is untyped and the first thing available will be drawn
     */
    Aspect getSuctionType(Direction face);

    /**
     * Returns the strength of suction this block is applying.
     */
    int getSuctionAmount(Direction face);

    /**
     * remove the specified amount of essentia from this transport block entity
     * @return how much was actually taken
     */
    int takeEssentia(Aspect aspect, int amount, Direction face);

    /**
     * add the specified amount of essentia to this transport block entity
     * @return how much was actually added
     */
    int addEssentia(Aspect aspect, int amount, Direction face);

    /**
     * What type of essentia this contains
     */
    Aspect getEssentiaType(Direction face);

    /**
     * How much essentia this block contains
     */
    int getEssentiaAmount(Direction face);

    /**
     * Essentia will not be drawn from this container unless the suction exceeds this amount.
     */
    int getMinimumSuction();

}
