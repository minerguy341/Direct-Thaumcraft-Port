package thaumcraft.api.research;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.ThaumcraftApi.EntityTagsNBT;
import thaumcraft.api.ThaumcraftApiHelper;

public class ScanEntity implements IScanThing {

    String research;
    Class<?> entityClass;
    EntityTagsNBT[] NBTData;

    /**
     * false if the specific entity class should be used, or true if anything that inherits from that class is also allowed.
     */
    boolean inheritedClasses;

    public ScanEntity(String research, Class<?> entityClass, boolean inheritedClasses) {
        this.research = research;
        this.entityClass = entityClass;
        this.inheritedClasses = inheritedClasses;
    }

    public ScanEntity(String research, Class<?> entityClass, boolean inheritedClasses, EntityTagsNBT... nbt) {
        this(research, entityClass, inheritedClasses);
        this.NBTData = nbt;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj != null && ((!inheritedClasses && entityClass == obj.getClass()) ||
                (inheritedClasses && entityClass.isInstance(obj)))) {
            if (NBTData != null && NBTData.length > 0) {
                CompoundTag tc = new CompoundTag();
                ((Entity) obj).saveWithoutId(tc);
                for (EntityTagsNBT nbt : NBTData) {
                    if (!tc.contains(nbt.name) || !ThaumcraftApiHelper.nbtMatches(tc, nbt.name, nbt.value)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }

}
