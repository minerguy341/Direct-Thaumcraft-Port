package thaumcraft.api.golems.parts;

import java.util.LinkedHashMap;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemAddon extends GolemPartBase {

    private static final LinkedHashMap<String, GolemAddon> REGISTRY = new LinkedHashMap<>();

    public GolemAddon(String key, String research, ItemStack[] components, EnumGolemTrait... traits) {
        super(key, research, components, traits);
    }

    public static void register(GolemAddon part) {
        part.id = (byte) REGISTRY.size();
        REGISTRY.put(part.key, part);
    }

    public static GolemAddon get(String key) {
        return REGISTRY.get(key);
    }

    public static GolemAddon[] getAddons() {
        return REGISTRY.values().toArray(new GolemAddon[0]);
    }
}
