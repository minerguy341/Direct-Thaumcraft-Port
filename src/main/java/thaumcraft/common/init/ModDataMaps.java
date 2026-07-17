package thaumcraft.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.AspectList;

public final class ModDataMaps {

    /**
     * Datapack-driven item aspect assignments:
     * data/thaumcraft/data_maps/item/item_aspects.json
     */
    public static final DataMapType<Item, AspectList> ITEM_ASPECTS =
            DataMapType.builder(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "item_aspects"),
                            Registries.ITEM, AspectList.CODEC)
                    .synced(AspectList.CODEC, false)
                    .build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(ITEM_ASPECTS);
    }

    private ModDataMaps() {
    }
}
