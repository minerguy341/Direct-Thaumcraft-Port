package thaumcraft.api.golems.parts;

import java.util.LinkedHashMap;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemLeg extends GolemPartBase {

    private static final LinkedHashMap<String, GolemLeg> REGISTRY = new LinkedHashMap<>();

    public GolemLeg(String key, String research, ItemStack[] components, EnumGolemTrait... traits) {
        super(key, research, components, traits);
    }

    public static void register(GolemLeg part) {
        part.id = (byte) REGISTRY.size();
        REGISTRY.put(part.key, part);
    }

    public static GolemLeg get(String key) {
        return REGISTRY.get(key);
    }

    public static GolemLeg[] getLegs() {
        return REGISTRY.values().toArray(new GolemLeg[0]);
    }
}
