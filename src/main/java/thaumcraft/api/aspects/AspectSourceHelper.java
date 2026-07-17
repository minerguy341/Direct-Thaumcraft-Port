package thaumcraft.api.aspects;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Bridges the API to Thaumcraft's essentia-handling implementation. The 1.12.2 version used
 * reflection into thaumcraft.common; this port uses an injectable hook the implementation
 * assigns at startup.
 */
public class AspectSourceHelper {

    public interface EssentiaHook {
        boolean drainEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range);

        boolean findEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range);
    }

    private static EssentiaHook hook = new EssentiaHook() {
        @Override
        public boolean drainEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range) {
            return false;
        }

        @Override
        public boolean findEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range) {
            return false;
        }
    };

    /** Assigned by the Thaumcraft implementation. Do not call from addons. */
    public static void setHook(EssentiaHook impl) {
        hook = impl;
    }

    /**
     * This method is what is used to drain essentia from jars and other sources for things like
     * infusion crafting or powering the arcane furnace. A record of possible sources are kept track of
     * and refreshed as needed around the calling block entity. This also renders the essentia trail particles.
     * Only 1 essentia is drained at a time
     * @param direction the direction from which you wish to drain. null simply seeks in all directions.
     * @param range how many blocks you wish to search for essentia sources.
     * @return true if essentia was found and removed from a source.
     */
    public static boolean drainEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range) {
        return hook.drainEssentia(tile, aspect, direction, range);
    }

    /**
     * This method returns if there is any essentia of the passed type that can be drained. It in no way checks how
     * much there is, only if an essentia container nearby contains at least 1 point worth.
     * @param direction the direction from which you wish to drain. null simply seeks in all directions.
     * @param range how many blocks you wish to search for essentia sources.
     * @return true if essentia was found.
     */
    public static boolean findEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range) {
        return hook.findEssentia(tile, aspect, direction, range);
    }
}
