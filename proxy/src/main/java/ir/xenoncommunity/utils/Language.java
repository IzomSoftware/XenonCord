package ir.xenoncommunity.utils;

import ir.xenoncommunity.XenonCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Language {

    private static Configuration config;
    private static File file;
    public static String prefix = "";

    public static void init() {
        File folder = new File("XenonCord");
        if (!folder.exists()) folder.mkdirs();

        file = new File(folder, "language.yml");

        if (!file.exists()) {
            try (InputStream in = XenonCore.class.getResourceAsStream("/language.yml")) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException e) {
                XenonCore.instance.getLogger().error("Could not save language.yml: " + e.getMessage());
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", "&b&lXenonCord &8» &7"));
        } catch (IOException e) {
            XenonCore.instance.getLogger().error("Could not load language.yml: " + e.getMessage());
        }
    }

    public static String get(String path) {
        if (config == null) return "Language not loaded: " + path;
        String msg = config.getString(path);
        if (msg == null) return path;
        return ChatColor.translateAlternateColorCodes('&', msg.replace("%prefix%", prefix));
    }
    
    public static String get(String path, String... replacements) {
        String msg = get(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                msg = msg.replace(replacements[i], replacements[i + 1]);
            }
        }
        return msg;
    }
    
    public static void reload() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", "&b&lXenonCord &8» &7"));
        } catch (IOException e) {
            XenonCore.instance.getLogger().error("Could not reload language.yml: " + e.getMessage());
        }
    }
}