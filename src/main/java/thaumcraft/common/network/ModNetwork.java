package thaumcraft.common.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public final class ModNetwork {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(KnowledgeSyncPayload.TYPE, KnowledgeSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        ThaumcraftCapabilities.getKnowledge(context.player())
                                .deserializeNBT(context.player().registryAccess(), payload.data())));

        registrar.playToClient(WarpSyncPayload.TYPE, WarpSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        ThaumcraftCapabilities.getWarp(context.player())
                                .deserializeNBT(context.player().registryAccess(), payload.data())));
    }

    private ModNetwork() {
    }
}
