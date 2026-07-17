package thaumcraft.api.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CardRethink extends TheorycraftCard {

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        int a = 0;
        for (String category : data.categoryTotals.keySet()) {
            a += data.getTotal(category);
        }
        return a >= 10;
    }

    @Override
    public int getInspirationCost() {
        return -1;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.rethink.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.rethink.text");
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        if (!initialize(player, data)) return false;
        int a = 0;
        for (String category : data.categoryTotals.keySet()) {
            a += data.getTotal(category);
        }
        a = Math.min(a, 10);
        int tries = 0;
        while (a > 0 && tries < 1000) {
            tries++;
            for (String category : data.categoryTotals.keySet()) {
                data.addTotal(category, -1);
                a--;
                if (a <= 0 || !data.hasTotal(category)) break;
            }
        }
        data.bonusDraws++;
        data.addTotal("BASICS", Mth.nextInt(player.getRandom(), 1, 10));
        return true;
    }

}
