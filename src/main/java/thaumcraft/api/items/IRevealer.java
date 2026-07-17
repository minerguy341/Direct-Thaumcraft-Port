package thaumcraft.api.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * @author Azanor
 *
 * Equipped or held items that extend this class will make aura nodes and related objects visible in world.
 * (Un-deprecated in this port: nodes are back.)
 */
public interface IRevealer {

    /**
     * If this method returns true the nodes will be visible.
     */
    boolean showNodes(ItemStack itemstack, LivingEntity player);

}
