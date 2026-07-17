package thaumcraft.common.capabilities;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchCategories;

public class PlayerKnowledge implements IPlayerKnowledge {

    private final Map<String, Integer> research = new HashMap<>();
    private final Map<String, EnumSet<EnumResearchFlag>> flags = new HashMap<>();
    private final Map<String, Integer> knowledge = new HashMap<>();

    @Override
    public void clear() {
        research.clear();
        flags.clear();
        knowledge.clear();
    }

    @Override
    public EnumResearchStatus getResearchStatus(String res) {
        if (!research.containsKey(res)) return EnumResearchStatus.UNKNOWN;
        return isResearchComplete(res) ? EnumResearchStatus.COMPLETE : EnumResearchStatus.IN_PROGRESS;
    }

    @Override
    public boolean isResearchComplete(String res) {
        Integer stage = research.get(res);
        if (stage == null) return false;
        ResearchEntry entry = ResearchCategories.getResearch(res);
        if (entry == null || entry.getStages() == null || entry.getStages().length == 0) return true;
        return stage > entry.getStages().length;
    }

    @Override
    public boolean isResearchKnown(String res) {
        if (res == null || res.isEmpty()) return true;
        if (res.contains("@")) {
            String[] parts = res.split("@");
            int needed;
            try {
                needed = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                return false;
            }
            return getResearchStage(parts[0]) >= needed;
        }
        return research.containsKey(res);
    }

    @Override
    public int getResearchStage(String res) {
        Integer stage = research.get(res);
        if (stage == null) return -1;
        ResearchEntry entry = ResearchCategories.getResearch(res);
        if (entry == null || entry.getStages() == null || entry.getStages().length == 0) return 0;
        return stage;
    }

    @Override
    public boolean addResearch(String res) {
        if (research.containsKey(res)) return false;
        research.put(res, 1);
        return true;
    }

    @Override
    public boolean setResearchStage(String res, int stage) {
        if (!research.containsKey(res) || stage < 1) return false;
        research.put(res, stage);
        return true;
    }

    @Override
    public boolean removeResearch(String res) {
        if (!research.containsKey(res)) return false;
        research.remove(res);
        flags.remove(res);
        return true;
    }

    @Override
    public Set<String> getResearchList() {
        return Collections.unmodifiableSet(research.keySet());
    }

    @Override
    public boolean setResearchFlag(String res, EnumResearchFlag flag) {
        return flags.computeIfAbsent(res, k -> EnumSet.noneOf(EnumResearchFlag.class)).add(flag);
    }

    @Override
    public boolean clearResearchFlag(String res, EnumResearchFlag flag) {
        EnumSet<EnumResearchFlag> set = flags.get(res);
        return set != null && set.remove(flag);
    }

    @Override
    public boolean hasResearchFlag(String res, EnumResearchFlag flag) {
        EnumSet<EnumResearchFlag> set = flags.get(res);
        return set != null && set.contains(flag);
    }

    private static String knowledgeKey(EnumKnowledgeType type, ResearchCategory category) {
        return type.getAbbreviation() + (category == null ? "" : "@" + category.key);
    }

    @Override
    public boolean addKnowledge(EnumKnowledgeType type, ResearchCategory category, int amount) {
        String key = knowledgeKey(type, category);
        int current = knowledge.getOrDefault(key, 0) + amount;
        if (current < 0) return false;
        knowledge.put(key, current);
        return true;
    }

    @Override
    public int getKnowledge(EnumKnowledgeType type, ResearchCategory category) {
        return getKnowledgeRaw(type, category) / type.getProgression();
    }

    @Override
    public int getKnowledgeRaw(EnumKnowledgeType type, ResearchCategory category) {
        return knowledge.getOrDefault(knowledgeKey(type, category), 0);
    }

    @Override
    public void sync(ServerPlayer player) {
        // TODO(networking phase): delta-sync knowledge + research to the client.
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag researchList = new ListTag();
        for (Map.Entry<String, Integer> e : research.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putString("key", e.getKey());
            t.putInt("stage", e.getValue());
            EnumSet<EnumResearchFlag> set = flags.get(e.getKey());
            if (set != null && !set.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (EnumResearchFlag f : set) sb.append(f.name()).append(",");
                t.putString("flags", sb.toString());
            }
            researchList.add(t);
        }
        nbt.put("research", researchList);

        ListTag knowledgeList = new ListTag();
        for (Map.Entry<String, Integer> e : knowledge.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putString("key", e.getKey());
            t.putInt("amount", e.getValue());
            knowledgeList.add(t);
        }
        nbt.put("knowledge", knowledgeList);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        clear();
        ListTag researchList = nbt.getList("research", Tag.TAG_COMPOUND);
        for (int i = 0; i < researchList.size(); i++) {
            CompoundTag t = researchList.getCompound(i);
            String key = t.getString("key");
            research.put(key, t.getInt("stage"));
            if (t.contains("flags")) {
                EnumSet<EnumResearchFlag> set = EnumSet.noneOf(EnumResearchFlag.class);
                for (String f : t.getString("flags").split(",")) {
                    if (!f.isEmpty()) {
                        try {
                            set.add(EnumResearchFlag.valueOf(f));
                        } catch (Exception ignored) {}
                    }
                }
                if (!set.isEmpty()) flags.put(key, set);
            }
        }
        ListTag knowledgeList = nbt.getList("knowledge", Tag.TAG_COMPOUND);
        for (int i = 0; i < knowledgeList.size(); i++) {
            CompoundTag t = knowledgeList.getCompound(i);
            knowledge.put(t.getString("key"), t.getInt("amount"));
        }
    }
}
