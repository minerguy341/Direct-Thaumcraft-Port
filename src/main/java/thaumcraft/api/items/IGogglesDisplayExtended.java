package thaumcraft.api.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

/**
 * An interface that you can attach to a block or block entity to display additional text when
 * viewed with an IGoggles item. Used by things like the Infusion Matrix to display additional
 * information.
 */
public interface IGogglesDisplayExtended {

    /**
     * What text to display onscreen. You can return multiple lines (discretion is advised).
     */
    Component[] getIGogglesText();

    /**
     * Returns the positional offset that text will be displayed at in relation to the object.
     */
    default Vec3 getIGogglesTextOffset() {
        return Vec3.ZERO;
    }

}
