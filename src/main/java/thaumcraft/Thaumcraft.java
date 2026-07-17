package thaumcraft;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.init.ModAttachments;
import thaumcraft.common.init.ModBlocks;
import thaumcraft.common.init.ModCreativeTabs;
import thaumcraft.common.init.ModDataMaps;
import thaumcraft.common.init.ModEffects;
import thaumcraft.common.init.ModItems;
import thaumcraft.common.lib.ThaumcraftInternalMethods;

@Mod(Thaumcraft.MODID)
public class Thaumcraft {

    public static final String MODID = "thaumcraft";

    @SuppressWarnings("unchecked")
    public Thaumcraft(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);
        modEventBus.addListener(ModDataMaps::register);

        ThaumcraftApi.internalMethods = new ThaumcraftInternalMethods();
        ThaumcraftCapabilities.KNOWLEDGE =
                () -> (AttachmentType<IPlayerKnowledge>) (AttachmentType<?>) ModAttachments.KNOWLEDGE.get();
        ThaumcraftCapabilities.WARP =
                () -> (AttachmentType<IPlayerWarp>) (AttachmentType<?>) ModAttachments.WARP.get();
    }
}
