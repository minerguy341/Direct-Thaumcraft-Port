package thaumcraft.api.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * @author Azanor
 *
 * Items with this interface can be recharged in wand pedestals and similar devices.
 * All values are stored on the stack (via a data component in this port).
 *
 * See RechargeHelper for methods to handle actual recharging of the item.
 */
public interface IRechargable {

    /**
     * @param player passed entity may be null so check first
     * @return how much vis this item can hold
     */
    int getMaxCharge(ItemStack stack, LivingEntity player);

    /**
     * @return when the charge will be displayed in the built-in hud display for chargable items
     */
    EnumChargeDisplay showInHud(ItemStack stack, LivingEntity player);

    enum EnumChargeDisplay {
        /** never */
        NEVER,
        /** whenever the charge changes */
        NORMAL,
        /** whenever charge changes to 0%, 25%, 50%, 75% or 100% */
        PERIODIC
    }

}
