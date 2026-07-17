package thaumcraft.api.golems.parts;

import java.util.LinkedHashMap;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemMaterial extends GolemPartBase {

    private static final LinkedHashMap<String, GolemMaterial> REGISTRY = new LinkedHashMap<>();

    public GolemMaterial(String key, String research, ItemStack[] components, EnumGolemTrait... traits) {
        super(key, research, components, traits);
    }

    public static void register(GolemMaterial part) {
        part.id = (byte) REGISTRY.size();
        REGISTRY.put(part.key, part);
    }

    public static GolemMaterial get(String key) {
        return REGISTRY.get(key);
    }

    public static GolemMaterial[] getMaterials() {
        return REGISTRY.values().toArray(new GolemMaterial[0]);
    }
}
