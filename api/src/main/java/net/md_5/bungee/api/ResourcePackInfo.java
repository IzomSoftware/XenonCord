package net.md_5.bungee.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.UUID;

@Data
@Builder(builderClassName = "Builder", builderMethodName = "builder", toBuilder = true)
@AllArgsConstructor
public class ResourcePackInfo {
    private final String url;
    @Builder.Default
    private final String hash = "";
    @Builder.Default
    private final boolean required = false;
    private final BaseComponent prompt;
    @Builder.Default
    private final UUID id = null;

    @Builder.Default
    private final PackSource packSource = PackSource.PLUGIN;

    public enum PackSource {
        PLUGIN,
        DOWNSTREAM_SERVER,
        UNKNOWN
    }
}
