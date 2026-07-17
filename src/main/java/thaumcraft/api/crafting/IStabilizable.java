package thaumcraft.api.crafting;

/**
 * This is no longer a thing and will be removed in the next major release.
 */
@Deprecated
public interface IStabilizable {

    @Deprecated
    void addStability();

    @Deprecated
    EnumStability getStability();

    @Deprecated
    enum EnumStability {
        VERY_STABLE, STABLE, UNSTABLE, VERY_UNSTABLE
    }
}
