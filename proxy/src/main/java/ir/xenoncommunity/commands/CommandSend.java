package ir.xenoncommunity.commands;

import ir.xenoncommunity.utils.Language;
import net.md_5.bungee.api.chat.TextComponent;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

@SuppressWarnings({"unused", "deprecation"})
public class CommandSend extends Command implements TabExecutor {

    public CommandSend() {
        super("send", "bungeecord.command.send");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Language.get("send_cmd_usage")));
            return;
        }
        ServerInfo server = ProxyServer.getInstance().getServerInfo(args[1]);
        if (server == null) {
            sender.sendMessage(TextComponent.fromLegacyText(Language.get("server_not_found").replace("%server%", args[1])));
            return;
        }

        List<ProxiedPlayer> targets;
        if (args[0].equalsIgnoreCase("all")) {
            targets = new ArrayList<>(ProxyServer.getInstance().getPlayers());
        } else if (args[0].equalsIgnoreCase("current")) {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(TextComponent.fromLegacyText(Language.get("not_player")));
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer) sender;
            targets = new ArrayList<>(player.getServer().getInfo().getPlayers());
        } else {
            // If we use a server name, send the entire server. This takes priority over players.
            ServerInfo serverTarget = ProxyServer.getInstance().getServerInfo(args[0]);
            if (serverTarget != null) {
                targets = new ArrayList<>(serverTarget.getPlayers());
            } else {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(Language.get("player_not_found").replace("%player%", args[0])));
                    return;
                }
                targets = Collections.singletonList(player);
            }
        }

        final SendCallback callback = new SendCallback(sender);
        for (ProxiedPlayer player : targets) {
            ServerConnectRequest request = ServerConnectRequest.builder()
                    .target(server)
                    .reason(ServerConnectEvent.Reason.COMMAND)
                    .callback(new SendCallback.Entry(callback, player, server))
                    .build();
            player.connect(request);
        }

        sender.sendMessage(TextComponent.fromLegacyText(Language.get("send_attempt").replace("%count%", String.valueOf(targets.size())).replace("%server%", server.getName())));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 2 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase(Locale.ROOT);
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.getName().toLowerCase(Locale.ROOT).startsWith(search)) {
                    matches.add(player.getName());
                }
            }
            if ("all".startsWith(search)) {
                matches.add("all");
            }
            if ("current".startsWith(search)) {
                matches.add("current");
            }
        } else {
            String search = args[1].toLowerCase(Locale.ROOT);
            for (String server : ProxyServer.getInstance().getServers().keySet()) {
                if (server.toLowerCase(Locale.ROOT).startsWith(search)) {
                    matches.add(server);
                }
            }
        }
        return matches;
    }

    protected static class SendCallback {

        private final Map<ServerConnectRequest.Result, List<String>> results = new HashMap<>();
        private final CommandSender sender;
        private int count = 0;

        public SendCallback(CommandSender sender) {
            this.sender = sender;
            for (ServerConnectRequest.Result result : ServerConnectRequest.Result.values()) {
                results.put(result, Collections.synchronizedList(new ArrayList<>()));
            }
        }

        public void lastEntryDone() {
            sender.sendMessage(TextComponent.fromLegacyText(Language.get("send_results")));
            for (Map.Entry<ServerConnectRequest.Result, List<String>> entry : results.entrySet()) {
                ComponentBuilder builder = new ComponentBuilder("");
                if (!entry.getValue().isEmpty()) {
                    builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Joiner.on(", ").join(entry.getValue())).color(ChatColor.YELLOW).create()));
                }
                builder.append(entry.getKey().name() + ": ").color(ChatColor.GREEN);
                builder.append("" + entry.getValue().size()).bold(true);
                sender.sendMessage(builder.create());
            }
        }

        public static class Entry implements Callback<ServerConnectRequest.Result> {

            private final SendCallback callback;
            private final ProxiedPlayer player;
            private final ServerInfo target;

            public Entry(SendCallback callback, ProxiedPlayer player, ServerInfo target) {
                this.callback = callback;
                this.player = player;
                this.target = target;
                this.callback.count++;
            }

            @Override
            public void done(ServerConnectRequest.Result result, Throwable error) {
                callback.results.get(result).add(player.getName());
                if (result == ServerConnectRequest.Result.SUCCESS) {
                    player.sendMessage(TextComponent.fromLegacyText(Language.get("you_got_summoned").replace("%server%", target.getName()).replace("%player%", callback.sender.getName())));
                }

                if (--callback.count == 0) {
                    callback.lastEntryDone();
                }
            }
        }
    }
}
