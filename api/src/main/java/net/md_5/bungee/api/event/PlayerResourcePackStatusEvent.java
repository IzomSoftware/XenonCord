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

    public static final int SUCCESSFULLY_LOADED = 0;
    public static final int DECLINED = 1;
    public static final int FAILED_DOWNLOAD = 2;
    public static final int ACCEPTED = 3;
    public static final int DOWNLOADED = 4;
    public static final int INVALID_URL = 5;
    public static final int FAILED_RELOAD = 6;
    public static final int DISCARDED = 7;

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