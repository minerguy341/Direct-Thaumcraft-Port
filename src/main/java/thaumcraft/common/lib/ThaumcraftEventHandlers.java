package thaumcraft.common.lib;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public final class ThaumcraftEventHandlers {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        AspectInference.rebuild(event.getServer());
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        // fires on login (per player) and on /reload (player == null)
        if (event.getPlayer() == null) {
            AspectInference.rebuild(event.getPlayerList().getServer());
        }
        event.getRelevantPlayers().forEach(ThaumcraftEventHandlers::syncPlayer);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) syncPlayer(sp);
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) syncPlayer(sp);
    }

    private static void syncPlayer(ServerPlayer player) {
        ThaumcraftCapabilities.getKnowledge(player).sync(player);
        ThaumcraftCapabilities.getWarp(player).sync(player);
    }

    private ThaumcraftEventHandlers() {
    }
}
