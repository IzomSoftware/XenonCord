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
    public enum PackResponse {
        SUCCESSFULLY_LOADED(0),
        DECLINED(1),
        FAILED_DOWNLOAD(2),
        ACCEPTED(3),
        DOWNLOADED(4),
        INVALID_URL(5),
        FAILED_RELOAD(6),
        DISCARDED(7);
        
        private final int id;
        PackResponse(int id) {
            this.id = id;
        }
        public int getId() {
            return this.id;
        }
    } 
}
