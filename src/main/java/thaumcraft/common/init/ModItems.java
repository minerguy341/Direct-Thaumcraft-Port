package thaumcraft.common.init;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final DeferredItem<Item> THAUMONOMICON =
            ITEMS.registerSimpleItem("thaumonomicon", new Item.Properties().stacksTo(1));

    private ModItems() {
    }
}
