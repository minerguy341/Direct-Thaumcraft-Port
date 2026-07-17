package thaumcraft.common.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thaumcraft.MODID);

    // arcane stone family
    public static final DeferredBlock<Block> STONE_ARCANE = stone("stone_arcane");
    public static final DeferredBlock<Block> STONE_ARCANE_BRICK = stone("stone_arcane_brick");

    // ores
    public static final DeferredBlock<Block> ORE_CINNABAR = stone("ore_cinnabar");
    public static final DeferredBlock<Block> ORE_AMBER = stone("ore_amber");

    public static final DeferredBlock<Block> AMBER_BLOCK = register("amber_block",
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE)
                    .strength(1.0F).sound(SoundType.AMETHYST));

    // trees
    public static final DeferredBlock<RotatedPillarBlock> LOG_GREATWOOD = log("log_greatwood");
    public static final DeferredBlock<RotatedPillarBlock> LOG_SILVERWOOD = log("log_silverwood");
    public static final DeferredBlock<Block> PLANK_GREATWOOD = plank("plank_greatwood");
    public static final DeferredBlock<Block> PLANK_SILVERWOOD = plank("plank_silverwood");
    public static final DeferredBlock<LeavesBlock> LEAVES_GREATWOOD = leaves("leaves_greatwood");
    public static final DeferredBlock<LeavesBlock> LEAVES_SILVERWOOD = leaves("leaves_silverwood");

    // stations (plain placeholder blocks until their block entities land)
    public static final DeferredBlock<Block> CRUCIBLE = register("crucible",
            BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                    .strength(3.0F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<Block> ARCANE_WORKBENCH = wood("arcane_workbench");
    public static final DeferredBlock<Block> RESEARCH_TABLE = wood("research_table");
    public static final DeferredBlock<Block> PEDESTAL_ARCANE = register("pedestal_arcane",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(2.0F, 10.0F).sound(SoundType.STONE).noOcclusion());

    private static DeferredBlock<Block> register(String name, BlockBehaviour.Properties props) {
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(name, props);
        ModItems.ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static DeferredBlock<Block> stone(String name) {
        return register(name, BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops().strength(2.0F, 10.0F).sound(SoundType.STONE));
    }

    private static DeferredBlock<Block> wood(String name) {
        return register(name, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                .strength(2.0F).sound(SoundType.WOOD));
    }

    private static DeferredBlock<Block> plank(String name) {
        return wood(name);
    }

    private static DeferredBlock<RotatedPillarBlock> log(String name) {
        DeferredBlock<RotatedPillarBlock> block = BLOCKS.register(name,
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD)));
        ModItems.ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static DeferredBlock<LeavesBlock> leaves(String name) {
        DeferredBlock<LeavesBlock> block = BLOCKS.register(name,
                () -> new LeavesBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT).strength(0.2F).randomTicks()
                        .sound(SoundType.GRASS).noOcclusion()
                        .isSuffocating((state, level, pos) -> false)
                        .isViewBlocking((state, level, pos) -> false)));
        ModItems.ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private ModBlocks() {
    }
}
