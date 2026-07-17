package thaumcraft.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menus.ArcaneWorkbenchMenu;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Thaumcraft.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ArcaneWorkbenchMenu>> ARCANE_WORKBENCH =
            MENUS.register("arcane_workbench",
                    () -> new MenuType<>(ArcaneWorkbenchMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {
    }
}
