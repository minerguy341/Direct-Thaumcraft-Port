package thaumcraft.api.research;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftInvHelper;

public class ScanItem implements IScanThing {

    String research;
    ItemStack stack;

    public ScanItem(String research, ItemStack stack) {
        this.research = research;
        this.stack = stack;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj == null) return false;

        ItemStack is = null;

        if (obj instanceof ItemStack st)
            is = st;
        if (obj instanceof ItemEntity ie && !ie.getItem().isEmpty())
            is = ie.getItem();

        return is != null && !is.isEmpty() && ThaumcraftInvHelper.areItemStacksEqualForCrafting(is, stack);
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }

}
