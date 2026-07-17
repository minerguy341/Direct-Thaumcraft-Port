package thaumcraft.api.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;

public class PotionFluxTaint extends MobEffect {

    public PotionFluxTaint() {
        super(MobEffectCategory.HARMFUL, 0x800080);
    }

    @Override
    public boolean applyEffectTick(LivingEntity target, int amplifier) {
        // TODO(port): also heal entities carrying the champion-mod attribute once
        // ThaumcraftApiHelper.CHAMPION_MOD is ported with the internals layer.
        if (target instanceof ITaintedMob) {
            target.heal(1);
        } else if (!target.isInvertedHealAndHarm()
                && (target.getMaxHealth() > 1 || target instanceof Player)) {
            target.hurt(DamageSourceThaumcraft.taint(target.level()), 1);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int k = 40 >> amplifier;
        return k <= 0 || duration % k == 0;
    }

}
