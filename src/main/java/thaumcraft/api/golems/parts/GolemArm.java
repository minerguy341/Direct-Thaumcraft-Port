package thaumcraft.api.golems.parts;

import java.util.LinkedHashMap;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemArm extends GolemPartBase {

    private static final LinkedHashMap<String, GolemArm> REGISTRY = new LinkedHashMap<>();

    public GolemArm(String key, String research, ItemStack[] components, EnumGolemTrait... traits) {
        super(key, research, components, traits);
    }

    public static void register(GolemArm part) {
        part.id = (byte) REGISTRY.size();
        REGISTRY.put(part.key, part);
    }

    public static GolemArm get(String key) {
        return REGISTRY.get(key);
    }

    public static GolemArm[] getArms() {
        return REGISTRY.values().toArray(new GolemArm[0]);
    }
}
