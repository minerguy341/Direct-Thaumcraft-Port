package thaumcraft.common.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.api.research.ScanningManager;

public class ThaumometerItem extends Item {

    public ThaumometerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            HitResult hit = player.pick(8.0D, 0.0F, true);
            Object target = hit instanceof BlockHitResult bhr && hit.getType() == HitResult.Type.BLOCK
                    ? bhr.getBlockPos() : null;
            ScanningManager.scanTheThing(player, target);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide) {
            ScanningManager.scanTheThing(player, target);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

}
