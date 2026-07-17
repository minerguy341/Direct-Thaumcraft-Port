package thaumcraft.api.golems;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public enum EnumGolemTrait {
    SMART,
    DEFT,
    CLUMSY,
    FIGHTER,
    WHEELED,
    FLYER,
    CLIMBER,
    HEAVY,
    LIGHT,
    FRAGILE,
    REPAIR,
    SCOUT,
    ARMORED,
    BRUTAL,
    FIREPROOF,
    BREAKER,
    HAULER,
    RANGED,
    BLASTPROOF;

    static {
        CLUMSY.opposite = DEFT;
        DEFT.opposite = CLUMSY;

        HEAVY.opposite = LIGHT;
        LIGHT.opposite = HEAVY;

        FRAGILE.opposite = ARMORED;
        ARMORED.opposite = FRAGILE;
    }

    public final ResourceLocation icon;
    public EnumGolemTrait opposite;

    EnumGolemTrait() {
        this.icon = ResourceLocation.fromNamespaceAndPath("thaumcraft",
                "textures/misc/golem/tag_" + name().toLowerCase() + ".png");
    }

    public MutableComponent getLocalizedName() {
        return Component.translatable("golem.trait." + this.name().toLowerCase());
    }

    public MutableComponent getLocalizedDescription() {
        return Component.translatable("golem.trait.text." + this.name().toLowerCase());
    }
}
