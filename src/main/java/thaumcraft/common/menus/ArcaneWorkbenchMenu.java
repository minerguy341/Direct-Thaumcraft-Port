package thaumcraft.common.menus;

import java.util.Optional;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IArcaneWorkbench;
import thaumcraft.common.init.ModBlocks;
import thaumcraft.common.init.ModMenus;
import thaumcraft.common.init.ModRecipeTypes;

/**
 * 3x3 arcane crafting. Matches arcane recipes first, then falls back to vanilla crafting
 * recipes so the bench stays useful early. Vis/crystal costs charge once wands land.
 */
public class ArcaneWorkbenchMenu extends AbstractContainerMenu implements IArcaneWorkbench {

    private final TransientCraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public ArcaneWorkbenchMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public ArcaneWorkbenchMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.ARCANE_WORKBENCH.get(), containerId);
        this.access = access;
        this.player = inventory.player;

        addSlot(new ResultSlot(player, craftSlots, resultSlots, 0, 124, 35));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(craftSlots, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        access.execute((level, pos) -> refreshResult(level));
    }

    private void refreshResult(Level level) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) return;
        CraftingInput input = craftSlots.asCraftInput();
        ItemStack out = ItemStack.EMPTY;

        Optional<RecipeHolder<IArcaneRecipe>> arcane = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.ARCANE_CRAFTING.get(), input, level);
        if (arcane.isPresent()) {
            // TODO(Phase 6): require a wand with sufficient vis + research known before showing the result
            out = arcane.get().value().assemble(input, level.registryAccess());
        } else {
            Optional<RecipeHolder<CraftingRecipe>> vanilla = level.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, input, level);
            if (vanilla.isPresent() && resultSlots.setRecipeUsed(level, serverPlayer, vanilla.get())) {
                out = vanilla.get().value().assemble(input, level.registryAccess());
            }
        }

        resultSlots.setItem(0, out);
        setRemoteSlot(0, out);
        serverPlayer.connection.send(
                new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, out));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, pos) -> clearContainer(player, craftSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.ARCANE_WORKBENCH.get());
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            moved = current.copy();
            if (index == 0) {
                access.execute((level, pos) -> current.getItem().onCraftedBy(current, level, player));
                if (!moveItemStackTo(current, 10, 46, true)) return ItemStack.EMPTY;
                slot.onQuickCraft(current, moved);
            } else if (index >= 10 && index < 46) {
                if (!moveItemStackTo(current, 1, 10, false)
                        && (index < 37
                                ? !moveItemStackTo(current, 37, 46, false)
                                : !moveItemStackTo(current, 10, 37, false))) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(current, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (current.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (current.getCount() == moved.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, current);
            if (index == 0) player.drop(current, false);
        }
        return moved;
    }
}
