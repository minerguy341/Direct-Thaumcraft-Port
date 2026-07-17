package thaumcraft.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import thaumcraft.api.crafting.IDustTrigger;

public class SalisMundusItem extends Item {

    public SalisMundusItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        for (IDustTrigger trigger : IDustTrigger.triggers) {
            IDustTrigger.Placement placement =
                    trigger.getValidFace(context.getLevel(), player, context.getClickedPos(), context.getClickedFace());
            if (placement == null) continue;

            if (!context.getLevel().isClientSide) {
                trigger.execute(context.getLevel(), player, context.getClickedPos(), placement, context.getClickedFace());
                if (context.getLevel() instanceof ServerLevel serverLevel) {
                    for (BlockPos sparkle : trigger.sparkle(context.getLevel(), player, context.getClickedPos(), placement)) {
                        serverLevel.sendParticles(ParticleTypes.ENCHANT,
                                sparkle.getX() + 0.5, sparkle.getY() + 0.5, sparkle.getZ() + 0.5,
                                20, 0.4, 0.4, 0.4, 0.1);
                    }
                }
                if (!player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        return InteractionResult.PASS;
    }
}
