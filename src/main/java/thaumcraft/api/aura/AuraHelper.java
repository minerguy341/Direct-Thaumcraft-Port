package thaumcraft.api.aura;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApi;

public class AuraHelper {

    /**
     * Consume vis from the aura at the given location
     * @return how much was actually drained
     */
    public static float drainVis(Level world, BlockPos pos, float amount, boolean simulate) {
        return ThaumcraftApi.internalMethods.drainVis(world, pos, amount, simulate);
    }

    /**
     * Consume flux from the aura at the given location
     * Added for completeness, but should really not be used. Add instability instead.
     * @return how much was actually drained
     */
    public static float drainFlux(Level world, BlockPos pos, float amount, boolean simulate) {
        return ThaumcraftApi.internalMethods.drainFlux(world, pos, amount, simulate);
    }

    /**
     * Adds vis to the aura at the given location.
     */
    public static void addVis(Level world, BlockPos pos, float amount) {
        ThaumcraftApi.internalMethods.addVis(world, pos, amount);
    }

    /**
     * Get how much vis is in the aura at the given location.
     */
    public static float getVis(Level world, BlockPos pos) {
        return ThaumcraftApi.internalMethods.getVis(world, pos);
    }

    /**
     * Adds flux to the aura at the specified block position.
     * @param amount how much stability to remove
     * @param showEffect if set to true, a flux smoke effect and sound will also be displayed. Use in moderation.
     */
    public static void polluteAura(Level world, BlockPos pos, float amount, boolean showEffect) {
        ThaumcraftApi.internalMethods.addFlux(world, pos, amount, showEffect);
    }

    /**
     * Get how much flux is in the aura at the given location.
     */
    public static float getFlux(Level world, BlockPos pos) {
        return ThaumcraftApi.internalMethods.getFlux(world, pos);
    }

    /**
     * Gets the general aura baseline at the given location
     */
    public static int getAuraBase(Level world, BlockPos pos) {
        return ThaumcraftApi.internalMethods.getAuraBase(world, pos);
    }

    /**
     * Gets if the local aura for the given aspect is below 10% and that the player has the node preserver research.
     * If the passed in player is null it will ignore the need for the research to be completed and just assume it is.
     */
    public static boolean shouldPreserveAura(Level world, Player player, BlockPos pos) {
        return ThaumcraftApi.internalMethods.shouldPreserveAura(world, player, pos);
    }
}
