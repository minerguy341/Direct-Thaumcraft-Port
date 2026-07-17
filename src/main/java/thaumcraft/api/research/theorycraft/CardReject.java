package thaumcraft.api.research.theorycraft;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CardReject extends TheorycraftCard {

    private String cat1;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        if (cat1 != null) nbt.putString("cat", cat1);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        cat1 = nbt.getString("cat");
    }

    @Override
    public int getInspirationCost() {
        return 0;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.reject.name",
                Component.translatable("tc.research_category." + cat1)
                        .withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD));
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.reject.text",
                Component.translatable("tc.research_category." + cat1).withStyle(ChatFormatting.BOLD));
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        ArrayList<String> s = new ArrayList<>();
        for (String c : data.categoryTotals.keySet()) {
            if (!data.categoriesBlocked.contains(c))
                s.add(c);
        }
        if (s.size() < 1) return false;
        Random r = new Random(this.getSeed());
        cat1 = s.get(r.nextInt(s.size()));
        return cat1 != null;
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        if (cat1 == null) return false;
        data.addTotal("BASICS", 5);
        data.categoriesBlocked.add(cat1);
        return true;
    }

}
