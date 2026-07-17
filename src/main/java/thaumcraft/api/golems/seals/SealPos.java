package thaumcraft.api.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SealPos {

    public BlockPos pos;
    public Direction face;

    public SealPos(BlockPos pos, Direction face) {
        this.pos = pos;
        this.face = face;
    }

    @Override
    public int hashCode() {
        byte b0 = (byte) (face.ordinal() + 1);
        int i = 31 * b0 + this.pos.getX();
        i = 31 * i + this.pos.getY();
        i = 31 * i + this.pos.getZ();
        return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof SealPos sp)) {
            return false;
        } else {
            return this.pos.equals(sp.pos) && this.face.equals(sp.face);
        }
    }
}
