package ir.xenoncommunity.module.impl.security;

import ir.xenoncommunity.annotations.ModuleInfo;
import ir.xenoncommunity.module.ModuleBase;
import ir.xenoncommunity.utils.Message;
import ir.xenoncommunity.utils.WhitelistUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@ModuleInfo(name = "AccountLimit", version = 1.0, description = "Limits the number of accounts per IP address.")
public class AccountLimitModule extends ModuleBase {

    @Override
    public void onInit() {
        if (!getConfig().getModules().getAccount_limit_module().isEnabled()) {
            return;
        }
        getServer().getPluginManager().registerListener(null, this);
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if (event.isCancelled()) return;

        final InetSocketAddress address = (InetSocketAddress) event.getConnection().getSocketAddress();
        final InetAddress inetAddress = address.getAddress();
        
        if (WhitelistUtils.isWhitelisted(inetAddress.getHostAddress(), event.getConnection().getName()))
            return;

        int count = 0;
        final int limit = getConfig().getModules().getAccount_limit_module().getMax_accounts();

        for (ProxiedPlayer player : getServer().getPlayers()) {
            if (player.getAddress().getAddress().equals(inetAddress)) {
                count++;
            }
        }

        


        if (count >= limit) {
            String kickMsg = getConfig().getModules().getAccount_limit_module().getKick_message()
                    .replace("%limit%", String.valueOf(limit))
                    .replace("%count%", String.valueOf(count));
            
            event.setCancelReason(TextComponent.fromLegacyText(Message.translateColor(kickMsg)));
            event.setCancelled(true);
        }
    }
}