package thaumcraft.api.research;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.internal.CommonInternals;

/**
 * Scan trigger matching any item in an item tag. Replaces the 1.12.2 ScanOreDictionary
 * (the ore dictionary became tags in modern Minecraft).
 */
public class ScanItemTag implements IScanThing {

    String research;
    TagKey<Item>[] entries;

    public ConcurrentHashMap<Integer, Boolean> cache = new ConcurrentHashMap<>();

    @SafeVarargs
    public ScanItemTag(String research, TagKey<Item>... entries) {
        this.research = research;
        this.entries = entries;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        ItemStack stack = null;
        if (obj != null) {
            if (obj instanceof BlockPos pos) {
                BlockState state = player.level().getBlockState(pos);
                stack = new ItemStack(state.getBlock());
            } else if (obj instanceof ItemStack st)
                stack = st;
            else if (obj instanceof ItemEntity ie && !ie.getItem().isEmpty())
                stack = ie.getItem();
        }

        if (stack != null && !stack.isEmpty()) {
            int hid = CommonInternals.generateUniqueItemstackId(stack);
            Boolean cached = cache.get(hid);
            if (cached != null) {
                return cached;
            }

            for (TagKey<Item> entry : entries) {
                if (stack.is(entry)) {
                    cache.put(hid, true);
                    return true;
                }
            }
            cache.put(hid, false);
        }

        return false;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
