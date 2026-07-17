package thaumcraft.api.research;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class ScanningManager {

    static ArrayList<IScanThing> things = new ArrayList<>();

    /**
     * Add things to scan.
     * For example: <i>ScanningManager.addScannableThing(new ScanItem("HIPSTER", new ItemStack(Items.APPLE)));</i><br>
     * This will unlock the <b>HIPSTER</b> research if you scan an apple.
     */
    public static void addScannableThing(IScanThing obj) {
        things.add(obj);
    }

    /**
     * @param object this could in theory be anything, but vanilla tc scanning tools only pass in Entity, BlockPos, Itemstack or null
     */
    public static void scanTheThing(Player player, Object object) {
        boolean found = false;
        boolean suppress = false;
        for (IScanThing thing : things) {
            if (thing.checkThing(player, object)) {
                String key = thing.getResearchKey(player, object);
                if (key == null || key.isEmpty() || ThaumcraftApi.internalMethods.progressResearch(player, key)) {
                    if (key == null || key.isEmpty())
                        suppress = true;
                    found = true;
                    thing.onSuccess(player, object);
                }
            }
        }
        if (!suppress) {
            if (!found) {
                player.displayClientMessage(
                        Component.literal("§5§o").append(Component.translatable("tc.unknownobject")), true);
            } else {
                player.displayClientMessage(
                        Component.literal("§a§o").append(Component.translatable("tc.knownobject")), true);
            }
        }

        // scan contents of inventories
        if (object instanceof BlockPos pos) {
            IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(player.level(), pos, Direction.UP);
            if (handler != null) {
                int scanned = 0;
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    ItemStack stack = handler.getStackInSlot(slot);
                    if (stack != null && !stack.isEmpty()) {
                        scanTheThing(player, stack);
                        scanned++;
                    }
                    if (scanned >= 100) {
                        player.displayClientMessage(
                                Component.literal("§5§o").append(Component.translatable("tc.invtoolarge")), true);
                        break; // to prevent lag with massive inventories
                    }
                }
            }
        }

    }

    /**
     * @return true if the object can be scanned for research the player has not yet discovered
     */
    public static boolean isThingStillScannable(Player player, Object object) {
        for (IScanThing thing : things) {
            if (thing.checkThing(player, object)) {
                try {
                    if (!ThaumcraftCapabilities.knowsResearch(player, thing.getResearchKey(player, object))) {
                        return true;
                    }
                } catch (Exception ignored) {}
            }
        }
        return false;
    }

    public static ItemStack getItemFromParms(Player player, Object obj) {
        ItemStack is = ItemStack.EMPTY;
        if (obj instanceof ItemStack st)
            is = st;
        if (obj instanceof ItemEntity ie && !ie.getItem().isEmpty())
            is = ie.getItem();
        if (obj instanceof BlockPos pos) {
            BlockState state = player.level().getBlockState(pos);
            try {
                is = state.getBlock().getCloneItemStack(player.level(), pos, state);
            } catch (Exception ignored) {}
            try {
                if (is.isEmpty() && state.getFluidState().is(Fluids.WATER)) {
                    is = new ItemStack(Items.WATER_BUCKET);
                }
                if (is.isEmpty() && state.getFluidState().is(Fluids.LAVA)) {
                    is = new ItemStack(Items.LAVA_BUCKET);
                }
            } catch (Exception ignored) {}
        }
        return is;
    }

}
