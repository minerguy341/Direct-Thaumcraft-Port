package thaumcraft.api.capabilities;

import java.util.function.Supplier;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

/**
 * @author Azanor
 *
 * Player knowledge and warp are stored as NeoForge data attachments in this port. The attachment
 * types are registered by the Thaumcraft implementation during mod construction and injected into
 * the suppliers below - addons should only use the accessor methods.
 */
public class ThaumcraftCapabilities {

    /**
     * Set by the Thaumcraft implementation at mod construction. Do not assign your own.
     */
    public static Supplier<AttachmentType<IPlayerKnowledge>> KNOWLEDGE;
    public static Supplier<AttachmentType<IPlayerWarp>> WARP;

    /**
     * Retrieves the knowledge data for the supplied player
     */
    public static IPlayerKnowledge getKnowledge(Player player) {
        return player.getData(KNOWLEDGE.get());
    }

    /**
     * Retrieves the warp data for the supplied player
     */
    public static IPlayerWarp getWarp(Player player) {
        return player.getData(WARP.get());
    }

    /**
     * Shortcut method to check if player knows the passed research entries. All must be true
     * Research does not need to be complete, just 'in progress'
     * Individual entries can also contain && to do a 'and' check, e.g. "basicgolemancy&&infusion"
     * Handy for recipes where multiple researches need to be true to be craftable
     * Individual entries can also contain || to do an 'or' check, e.g. "basicgolemancy||infusion"
     * Queries should NOT contain both && and || - shennanigans will occur.
     */
    public static boolean knowsResearch(Player player, String... research) {
        for (String r : research) {
            if (r.contains("&&")) {
                String[] rr = r.split("&&");
                if (!knowsResearch(player, rr)) return false;
            } else if (r.contains("||")) {
                String[] rr = r.split("\\|\\|");
                for (String str : rr)
                    if (knowsResearch(player, str)) return true;
                return false;
            } else if (!getKnowledge(player).isResearchKnown(r)) return false;
        }
        return true;
    }

    /**
     * Shortcut method to check if player knows all the passed research entries.
     * Research needs to be complete and 'in progress' research will only count if a stage is passed in the research parameter (using @, eg. "FOCUSFIRE@2")
     * Individual entries can also contain && to do a 'and' check, e.g. "basicgolemancy&&infusion"
     * Handy for recipes where multiple researches need to be true to be craftable
     * Individual entries can also contain || to do an 'or' check, e.g. "basicgolemancy||infusion"
     * Queries should NOT contain both && and || - shennanigans will occur.
     */
    public static boolean knowsResearchStrict(Player player, String... research) {
        for (String r : research) {
            if (r.contains("&&")) {
                String[] rr = r.split("&&");
                if (!knowsResearchStrict(player, rr)) return false;
            } else if (r.contains("||")) {
                String[] rr = r.split("\\|\\|");
                for (String str : rr)
                    if (knowsResearchStrict(player, str)) return true;
                return false;
            } else if (r.contains("@")) {
                if (!getKnowledge(player).isResearchKnown(r)) return false;
            } else {
                if (!getKnowledge(player).isResearchComplete(r)) return false;
            }
        }
        return true;
    }

}
