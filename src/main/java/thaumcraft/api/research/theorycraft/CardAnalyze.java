package thaumcraft.api.research.theorycraft;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;

public class CardAnalyze extends TheorycraftCard {

    String cat = null;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        if (cat != null) nbt.putString("cat", cat);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        cat = nbt.getString("cat");
    }

    @Override
    public String getResearchCategory() {
        return cat;
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(this.getSeed());
        ArrayList<String> cats = new ArrayList<>();
        for (ResearchCategory rc : ResearchCategories.researchCategories.values()) {
            if ("BASICS".equals(rc.key)) continue;
            if (ThaumcraftCapabilities.getKnowledge(player).getKnowledge(
                    EnumKnowledgeType.OBSERVATION, rc) > 0)
                cats.add(rc.key);
        }
        if (cats.size() > 0) {
            cat = cats.get(r.nextInt(cats.size()));
        }
        return cat != null;
    }

    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.analyze.name",
                Component.translatable("tc.research_category." + cat)
                        .withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD));
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.analyze.text",
                Component.translatable("tc.research_category." + cat).withStyle(ChatFormatting.BOLD),
                Component.translatable("tc.research_category.BASICS").withStyle(ChatFormatting.BOLD));
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
        int k = ThaumcraftCapabilities.getKnowledge(player).getKnowledge(EnumKnowledgeType.OBSERVATION, rc);
        if (k >= 1) {
            data.addTotal("BASICS", 5);
            ThaumcraftCapabilities.getKnowledge(player).addKnowledge(
                    EnumKnowledgeType.OBSERVATION, rc, -EnumKnowledgeType.OBSERVATION.getProgression());
            data.addTotal(cat, Mth.nextInt(player.getRandom(), 25, 50));
            return true;
        }
        return false;
    }

}
