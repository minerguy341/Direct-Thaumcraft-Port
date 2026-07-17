package thaumcraft.common.capabilities;

import java.util.EnumMap;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.api.capabilities.IPlayerWarp;

public class PlayerWarp implements IPlayerWarp {

    private final EnumMap<EnumWarpType, Integer> warp = new EnumMap<>(EnumWarpType.class);
    private int counter;

    @Override
    public void clear() {
        warp.clear();
        counter = 0;
    }

    @Override
    public int get(EnumWarpType type) {
        return warp.getOrDefault(type, 0);
    }

    @Override
    public void set(EnumWarpType type, int amount) {
        warp.put(type, Math.max(0, amount));
    }

    @Override
    public int add(EnumWarpType type, int amount) {
        int total = Math.max(0, get(type) + amount);
        warp.put(type, total);
        return total;
    }

    @Override
    public int reduce(EnumWarpType type, int amount) {
        return add(type, -amount);
    }

    @Override
    public void sync(ServerPlayer player) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                new thaumcraft.common.network.WarpSyncPayload(serializeNBT(player.registryAccess())));
    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public void setCounter(int amount) {
        counter = amount;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        for (EnumWarpType type : EnumWarpType.values()) {
            nbt.putInt(type.name().toLowerCase(), get(type));
        }
        nbt.putInt("counter", counter);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        clear();
        for (EnumWarpType type : EnumWarpType.values()) {
            warp.put(type, nbt.getInt(type.name().toLowerCase()));
        }
        counter = nbt.getInt("counter");
    }
}
