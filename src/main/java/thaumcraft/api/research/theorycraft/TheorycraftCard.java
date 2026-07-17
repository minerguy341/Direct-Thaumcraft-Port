package thaumcraft.api.research.theorycraft;

import java.util.Arrays;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * See CardAnalyze for an example
 *
 * @author Azanor
 */
public abstract class TheorycraftCard {

    private long seed = -1;

    /**
     * A seed value used to determine random attributes associated with the card
     */
    public long getSeed() {
        if (seed < 0) this.setSeed(System.nanoTime());
        return seed;
    }

    /**
     * This method is run when card is initially created.
     * @return if the card can not be initialized for some reason it will be discarded and a new one created.
     */
    public boolean initialize(Player player, ResearchTableData data) {
        return true;
    }

    /**
     * If true this card cannot come up in the normal draw rotation - it only appears if added by a mutator block
     */
    public boolean isAidOnly() {
        return false;
    }

    /**
     * How much inspiration this card costs to activate. Can be zero. Negative numbers will return inspiration.
     */
    public abstract int getInspirationCost();

    /**
     * The research category this card is associated with. Can be null if it is not linked to anything.
     */
    public String getResearchCategory() {
        return null;
    }

    /**
     * Name of the card. Will be shown in gui.
     */
    public abstract Component getLocalizedName();

    /**
     * Text of the card. Will be shown in gui.
     */
    public abstract Component getLocalizedText();

    /**
     * The items required to complete this operation.
     * If a null is returned no items are required. The array itself can contain null itemstacks -
     * that signifies an item is required, but it will display as a ? in the GUI.
     * You need to take care of consuming and checking for those items yourself in the activate method (see below).
     * Non-null items will be handled automatically.
     */
    public ItemStack[] getRequiredItems() {
        return null;
    }

    /**
     * Will the listed items be consumed when the card is picked.
     */
    public boolean[] getRequiredItemsConsumed() {
        if (getRequiredItems() != null) {
            boolean[] b = new boolean[getRequiredItems().length];
            Arrays.fill(b, false);
            return b;
        }
        return null;
    }

    /**
     * Perform the cards functionality on the current research table data.
     * You need to do all the proper checks for items carried and so forth in this method,
     * as well as consuming them where needed.
     * @return if the action was successful
     */
    public abstract boolean activate(Player player, ResearchTableData data);

    /**
     * Internal use only. This should not be called unless you want to mess things up.
     */
    public void setSeed(long seed) {
        this.seed = Math.abs(seed);
    }

    /**
     * Called when card is saved
     */
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("seed", seed);
        return nbt;
    }

    /**
     * Called when card is loaded
     */
    public void deserialize(CompoundTag nbt) {
        if (nbt == null) return;
        seed = nbt.getLong("seed");
    }

}
