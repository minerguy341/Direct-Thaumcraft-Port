package thaumcraft.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.research.ResearchCategory;

/**
 * @author Azanor
 */
public interface IInternalMethodHandler {

    /**
     * Add raw knowledge points (not whole knowledges) to the given player.
     * This method will trigger appropriate gui notifications, etc.
     * @return if the knowledge was added
     */
    boolean addKnowledge(Player player, EnumKnowledgeType type, ResearchCategory category, int amount);

    /**
     * Progresses research with all the proper bells and whistles (popups, sounds, warp, etc)
     * If the research is linked to a research entry with stages the player's current stage will be increased
     * by 1, or set to 1 if the research was not known before.
     * @return if operation succeeded
     */
    boolean progressResearch(Player player, String researchkey);

    /**
     * Completes research with all the proper bells and whistles (popups, sounds, warp, etc)
     * This automatically sets all its stages as complete.
     * Most of the time you should probably use progressResearch instead.
     * @return if operation succeeded
     */
    boolean completeResearch(Player player, String researchkey);

    /**
     * @param researchkey the key of the research you want to check
     * @return does the player have all the required knowledge to complete the passed researchkey
     */
    boolean doesPlayerHaveRequisites(Player player, String researchkey);

    /**
     * Adds warp with all the proper bells and whistles (text, sounds, etc)
     */
    void addWarpToPlayer(Player player, int amount, EnumWarpType type);

    /**
     * The total of the players normal + permanent warp. NOT temporary warp.
     */
    int getActualWarp(Player player);

    AspectList getObjectAspects(ItemStack is);

    AspectList generateTags(ItemStack is);

    float drainVis(Level world, BlockPos pos, float amount, boolean simulate);

    float drainFlux(Level world, BlockPos pos, float amount, boolean simulate);

    void addVis(Level world, BlockPos pos, float amount);

    void addFlux(Level world, BlockPos pos, float amount, boolean showEffect);

    /**
     * returns the aura and flux in a chunk added together
     */
    float getTotalAura(Level world, BlockPos pos);

    float getVis(Level world, BlockPos pos);

    float getFlux(Level world, BlockPos pos);

    int getAuraBase(Level world, BlockPos pos);

    void registerSeal(ISeal seal);

    ISeal getSeal(String key);

    ISealEntity getSealEntity(ResourceKey<Level> dim, SealPos pos);

    void addGolemTask(ResourceKey<Level> dim, Task task);

    boolean shouldPreserveAura(Level world, Player player, BlockPos pos);

    ItemStack getSealStack(String key);

}
