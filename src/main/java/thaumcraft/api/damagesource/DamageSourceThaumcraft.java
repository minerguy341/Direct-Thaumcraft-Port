package thaumcraft.api.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Thaumcraft damage types. Damage is data-driven in modern Minecraft: the type definitions
 * live in data/thaumcraft/damage_type/*.json and armor-bypass/magic behaviour is applied
 * via damage type tags.
 */
public final class DamageSourceThaumcraft {

    public static final ResourceKey<DamageType> TAINT = key("taint");
    public static final ResourceKey<DamageType> TENTACLE = key("tentacle");
    public static final ResourceKey<DamageType> SWARM = key("swarm");
    public static final ResourceKey<DamageType> DISSOLVE = key("dissolve");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE,
                ResourceLocation.fromNamespaceAndPath("thaumcraft", name));
    }

    public static DamageSource taint(Level level) {
        return source(level, TAINT);
    }

    public static DamageSource tentacle(Level level) {
        return source(level, TENTACLE);
    }

    public static DamageSource swarm(Level level) {
        return source(level, SWARM);
    }

    public static DamageSource dissolve(Level level) {
        return source(level, DISSOLVE);
    }

    public static DamageSource causeSwarmDamage(LivingEntity attacker) {
        return entitySource(attacker.level(), SWARM, attacker);
    }

    public static DamageSource causeTentacleDamage(LivingEntity attacker) {
        return entitySource(attacker.level(), TENTACLE, attacker);
    }

    private static DamageSource source(Level level, ResourceKey<DamageType> type) {
        return new DamageSource(level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type));
    }

    private static DamageSource entitySource(Level level, ResourceKey<DamageType> type, LivingEntity attacker) {
        return new DamageSource(level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker);
    }

    private DamageSourceThaumcraft() {
    }

}
