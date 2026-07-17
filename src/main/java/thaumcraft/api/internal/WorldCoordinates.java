package thaumcraft.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WorldCoordinates implements Comparable<WorldCoordinates> {

    public BlockPos pos;

    public ResourceKey<Level> dim;

    public WorldCoordinates() {
    }

    public WorldCoordinates(BlockPos pos, ResourceKey<Level> d) {
        this.pos = pos;
        this.dim = d;
    }

    public WorldCoordinates(BlockEntity tile) {
        this.pos = tile.getBlockPos();
        this.dim = tile.getLevel().dimension();
    }

    public WorldCoordinates(WorldCoordinates other) {
        this.pos = other.pos;
        this.dim = other.dim;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WorldCoordinates coordinates)) {
            return false;
        } else {
            return this.pos.equals(coordinates.pos) && this.dim.equals(coordinates.dim);
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.pos.hashCode() + this.dim.location().hashCode();
    }

    /**
     * Compare the coordinate with another coordinate
     */
    public int compareWorldCoordinate(WorldCoordinates other) {
        return this.dim.equals(other.dim) ? this.pos.compareTo(other.pos) : -1;
    }

    public void set(BlockPos pos, ResourceKey<Level> d) {
        this.pos = pos;
        this.dim = d;
    }

    /**
     * Returns the squared distance between this coordinates and the coordinates given as argument.
     */
    public double getDistanceSquared(BlockPos pos) {
        return this.pos.distSqr(pos);
    }

    /**
     * Return the squared distance between this coordinates and the WorldCoordinates given as argument.
     */
    public double getDistanceSquaredToWorldCoordinates(WorldCoordinates other) {
        return this.getDistanceSquared(other.pos);
    }

    @Override
    public int compareTo(WorldCoordinates other) {
        return this.compareWorldCoordinate(other);
    }

    public void readNBT(CompoundTag nbt) {
        int x = nbt.getInt("w_x");
        int y = nbt.getInt("w_y");
        int z = nbt.getInt("w_z");
        this.pos = new BlockPos(x, y, z);
        this.dim = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,
                ResourceLocation.parse(nbt.getString("w_d")));
    }

    public void writeNBT(CompoundTag nbt) {
        nbt.putInt("w_x", pos.getX());
        nbt.putInt("w_y", pos.getY());
        nbt.putInt("w_z", pos.getZ());
        nbt.putString("w_d", dim.location().toString());
    }

}
