package net.md_5.bungee.conf;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.github.waterfallmc.waterfall.forwarding.ForwardingMode;
import ir.xenoncommunity.XenonCore;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.util.CaseInsensitiveMap;
import net.md_5.bungee.util.CaseInsensitiveSet;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Core configuration for the proxy.
 */
@Getter
public class Configuration implements ProxyConfig {

    // Base configuration defaults
    private static final int DEFAULT_TIMEOUT = 30000;
    private static final int DEFAULT_SERVER_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_REMOTE_PING_TIMEOUT = 5000;
    private static final int DEFAULT_THROTTLE = 4000;
    private static final int DEFAULT_THROTTLE_LIMIT = 3;
    private static final int DEFAULT_COMPRESSION_THRESHOLD = 256;

    // Extended configuration defaults
    private static final int DEFAULT_TAB_THROTTLE = 1000;
    private static final int DEFAULT_PLUGIN_CHANNEL_LIMIT = 128;
    private static final int DEFAULT_PLUGIN_CHANNEL_NAME_LIMIT = 128;
    private static final String DEFAULT_FORWARDING_MODE = "BUNGEECORD_LEGACY";

    private final Object serversLock = new Object();

    // Base configuration fields
    private int timeout = DEFAULT_TIMEOUT;
    private String uuid = UUID.randomUUID().toString();
    private Collection<ListenerInfo> listeners;
    private Map<String, ServerInfo> servers;
    private boolean onlineMode;
    private boolean enforceSecureProfile;
    private boolean ignoreSecureProfileForOlderVersions;
    private int remotePingCache = -1;
    private int playerLimit = -1;
    private int serverConnectTimeout = DEFAULT_SERVER_CONNECT_TIMEOUT;
    private int remotePingTimeout = DEFAULT_REMOTE_PING_TIMEOUT;
    private int throttle = DEFAULT_THROTTLE;
    private int throttleLimit = DEFAULT_THROTTLE_LIMIT;
    private boolean ipForward;
    private Favicon favicon;
    private int compressionThreshold = DEFAULT_COMPRESSION_THRESHOLD;
    private boolean preventProxyConnections;
    private boolean rejectTransfers;
    private boolean forgeSupport = true;
    private Collection<String> disabledCommands;

    // Extended configuration fields
    private String gameVersion;
    private boolean useNettyDnsResolver = true;

    // Throttling options
    private int tabThrottle = DEFAULT_TAB_THROTTLE;
    private boolean disableModernTabLimiter = true;
    private boolean disableTabListRewrite = true;

    // Forwarding options
    private ForwardingMode forwardingMode = ForwardingMode.BUNGEECORD_LEGACY;
    private byte[] forwardingSecret = Util.randomAlphanumericSequence(12);

    // Plugin message options
    private int pluginChannelLimit = DEFAULT_PLUGIN_CHANNEL_LIMIT;
    private int pluginChannelNameLimit = DEFAULT_PLUGIN_CHANNEL_NAME_LIMIT;

    public void load() {
        synchronized (serversLock) {
            ConfigurationAdapter adapter = ProxyServer.getInstance().getConfigurationAdapter();
            adapter.load();

            loadFavicon();
            loadSettings(adapter);
            loadGameVersion();
            loadForwardingSettings(adapter);

            listeners = adapter.getListeners();
            loadServers(adapter);
            validateListeners();
        }
    }

    private void loadFavicon() {
        File fav = new File("server-icon.png");
        if (!fav.exists()) {
            return;
        }
        try {
            favicon = Favicon.create(ImageIO.read(fav));
        } catch (IOException | IllegalArgumentException ex) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not load server icon", ex);
        }
    }

    private void loadSettings(ConfigurationAdapter adapter) {
        // Base settings
        timeout = adapter.getInt("timeout", timeout);
        uuid = adapter.getString("stats", uuid);
        onlineMode = adapter.getBoolean("online_mode", onlineMode);
        enforceSecureProfile = adapter.getBoolean("enforce_secure_profile", enforceSecureProfile);
        ignoreSecureProfileForOlderVersions = adapter.getBoolean("ignore_secure_profile_for_older_versions",
                ignoreSecureProfileForOlderVersions);
        remotePingCache = adapter.getInt("remote_ping_cache", remotePingCache);
        playerLimit = adapter.getInt("player_limit", playerLimit);
        serverConnectTimeout = adapter.getInt("server_connect_timeout", serverConnectTimeout);
        remotePingTimeout = adapter.getInt("remote_ping_timeout", remotePingTimeout);
        throttle = adapter.getInt("connection_throttle", throttle);
        throttleLimit = adapter.getInt("connection_throttle_limit", throttleLimit);
        ipForward = adapter.getBoolean("ip_forward", ipForward);
        compressionThreshold = adapter.getInt("network_compression_threshold", compressionThreshold);
        preventProxyConnections = adapter.getBoolean("prevent_proxy_connections", preventProxyConnections);
        forgeSupport = adapter.getBoolean("forge_support", forgeSupport);
        rejectTransfers = adapter.getBoolean("reject_transfers", rejectTransfers);
        disabledCommands = new CaseInsensitiveSet(
                (Collection<String>) adapter.getList("disabled_commands", new ArrayList<>()));

        // Extended network and throttling settings
        useNettyDnsResolver = adapter.getBoolean("use_netty_dns_resolver", useNettyDnsResolver);
        tabThrottle = adapter.getInt("throttling.tab_complete", tabThrottle);
        disableModernTabLimiter = adapter.getBoolean("disable_modern_tab_limiter", disableModernTabLimiter);
        disableTabListRewrite = adapter.getBoolean("disable_tab_list_rewrite", disableTabListRewrite);
        pluginChannelLimit = adapter.getInt("registered_plugin_channels_limit", pluginChannelLimit);
        pluginChannelNameLimit = adapter.getInt("plugin_channel_name_limit", pluginChannelNameLimit);
    }

    private void loadGameVersion() {
        gameVersion = Joiner.on(", ").join(ProtocolConstants.SUPPORTED_VERSIONS);
    }

    private void loadForwardingSettings(ConfigurationAdapter adapter) {
        String modeString = adapter.getString("forwarding_mode", DEFAULT_FORWARDING_MODE).toUpperCase();
        forwardingMode = ForwardingMode.valueOf(modeString);

        String secret = adapter.getString("forwarding_secret", "");
        if (secret.isEmpty()) {
            XenonCore.instance.getLogger()
                    .warn("No 'forwarding_secret' is defined in config.yml. A random secret has been generated. " +
                            "Please set 'forwarding_secret' in config.yml to ensure player connections remain stable across restarts.");
            forwardingSecret = Util.randomAlphanumericSequence(12);
        } else {
            forwardingSecret = secret.getBytes(StandardCharsets.UTF_8);
        }

        logForwardingWarnings();
    }

    private void logForwardingWarnings() {
        Logger logger = XenonCore.instance.getLogger();

        if (ipForward) {
            switch (forwardingMode) {
                case BUNGEECORD_LEGACY:
                    logger.info("Forwarding mode is set to Bungeecord/Legacy forwarding. " +
                            "It is recommended to use another forwarding method to mitigate information spoofing attacks.");
                    break;
                case BUNGEEGUARD:
                    logger.info("Forwarding mode is set to BungeeGuard forwarding. " +
                            "Please ensure all connected servers make use of BungeeGuard for optimal security.");
                    break;
                case VELOCITY_MODERN:
                    logger.info("Forwarding mode is set to modern/Velocity forwarding. " +
                            "If you need to use versions older than 1.13 please use another forwarding type.");
                    break;
            }
        } else {
            logger.warn("Information forwarding (ip-forwarding) is disabled. " +
                    "Player UUIDs may not be consistent across the servers. " +
                    "For the optimal experience please enable ip_forward in the config.yml and " +
                    "configure forwarding on your backend servers.");
        }
    }

    private void loadServers(ConfigurationAdapter adapter) {
        Map<String, ServerInfo> newServers = adapter.getServers();
        Preconditions.checkArgument(newServers != null && !newServers.isEmpty(), "No servers defined");

        if (servers == null) {
            servers = new CaseInsensitiveMap<>(newServers);
            return;
        }

        migratePlayersIfNeeded(newServers);
        this.servers = new CaseInsensitiveMap<>(newServers);
    }

    private void migratePlayersIfNeeded(Map<String, ServerInfo> newServers) {
        Map<String, ServerInfo> oldServers = getServersCopy();

        for (ServerInfo oldServer : oldServers.values()) {
            ServerInfo newServer = newServers.get(oldServer.getName());

            if (isServerChanged(oldServer, newServer) && !oldServer.getPlayers().isEmpty()) {
                migratePlayersOffServer(oldServer, newServers);
            } else if (newServer != null) {
                newServers.put(oldServer.getName(), oldServer);
            }
        }
    }

    private boolean isServerChanged(ServerInfo oldServer, ServerInfo newServer) {
        return newServer == null || !oldServer.getAddress().equals(newServer.getAddress());
    }

    private void migratePlayersOffServer(ServerInfo oldServer, Map<String, ServerInfo> newServers) {
        BungeeCord.getInstance().getLogger().info("Moving players off of server: " + oldServer.getName());

        for (ProxiedPlayer player : oldServer.getPlayers()) {
            ServerInfo destination = findMigrationDestination(player, newServers);

            if (destination == null) {
                kickPlayerWithNoDestination(player);
                continue;
            }

            connectPlayerToDestination(player, destination);
        }
    }

    private ServerInfo findMigrationDestination(ProxiedPlayer player, Map<String, ServerInfo> newServers) {
        ListenerInfo listener = player.getPendingConnection().getListener();

        ServerInfo defaultServer = newServers.get(listener.getDefaultServer());
        if (defaultServer != null) {
            return defaultServer;
        }
        return newServers.get(listener.getFallbackServer());
    }

    private void kickPlayerWithNoDestination(ProxiedPlayer player) {
        ListenerInfo listener = player.getPendingConnection().getListener();
        String serverNames = listener.getDefaultServer() + " or " + listener.getFallbackServer();

        BungeeCord.getInstance().getLogger().severe(
                "Couldn't find server " + serverNames + " to put player " + player.getName() + " on");
        player.disconnect(BungeeCord.getInstance().getTranslation("fallback_kick", "Not found on reload"));
    }

    private void connectPlayerToDestination(ProxiedPlayer player, ServerInfo destination) {
        player.connect(destination, (success, cause) -> {
            if (!success) {
                BungeeCord.getInstance().getLogger().log(
                        Level.WARNING,
                        "Failed to connect " + player.getName() + " to " + destination.getName(),
                        cause);
                player.disconnect(BungeeCord.getInstance().getTranslation(
                        "fallback_kick", cause.getCause().getClass().getName()));
            }
        });
    }

    private void validateListeners() {
        Preconditions.checkArgument(listeners != null && !listeners.isEmpty(), "No listeners defined.");

        for (ListenerInfo listener : listeners) {
            validateServerPriority(listener);
            validateForcedHosts(listener);
        }
    }

    private void validateServerPriority(ListenerInfo listener) {
        List<String> priority = listener.getServerPriority();
        for (int i = 0; i < priority.size(); i++) {
            String server = priority.get(i);
            Preconditions.checkArgument(
                    servers.containsKey(server),
                    "Server %s (priority %s) is not defined",
                    server, i);
        }
    }

    private void validateForcedHosts(ListenerInfo listener) {
        for (String server : listener.getForcedHosts().values()) {
            if (!servers.containsKey(server)) {
                ProxyServer.getInstance().getLogger().log(
                        Level.WARNING, "Forced host server {0} is not defined", server);
            }
        }
    }

    @Override
    @Deprecated
    public String getFavicon() {
        return getFaviconObject().getEncoded();
    }

    @Override
    public Favicon getFaviconObject() {
        return favicon;
    }

    // Interface-required getters (Lombok @Getter doesn't generate @Override
    // methods)

    @Override
    public String getGameVersion() {
        return gameVersion;
    }

    @Override
    public boolean isUseNettyDnsResolver() {
        return useNettyDnsResolver;
    }

    @Override
    public int getTabThrottle() {
        return tabThrottle;
    }

    @Override
    public boolean isDisableModernTabLimiter() {
        return disableModernTabLimiter;
    }

    @Override
    public boolean isDisableTabListRewrite() {
        return disableTabListRewrite;
    }

    @Override
    public ForwardingMode getForwardingMode() {
        return forwardingMode;
    }

    @Override
    public int getPluginChannelLimit() {
        return pluginChannelLimit;
    }

    @Override
    public int getPluginChannelNameLimit() {
        return pluginChannelNameLimit;
    }

    // Server management methods

    @Override
    public Map<String, ServerInfo> getServersCopy() {
        synchronized (serversLock) {
            return ImmutableMap.copyOf(servers);
        }
    }

    @Override
    public ServerInfo getServerInfo(String name) {
        synchronized (serversLock) {
            return servers.get(name);
        }
    }

    @Override
    public ServerInfo addServer(ServerInfo server) {
        synchronized (serversLock) {
            return servers.put(server.getName(), server);
        }
    }

    @Override
    public boolean addServers(Collection<ServerInfo> servers) {
        synchronized (serversLock) {
            boolean changed = false;
            for (ServerInfo server : servers) {
                if (server != this.servers.put(server.getName(), server)) {
                    changed = true;
                }
            }
            return changed;
        }
    }

    @Override
    public ServerInfo removeServerNamed(String name) {
        synchronized (serversLock) {
            return servers.remove(name);
        }
    }

    @Override
    public ServerInfo removeServer(ServerInfo server) {
        synchronized (serversLock) {
            return servers.remove(server.getName());
        }
    }

    @Override
    public boolean removeServersNamed(Collection<String> names) {
        synchronized (serversLock) {
            return servers.keySet().removeAll(names);
        }
    }

    @Override
    public boolean removeServers(Collection<ServerInfo> servers) {
        synchronized (serversLock) {
            boolean changed = false;
            for (ServerInfo server : servers) {
                if (this.servers.remove(server.getName()) != null) {
                    changed = true;
                }
            }
            return changed;
        }
    }
}