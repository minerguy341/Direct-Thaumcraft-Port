package thaumcraft.common.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thaumcraft.Thaumcraft;
import thaumcraft.common.capabilities.PlayerKnowledge;
import thaumcraft.common.capabilities.PlayerWarp;

public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Thaumcraft.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerKnowledge>> KNOWLEDGE =
            ATTACHMENTS.register("knowledge",
                    () -> AttachmentType.serializable(PlayerKnowledge::new).copyOnDeath().build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerWarp>> WARP =
            ATTACHMENTS.register("warp",
                    () -> AttachmentType.serializable(PlayerWarp::new).copyOnDeath().build());

    private ModAttachments() {
    }
}
