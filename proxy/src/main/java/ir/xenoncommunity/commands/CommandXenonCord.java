package ir.xenoncommunity.commands;

import ir.xenoncommunity.utils.Language;
import net.md_5.bungee.api.chat.TextComponent;
import ir.xenoncommunity.utils.Language;
import net.md_5.bungee.api.chat.TextComponent;
import ir.xenoncommunity.XenonCore;
import ir.xenoncommunity.utils.Message;
import ir.xenoncommunity.module.impl.gui.GuiModule;
import ir.xenoncommunity.module.impl.security.BlacklistModule;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class CommandXenonCord extends Command {
    public static final Set<String> ABstatusPlayers = new HashSet<>();
    public boolean log = false;

    public CommandXenonCord() {
        super("xenoncord", XenonCore.instance.getConfigData().getXenoncord_permission());
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Message.send(sender, Language.get("xenoncord_info").replace("%version%", XenonCore.instance.getVersion()), false);
            return;
        }

        switch (args[0]) {
            case "reload":
                if (!sender.hasPermission(XenonCore.instance.getConfigData().getReload_permission())) return;
                Message.send(sender, XenonCore.instance.getConfigData().getReload_message(), true);
                XenonCore.instance.setConfigData(XenonCore.instance.getConfiguration().init());
                Message.send(sender, XenonCore.instance.getConfigData().getReload_complete_message(), true);
                break;
            case "gui":
                if (!sender.hasPermission(XenonCore.instance.getConfigData().getGui_permission())) return;
                GuiModule guiModule = GuiModule.instance;
                
                if (guiModule == null || !XenonCore.instance.getConfigData().getModules().getGui_module().isEnabled()) {
                    Message.send(sender, Language.get("gui_disabled"), true);
                    return;
                }
                guiModule.toggleGUI(sender);
                break;
        }
    }
}
