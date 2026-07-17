package thaumcraft.api.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

/**
 * Factory for indirect (projectile-style) Thaumcraft damage: the direct entity is the
 * projectile/effect, the causing entity is the true attacker. Replaces the 1.12.2
 * EntityDamageSourceIndirect subclass; behaviour flags live on the damage type JSON + tags.
 */
public final class DamageSourceIndirectThaumcraftEntity {

    public static DamageSource create(ResourceKey<DamageType> type, Entity direct, Entity causing) {
        return new DamageSource(direct.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), direct, causing);
    }

    private DamageSourceIndirectThaumcraftEntity() {
    }

}
