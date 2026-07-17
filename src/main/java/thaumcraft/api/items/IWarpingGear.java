package thaumcraft.api.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author Azanor
 *
 * Armor, held items or curio slot items that implement this interface add warp when equipped or held.
 *
 * IMPORTANT:
 * A warp data component will serve much the same function without having to implement this
 * interface (the 1.12.2 "TC.WARP" nbt tag equivalent). Warp from this interface and the component stacks.
 */
public interface IWarpingGear {

    /**
     * returns how much warp this item adds while worn or held.
     */
    int getWarp(ItemStack itemstack, Player player);

}
