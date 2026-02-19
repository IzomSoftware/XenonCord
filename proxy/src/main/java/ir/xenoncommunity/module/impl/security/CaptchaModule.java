package ir.xenoncommunity.module.impl.security;

import ir.xenoncommunity.XenonCore;
import ir.xenoncommunity.module.ModuleBase;
import ir.xenoncommunity.annotations.ModuleInfo;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.MapData;
import ir.xenoncommunity.utils.Configuration;
import ir.xenoncommunity.utils.MapPalette;
import ir.xenoncommunity.utils.WhitelistUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import java.util.concurrent.ScheduledFuture;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientChat;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.PlayerPositionAndLook;
import net.md_5.bungee.protocol.ProtocolConstants;
import io.netty.channel.*;
import java.lang.reflect.Method;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@ModuleInfo(name = "Captcha", version = 1.0, description = "Map-based captcha with isolation")
public class CaptchaModule extends ModuleBase implements Listener {

    private final Map<UUID, CaptchaSession> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, Long> verifiedPlayers = new ConcurrentHashMap<>();
    private final File verifiedFile = new File("XenonCord/verified_players.csv");
    private final File oldVerifiedFile = new File("verified_players.csv");
    private static final ThreadLocal<Boolean> BYPASS = ThreadLocal.withInitial(() -> false);

    @Override
    public void onInit() {
        if (!getConfig().getModules().getCaptcha_module().isEnabled()) return;
        loadVerifiedPlayers();
        XenonCore.instance.getBungeeInstance().getPluginManager().registerListener(null, this);
        getTaskManager().repeatingTask(this::cleanupSessions, 1, 1, TimeUnit.MINUTES);
    }

    @EventHandler(priority = -64)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (WhitelistUtils.isWhitelisted(player.getAddress().getAddress().getHostAddress(), player.getName())) return;
        if (isVerified(player.getUniqueId())) return;

        sessions.put(player.getUniqueId(), new CaptchaSession(player));
        injectNetty(player);
        showCaptcha(player);
        
    }

    @EventHandler(priority = -64)
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CaptchaSession session = sessions.get(player.getUniqueId());
        
        if (session != null && !session.verified) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = -64)
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        CaptchaSession session = sessions.get(player.getUniqueId());
        
        if (session != null) {
            event.setCancelled(true);
            if (session.state == State.CAPTCHA) {
                String message = event.getMessage().trim();
                if (message.equalsIgnoreCase(session.code)) {
                    handleSuccess(player);
                } else {
                    handleFailure(player);
                }
            } else {
                player.sendMessage(ChatColor.RED + "Please wait... verifying connection.");
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        cleanupSession(event.getPlayer().getUniqueId());
    }

    private void cleanupSession(UUID uuid) {
        CaptchaSession session = sessions.remove(uuid);
        if (session != null && session.task != null) {
            session.task.cancel(true);
        }
    }

    private void showCaptcha(ProxiedPlayer player) {
        CaptchaSession session = sessions.get(player.getUniqueId());
        if (session == null) return;

        session.state = State.CAPTCHA;
        session.code = generateCode();
        byte[] mapData = generateMapData(session.code);

        Login login = new Login();
        login.setEntityId(-1);
        login.setHardcore(false);
        login.setGameMode((short) 2);
        login.setPreviousGameMode((short) -1);
        login.setWorldNames(Collections.singleton("minecraft:the_nether"));
        
        int version = player.getPendingConnection().getVersion();
        if (version >= ProtocolConstants.MINECRAFT_1_16) {
            login.setDimension("minecraft:the_nether");
        } else {
            login.setDimension(-1);
        }

        login.setWorldName("minecraft:the_nether");
        login.setDifficulty((short) 1);
        login.setMaxPlayers(1);
        login.setLevelType("default");
        login.setViewDistance(2);
        login.setSimulationDistance(2);
        
        BYPASS.set(true);
        try {
            player.unsafe().sendPacket(login);
        } finally {
            BYPASS.set(false);
        }

        sendPacket(player, new PlayerPositionAndLook(0, 64, 0, 0f, 0f, (byte) 0, 0, false));

        MapData map = new MapData();
        map.setMapId(0);
        map.setScale((byte) 4);
        map.setTrackingPosition(false);
        map.setLocked(true);
        map.setColumns((byte) 128);
        map.setRows((byte) 128);
        map.setX((byte) 0);
        map.setZ((byte) 0);
        map.setData(mapData);

        sendPacket(player, map);

        net.md_5.bungee.protocol.packet.Item item = new net.md_5.bungee.protocol.packet.Item();
        item.setId(358);
        item.setCount(1);
        item.setData(0); 
        
        sendPacket(player, new net.md_5.bungee.protocol.packet.SetSlot(0, 36, item));
        
        sendMessage(player, ChatColor.translateAlternateColorCodes('&', 
            getConfig().getModules().getCaptcha_module().getMessages().getInstructions()));

        session.task = getTaskManager().repeatingTask(() -> {
            if (player.isConnected()) {
                sendPacket(player, new KeepAlive(System.currentTimeMillis()));
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void sendMessage(ProxiedPlayer player, String message) {
        BYPASS.set(true);
        try {
            player.sendMessage(message);
        } finally {
            BYPASS.set(false);
        }
    }

    private void sendPacket(ProxiedPlayer player, DefinedPacket packet) {
        BYPASS.set(true);
        try {
            player.unsafe().sendPacket(packet);
        } finally {
            BYPASS.set(false);
        }
    }

    private void injectNetty(ProxiedPlayer player) {
        try {
            Method getChMethod = player.getClass().getDeclaredMethod("getCh");
            getChMethod.setAccessible(true);
            Object channelWrapper = getChMethod.invoke(player);
            
            Method getHandleMethod = channelWrapper.getClass().getDeclaredMethod("getHandle");
            getHandleMethod.setAccessible(true);
            Channel channel = (Channel) getHandleMethod.invoke(channelWrapper);
            
            if (channel.pipeline().get("captcha-handler") != null) {
                channel.pipeline().remove("captcha-handler");
            }

            channel.pipeline().addBefore("inbound-boss", "captcha-handler", new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    Object packet = msg;
                    if (msg instanceof net.md_5.bungee.protocol.PacketWrapper) {
                        packet = ((net.md_5.bungee.protocol.PacketWrapper) msg).packet;
                    }

                    String chatText = null;
                    if (packet instanceof Chat) {
                        chatText = ((Chat) packet).getMessage();
                    } else if (packet instanceof ClientChat) {
                        chatText = ((ClientChat) packet).getMessage();
                    }

                    if (chatText != null) {
                        CaptchaSession session = sessions.get(player.getUniqueId());
                        if (session != null) {
                            if (!chatText.startsWith("/")) {
                                final String message = chatText.trim();
                                if (session.state == State.CAPTCHA) {
                                    getTaskManager().async(() -> {
                                        if (message.equalsIgnoreCase(session.code)) {
                                            handleSuccess(player);
                                        } else {
                                            handleFailure(player);
                                        }
                                    });
                                }
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                    super.channelRead(ctx, msg);
                }

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    if (BYPASS.get()) {
                        super.write(ctx, msg, promise);
                        return;
                    }

                    CaptchaSession session = sessions.get(player.getUniqueId());
                    if (session != null && !session.verified) {
                        Object packet = msg;
                        if (msg instanceof net.md_5.bungee.protocol.PacketWrapper) {
                            packet = ((net.md_5.bungee.protocol.PacketWrapper) msg).packet;
                        }

                        if (packet != null) {
                            String className = packet.getClass().getSimpleName();
                            if (className.contains("Chat") || className.contains("Title") || 
                                className.contains("PlayerList") || className.contains("BossBar")) {
                                return;
                            }
                        }
                    }
                    super.write(ctx, msg, promise);
                }
            });
        } catch (Exception ignored) {}
    }

    private void handleSuccess(ProxiedPlayer player) {
        CaptchaSession session = sessions.get(player.getUniqueId());
        if (session == null) return;

        session.verified = true;
        
        long expiry = System.currentTimeMillis() + (TimeUnit.HOURS.toMillis(getConfig().getModules().getCaptcha_module().getVerification_duration()));
        verifiedPlayers.put(player.getUniqueId(), expiry);
        saveVerifiedPlayer(player.getUniqueId(), expiry);
        
        cleanupSession(player.getUniqueId());

        player.disconnect(net.md_5.bungee.api.chat.TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', 
            getConfig().getModules().getCaptcha_module().getMessages().getSuccess())));
    }

    private void handleFailure(ProxiedPlayer player) {
        CaptchaSession session = sessions.get(player.getUniqueId());
        if (session == null) return;

        session.attempts++;
        int maxAttempts = 3;
        int blacklistThreshold = getConfig().getModules().getCaptcha_module().getBlacklist_threshold();
        
        if (session.attempts >= blacklistThreshold) {
            if (BlacklistModule.instance != null) {
                int duration = getConfig().getModules().getCaptcha_module().getBlacklist_duration();
                BlacklistModule.instance.getBlacklistManager().add(player.getAddress().getAddress().getHostAddress(), duration);
            }
            player.disconnect(ChatColor.translateAlternateColorCodes('&', 
                getConfig().getModules().getCaptcha_module().getBlacklist_message()));
            sessions.remove(player.getUniqueId());
            return;
        }

        if (session.attempts >= maxAttempts) {
            player.disconnect(ChatColor.translateAlternateColorCodes('&', 
                getConfig().getModules().getCaptcha_module().getMessages().getToo_many_attempts()));
            sessions.remove(player.getUniqueId());
        } else {
            sendMessage(player, ChatColor.translateAlternateColorCodes('&', 
                getConfig().getModules().getCaptcha_module().getMessages().getInvalid_code()
                .replace("%attempts%", String.valueOf(maxAttempts - session.attempts))));
        }
    }

    private String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private byte[] generateMapData(String code) {
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 128, 128);
        
        Random random = new Random();
        int difficulty = getConfig().getModules().getCaptcha_module().getDifficulty();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 50 * difficulty; i++) {
            g.fillOval(random.nextInt(128), random.nextInt(128), 2, 2);
        }

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.BLACK);
        
        FontMetrics fm = g.getFontMetrics();
        int x = (128 - fm.stringWidth(code)) / 2;
        int y = (128 - fm.getHeight()) / 2 + fm.getAscent();
        
        char[] chars = code.toCharArray();
        int currentX = x;
        for (char c : chars) {
            g.rotate(Math.toRadians(random.nextInt(20) - 10), currentX, y);
            g.drawString(String.valueOf(c), currentX, y);
            g.rotate(-Math.toRadians(random.nextInt(20) - 10), currentX, y);
            currentX += fm.charWidth(c) + 2;
        }

        g.dispose();

        byte[] data = new byte[128 * 128];
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                data[j * 128 + i] = MapPalette.getColor(new Color(image.getRGB(i, j)));
            }
        }
        return data;
    }

    private boolean isVerified(UUID uuid) {
        Long expiry = verifiedPlayers.get(uuid);
        if (expiry == null) return false;
        if (System.currentTimeMillis() > expiry) {
            verifiedPlayers.remove(uuid);
            return false;
        }
        return true;
    }

    private void loadVerifiedPlayers() {
        if (oldVerifiedFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(oldVerifiedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        try {
                            // Migrate valid entries
                            verifiedPlayers.put(UUID.fromString(parts[0]), Long.parseLong(parts[1]));
                            saveVerifiedPlayer(UUID.fromString(parts[0]), Long.parseLong(parts[1]));
                        } catch (Exception ignored) {}
                    }
                }
                reader.close();
                oldVerifiedFile.delete();
            } catch (Exception e) {
                XenonCore.instance.logdebugerror("Failed to migrate old verified players: " + e.getMessage());
            }
        }

        if (!verifiedFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(verifiedFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    verifiedPlayers.put(UUID.fromString(parts[0]), Long.parseLong(parts[1]));
                }
            }
        } catch (Exception e) {
            XenonCore.instance.logdebugerror("Failed to load verified players: " + e.getMessage());
        }
    }

    private void saveVerifiedPlayer(UUID uuid, long expiry) {
        if (!verifiedFile.getParentFile().exists()) {
            verifiedFile.getParentFile().mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(verifiedFile, true))) {
            writer.write(uuid.toString() + "," + expiry);
            writer.newLine();
        } catch (Exception e) {
            XenonCore.instance.logdebugerror("Failed to save verified player: " + e.getMessage());
        }
    }

    private void cleanupSessions() {
        long current = System.currentTimeMillis();
        verifiedPlayers.entrySet().removeIf(entry -> current > entry.getValue());
    }

    private enum State {
        CAPTCHA
    }

    private static class CaptchaSession {
        final ProxiedPlayer player;
        State state;
        String code;
        int attempts = 0;
        int elapsed = 0;
        boolean verified = false;
        ScheduledFuture<?> task;

        CaptchaSession(ProxiedPlayer player) {
            this.player = player;
        }
    }
}