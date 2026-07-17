package thaumcraft.api.golems.parts;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;

/**
 * Base data shared by all golem part types.
 *
 * NOTE: the golems/parts classes were missing from Azanor's published b26 API, so these are
 * reconstructed from the interface contract in IGolemProperties. Expect them to grow when the
 * golem milestone is implemented.
 */
public abstract class GolemPartBase {

    /** Runtime id assigned at registration; stable within a session, persisted by key. */
    public byte id;
    public final String key;
    public final String research;
    public final ItemStack[] components;
    public final EnumGolemTrait[] traits;

    protected GolemPartBase(String key, String research, ItemStack[] components, EnumGolemTrait... traits) {
        this.key = key;
        this.research = research;
        this.components = components;
        this.traits = traits;
    }

}
