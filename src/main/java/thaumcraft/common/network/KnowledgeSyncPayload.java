package thaumcraft.common.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

/**
 * Full research/knowledge state pushed server -> client on login, respawn, and change.
 */
public record KnowledgeSyncPayload(CompoundTag data) implements CustomPacketPayload {

    public static final Type<KnowledgeSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "knowledge_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeSyncPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.TRUSTED_COMPOUND_TAG, KnowledgeSyncPayload::data,
                    KnowledgeSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
