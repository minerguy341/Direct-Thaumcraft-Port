package thaumcraft.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)
public final class ClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        // TODO(Phase 8): gate behind "has scanned this item" once scan state syncs per-thing.
        AspectList aspects = AspectHelper.getObjectAspects(event.getItemStack());
        if (aspects == null || aspects.size() == 0) return;

        if (!net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            event.getToolTip().add(Component.translatable("tc.aspects.shift")
                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
            return;
        }
        for (Aspect aspect : aspects.getAspectsSortedByAmount()) {
            event.getToolTip().add(Component.literal(" " + aspects.getAmount(aspect) + " × ")
                    .withStyle(ChatFormatting.DARK_PURPLE)
                    .append(Component.literal(aspect.getName())
                            .withStyle(ChatFormatting.LIGHT_PURPLE)));
        }
    }

    private ClientEvents() {
    }
}
