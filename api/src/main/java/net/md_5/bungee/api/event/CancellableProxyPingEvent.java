package net.md_5.bungee.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

/**
 * Called when the proxy is queried for status from the server list.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class CancellableProxyPingEvent extends Event implements Cancellable {

    /**
     * The connection asking for a ping response.
     */
    private final PendingConnection connection;
    /**
     * The data to respond with.
     */
    private boolean cancelled;
    // private boolean ignored;

    public CancellableProxyPingEvent(PendingConnection connection) {
        this.connection = connection;
    }
}
