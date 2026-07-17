package thaumcraft;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import thaumcraft.common.init.ModBlocks;
import thaumcraft.common.init.ModCreativeTabs;
import thaumcraft.common.init.ModEffects;
import thaumcraft.common.init.ModItems;

@Mod(Thaumcraft.MODID)
public class Thaumcraft {

    public static final String MODID = "thaumcraft";

    public Thaumcraft(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
    }
}
