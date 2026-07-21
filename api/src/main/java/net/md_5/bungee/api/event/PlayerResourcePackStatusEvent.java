package net.md_5.bungee.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class PlayerResourcePackStatusEvent extends Event {
    private final ProxiedPlayer player;
    private final UUID packId;
    private final String hash;
    private final int status;

    public PlayerResourcePackStatusEvent(ProxiedPlayer player, UUID packId, String hash, int status) {
        this.player = player;
        this.packId = packId;
        this.hash = hash;
        this.status = status;
    }
}
