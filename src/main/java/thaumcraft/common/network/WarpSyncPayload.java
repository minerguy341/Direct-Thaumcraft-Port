package thaumcraft.common.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

/**
 * Warp counters pushed server -> client.
 */
public record WarpSyncPayload(CompoundTag data) implements CustomPacketPayload {

    public static final Type<WarpSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "warp_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WarpSyncPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.TRUSTED_COMPOUND_TAG, WarpSyncPayload::data,
                    WarpSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
