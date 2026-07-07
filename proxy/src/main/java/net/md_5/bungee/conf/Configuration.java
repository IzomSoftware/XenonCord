
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class Configuration implements ProxyConfig {

    private final Object serversLock = new Object();

    private int timeout = 30000;
    private String uuid = UUID.randomUUID().toString();
    private Collection<ListenerInfo> listeners;
    private Map<String, ServerInfo> servers;
    private boolean onlineMode = false;
    private boolean enforceSecureProfile;
    private boolean ignoreSecureProfileForOlderVersions;
    private int remotePingCache = -1;
    private int playerLimit = -1;
    private int serverConnectTimeout = 5000;
    private int remotePingTimeout = 5000;
    private int throttle = 4000;
    private int throttleLimit = 3;
    private Favicon favicon;
    private int compressionThreshold = 256;
    private boolean preventProxyConnections;
    private boolean rejectTransfers;
    private boolean forgeSupport = true;
    private Collection<String> disabledCommands;

    private String gameVersion;
    private boolean useNettyDnsResolver = true;
    private int tabThrottle = 1000;
    private boolean disableModernTabLimiter = true;
    private boolean disableTabListRewrite = true;
    private ForwardingMode forwardingMode = ForwardingMode.BUNGEECORD_LEGACY;
    private byte[] forwardingSecret = Util.randomAlphanumericSequence(12);
    private int pluginChannelLimit = 128;
    private int pluginChannelNameLimit = 128;

    private int tcpFastOpen = 3;

    public void load() {
        synchronized (serversLock) {
            ConfigurationAdapter adapter = ProxyServer.getInstance().getConfigurationAdapter();
            adapter.load();

            File fav = new File("server-icon.png");
            if (fav.exists()) {
                try {
                    favicon = Favicon.create(ImageIO.read(fav));
                } catch (IOException | IllegalArgumentException ex) {
                    ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not load server icon", ex);
                }
            }

            listeners = adapter.getListeners();
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

            compressionThreshold = adapter.getInt("network_compression_threshold", compressionThreshold);
            preventProxyConnections = adapter.getBoolean("prevent_proxy_connections", preventProxyConnections);
            forgeSupport = adapter.getBoolean("forge_support", forgeSupport);
            rejectTransfers = adapter.getBoolean("reject_transfers", rejectTransfers);
            disabledCommands = new CaseInsensitiveSet(
                    (Collection<String>) adapter.getList("disabled_commands", new ArrayList<>()));

            // Waterfall settings loaded directly from config.yml
            gameVersion = Joiner.on(", ").join(ProtocolConstants.SUPPORTED_VERSIONS);
            useNettyDnsResolver = adapter.getBoolean("use_netty_dns_resolver", useNettyDnsResolver);
            tabThrottle = adapter.getInt("throttling.tab_complete", tabThrottle);
            disableModernTabLimiter = adapter.getBoolean("disable_modern_tab_limiter", disableModernTabLimiter);
            disableTabListRewrite = adapter.getBoolean("disable_tab_list_rewrite", disableTabListRewrite);
            pluginChannelLimit = adapter.getInt("registered_plugin_channels_limit", pluginChannelLimit);
            pluginChannelNameLimit = adapter.getInt("plugin_channel_name_limit", pluginChannelNameLimit);
            tcpFastOpen = adapter.getInt("tcpFastOpen", 3);
            forwardingMode = ForwardingMode
                    .valueOf(adapter.getString("forwarding_mode", forwardingMode.name()).toUpperCase());

            final Logger logger = XenonCore.instance.getLogger();
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
                case NONE:
                    logger.warn("Information forwarding (ip-forwarding) is disabled. " +
                            "Player UUIDs may not be consistent across the servers. " +
                            "For the optimal experience please enable ip_forward in the config.yml and " +
                            "configure forwarding and on your servers.");
                    break;
            }

            forwardingSecret = adapter.getString("forwarding_secret", Arrays.toString(forwardingSecret)).getBytes();

            Preconditions.checkArgument(listeners != null && !listeners.isEmpty(), "No listeners defined.");

            Map<String, ServerInfo> newServers = adapter.getServers();
            Preconditions.checkArgument(newServers != null && !newServers.isEmpty(), "No servers defined");

            if (servers == null) {
                servers = new CaseInsensitiveMap<>(newServers);
            } else {
                Map<String, ServerInfo> oldServers = getServersCopy();

                for (ServerInfo oldServer : oldServers.values()) {
                    ServerInfo newServer = newServers.get(oldServer.getName());
                    if ((newServer == null || !oldServer.getAddress().equals(newServer.getAddress()))
                            && !oldServer.getPlayers().isEmpty()) {
                        BungeeCord.getInstance().getLogger()
                                .info("Moving players off of server: " + oldServer.getName());
                        for (ProxiedPlayer player : oldServer.getPlayers()) {
                            ListenerInfo listener = player.getPendingConnection().getListener();
                            String destinationName = newServers.get(listener.getDefaultServer()) == null
                                    ? listener.getDefaultServer()
                                    : listener.getFallbackServer();
                            ServerInfo destination = newServers.get(destinationName);
                            if (destination == null) {
                                BungeeCord.getInstance().getLogger()
                                        .severe("Couldn't find server " + listener.getDefaultServer() + " or "
                                                + listener.getFallbackServer() + " to put player " + player.getName()
                                                + " on");
                                player.disconnect(BungeeCord.getInstance().getTranslation("fallback_kick",
                                        "Not found on reload"));
                                continue;
                            }
                            player.connect(destination, (success, cause) -> {
                                if (!success) {
                                    BungeeCord.getInstance().getLogger().log(Level.WARNING,
                                            "Failed to connect " + player.getName() + " to " + destination.getName(),
                                            cause);
                                    player.disconnect(BungeeCord.getInstance().getTranslation("fallback_kick",
                                            cause.getCause().getClass().getName()));
                                }
                            });
                        }
                    } else {
                        newServers.put(oldServer.getName(), oldServer);
                    }
                }
                this.servers = new CaseInsensitiveMap<>(newServers);
            }

            for (ListenerInfo listener : listeners) {
                for (int i = 0; i < listener.getServerPriority().size(); i++) {
                    String server = listener.getServerPriority().get(i);
                    Preconditions.checkArgument(servers.containsKey(server), "Server %s (priority %s) is not defined",
                            server, i);
                }
                for (String server : listener.getForcedHosts().values()) {
                    if (!servers.containsKey(server)) {
                        ProxyServer.getInstance().getLogger().log(Level.WARNING,
                                "Forced host server {0} is not defined", server);
                    }
                }
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

    public int getTcpFastOpen() {
        return tcpFastOpen;
    }
    // Waterfall end

    @Override
    public synchronized Map<String, ServerInfo> getServersCopy() {
        return ImmutableMap.copyOf(servers);
    }

    @Override
    public synchronized ServerInfo getServerInfo(String name) {
        return this.servers.get(name);
    }

    @Override
    public synchronized ServerInfo addServer(ServerInfo server) {
        return this.servers.put(server.getName(), server);
    }

    @Override
    public synchronized boolean addServers(Collection<ServerInfo> servers) {
        boolean changed = false;
        for (ServerInfo server : servers) {
            if (server != this.servers.put(server.getName(), server))
                changed = true;
        }
        return changed;
    }

    @Override
    public synchronized ServerInfo removeServerNamed(String name) {
        return this.servers.remove(name);
    }

    @Override
    public synchronized ServerInfo removeServer(ServerInfo server) {
        return this.servers.remove(server.getName());
    }

    @Override
    public synchronized boolean removeServersNamed(Collection<String> names) {
        return this.servers.keySet().removeAll(names);
    }

    @Override
    public synchronized boolean removeServers(Collection<ServerInfo> servers) {
        boolean changed = false;
        for (ServerInfo server : servers) {
            if (null != this.servers.remove(server.getName()))
                changed = true;
        }
        return changed;
    }
}