package thaumcraft.api.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Marker debuff read by wand/casting logic: afflicted casters pay more vis.
 * No per-tick behaviour of its own.
 */
public class PotionVisExhaust extends MobEffect {

    public PotionVisExhaust() {
        super(MobEffectCategory.HARMFUL, 0x5A6273);
    }

}
