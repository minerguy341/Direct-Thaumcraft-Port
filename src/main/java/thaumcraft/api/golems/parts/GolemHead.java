package thaumcraft.api.golems.parts;

import java.util.LinkedHashMap;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemHead extends GolemPartBase {

    private static final LinkedHashMap<String, GolemHead> REGISTRY = new LinkedHashMap<>();

    public GolemHead(String key, String research, ItemStack[] components, EnumGolemTrait... traits) {
        super(key, research, components, traits);
    }

    public static void register(GolemHead part) {
        part.id = (byte) REGISTRY.size();
        REGISTRY.put(part.key, part);
    }

    public static GolemHead get(String key) {
        return REGISTRY.get(key);
    }

    public static GolemHead[] getHeads() {
        return REGISTRY.values().toArray(new GolemHead[0]);
    }
}
