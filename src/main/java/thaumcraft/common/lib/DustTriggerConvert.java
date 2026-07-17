package thaumcraft.common.lib;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IDustTrigger;

/**
 * Simple salis mundus conversion: clicked block becomes another block, or breaks and
 * drops an item (the bookshelf-to-Thaumonomicon rite).
 */
public class DustTriggerConvert implements IDustTrigger {

    private final String research;
    private final Block target;
    private final Supplier<? extends Block> replacement;
    private final Supplier<? extends Item> drop;

    public DustTriggerConvert(String research, Block target,
            Supplier<? extends Block> replacement, Supplier<? extends Item> drop) {
        this.research = research;
        this.target = target;
        this.replacement = replacement;
        this.drop = drop;
    }

    @Override
    public Placement getValidFace(Level world, Player player, BlockPos pos, Direction face) {
        if (research != null && !research.isEmpty()
                && !ThaumcraftCapabilities.knowsResearch(player, research)) return null;
        return world.getBlockState(pos).is(target) ? new Placement(0, 0, 0, face) : null;
    }

    @Override
    public void execute(Level world, Player player, BlockPos pos, Placement placement, Direction side) {
        if (replacement != null) {
            world.setBlockAndUpdate(pos, replacement.get().defaultBlockState());
        } else {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        if (drop != null) {
            Block.popResource(world, pos, new ItemStack(drop.get()));
        }
        world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
