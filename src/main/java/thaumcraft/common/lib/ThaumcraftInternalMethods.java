package thaumcraft.common.lib;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.internal.DummyInternalMethodHandler;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchEvent;
import thaumcraft.common.init.ModDataMaps;

/**
 * Implementation-side handler behind ThaumcraftApi.internalMethods. Aura and golem seal
 * methods still fall through to the dummy no-ops until those systems land (Phases 5 and 10).
 */
public class ThaumcraftInternalMethods extends DummyInternalMethodHandler {

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        if (is == null || is.isEmpty()) return null;

        if (is.getItem() instanceof IEssentiaContainerItem container && !container.ignoreContainedAspects()) {
            AspectList contained = container.getAspects(is);
            if (contained != null && contained.size() > 0) return contained;
        }

        AspectList override = CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(is));
        if (override != null) return override;

        AspectList mapped = is.getItemHolder().getData(ModDataMaps.ITEM_ASPECTS);
        return mapped != null ? mapped.copy() : null;
    }

    @Override
    public AspectList generateTags(ItemStack is) {
        // TODO(Phase 3): recipe-derived aspect inference at recipe-manager reload.
        return null;
    }

    @Override
    public boolean addKnowledge(Player player, EnumKnowledgeType type, ResearchCategory category, int amount) {
        if (player == null || type == null) return false;
        ResearchEvent.Knowledge event = new ResearchEvent.Knowledge(player, type, category, amount);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        boolean added = knowledge.addKnowledge(type, category, amount);
        if (added && player instanceof ServerPlayer sp) knowledge.sync(sp);
        return added;
    }

    @Override
    public boolean progressResearch(Player player, String researchkey) {
        if (player == null || researchkey == null || researchkey.isEmpty()) return false;
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);

        ResearchEntry entry = ResearchCategories.getResearch(researchkey);
        int stage = knowledge.getResearchStage(researchkey);
        if (stage < 0) {
            ResearchEvent.Research event = new ResearchEvent.Research(player, researchkey);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) return false;
            knowledge.addResearch(researchkey);
        } else if (entry != null && entry.getStages() != null && stage <= entry.getStages().length) {
            knowledge.setResearchStage(researchkey, stage + 1);
        } else {
            return false; // already complete / nothing to progress
        }

        if (entry != null && knowledge.isResearchComplete(researchkey) && entry.getSiblings() != null) {
            for (String sibling : entry.getSiblings()) {
                completeResearch(player, sibling);
            }
        }
        if (player instanceof ServerPlayer sp) knowledge.sync(sp);
        return true;
    }

    @Override
    public boolean completeResearch(Player player, String researchkey) {
        if (player == null || researchkey == null || researchkey.isEmpty()) return false;
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge.isResearchComplete(researchkey)) return false;

        ResearchEvent.Research event = new ResearchEvent.Research(player, researchkey);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;

        knowledge.addResearch(researchkey);
        ResearchEntry entry = ResearchCategories.getResearch(researchkey);
        if (entry != null && entry.getStages() != null && entry.getStages().length > 0) {
            knowledge.setResearchStage(researchkey, entry.getStages().length + 1);
        }
        if (entry != null && entry.getSiblings() != null) {
            for (String sibling : entry.getSiblings()) {
                completeResearch(player, sibling);
            }
        }
        if (player instanceof ServerPlayer sp) knowledge.sync(sp);
        return true;
    }

    @Override
    public boolean doesPlayerHaveRequisites(Player player, String researchkey) {
        // TODO(Phase 8): check stage knowledge/item/craft requirements from the research JSON.
        return true;
    }

    @Override
    public void addWarpToPlayer(Player player, int amount, EnumWarpType type) {
        if (player == null || type == null) return;
        IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
        warp.add(type, amount);
        if (player instanceof ServerPlayer sp) warp.sync(sp);
    }

    @Override
    public int getActualWarp(Player player) {
        IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
        return warp.get(EnumWarpType.PERMANENT) + warp.get(EnumWarpType.NORMAL);
    }
}
