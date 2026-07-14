package net.md_5.bungee.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.ResourcePackInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ResourcePackSendEvent extends Event implements Cancellable {

    private final ProxiedPlayer player;
    private ResourcePackInfo resourcePack;
    private boolean cancelled;

    public ResourcePackSendEvent(ProxiedPlayer player, ResourcePackInfo resourcePack) {
        this.player = player;
        this.resourcePack = resourcePack;
    }
}