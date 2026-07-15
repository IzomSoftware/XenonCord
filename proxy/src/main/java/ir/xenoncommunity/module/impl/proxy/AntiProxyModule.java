package ir.xenoncommunity.module.impl.proxy;

import ir.xenoncommunity.XenonCore;
import ir.xenoncommunity.annotations.ModuleInfo;
import ir.xenoncommunity.module.ModuleBase;
import ir.xenoncommunity.utils.Colorize;
import ir.xenoncommunity.utils.HttpClient;
import ir.xenoncommunity.utils.WhitelistUtils;
import lombok.Getter;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.event.EventHandler;

import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ModuleInfo(name = "AntiProxy", version = 1.0, description = "Restricts connections from known proxies")
public class AntiProxyModule extends ModuleBase {

    @Getter
    private ConcurrentLinkedQueue<String> proxyList;

    @Override
    public void onInit() {
        if (!getConfig().getModules().getAnti_proxy_module().isEnabled())
            return;
        getServer().getPluginManager().registerListener(null, this);
        getTaskManager().async(() -> {
            getLogger().info(Colorize.console("&bFetching proxies from config links...."));

            XenonCore.instance.getConfiguration().downloadProxyLists();
            proxyList = new ConcurrentLinkedQueue<>(XenonCore.instance.getConfiguration().getProxyList());

            getLogger()
                    .info(Colorize
                            .console(String.format("&bFetching DONE! total cached proxies: %d", proxyList.size())));
        });
    }

    private boolean checkSuspicious(PendingConnection connection) {
        final String ip = connection.getAddress().getAddress().getHostAddress();

        if (WhitelistUtils.isWhitelisted(ip, null))
            return false;

        if (proxyList.contains(ip))
            return true;

        getTaskManager().add(() -> {
            try {
                final StringBuilder sb = new StringBuilder();
                HttpClient.get(new URL(String.format(
                        "http://ip-api.com/json/%s?fields=status,message,country,countryCode,isp,org,mobile,proxy,hosting,query",
                        ip))).get()
                        .forEach(sb::append);
                    System.out.println(sb.toString());
                final JsonObject object = JsonParser.parseString(sb.toString()).getAsJsonObject();
                
                String msg;
                if ((msg = object.get("message").getAsString()) != null) {
                    getLogger().info(String.format("Caught an error while contacting ip-api: %s", msg));
                    return;
                }

                if (object.get("hosting").getAsBoolean() || object.get("proxy").getAsBoolean()) {
                    proxyList.add(ip);
                }

                XenonCore.instance.getConfiguration().addIpToProxyList(ip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return false;
    }

    @EventHandler
    public void onHandshake(PlayerHandshakeEvent event) {
        boolean block = checkSuspicious(event.getConnection());
        if (getConfig().getGeneral().isBother_suspicious_connections()) {
            event.setIgnored(block);
        } else {
            event.setCancelled(block);
        }
    }
}
