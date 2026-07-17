package thaumcraft.api.research;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

/**
 * Scan trigger matching any block in a block tag. Replaces the 1.12.2 ScanMaterial (the
 * Material system was removed from Minecraft; tags are the modern classification).
 */
public class ScanBlockTag implements IScanThing {

    String research;
    TagKey<Block>[] tags;

    @SafeVarargs
    public ScanBlockTag(String research, TagKey<Block>... tags) {
        this.research = research;
        this.tags = tags;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj instanceof BlockPos pos) {
            for (TagKey<Block> tag : tags)
                if (player.level().getBlockState(pos).is(tag))
                    return true;
        }
        return false;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
