package ir.xenoncommunity.utils;

import ir.xenoncommunity.XenonCore;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Objects;

@Getter
public class Configuration {
    private final File configFile;
    private final File bstatsFile;
    private final Logger logger;

    public Configuration() {
        this.bstatsFile = new File("XenonCord/bstats", "bstats.txt");
        this.configFile = new File("XenonCord", "XenonCord.yml");
        this.logger = XenonCore.instance.getLogger();
    }

    private void copyConfig() {
        try {
            Files.copy(Objects.requireNonNull(XenonCore.class.getResourceAsStream("/XenonCord.yml")), configFile.toPath());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public ConfigData init() {
        logger.info("Initializing Configuration...");
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            if (!configFile.exists()) copyConfig();

            Thread.currentThread().setContextClassLoader(ConfigData.class.getClassLoader());

            @Cleanup final FileInputStream is = new FileInputStream(configFile);
            final ConfigData configData = new Yaml().loadAs(is, ConfigData.class);

            // Translate colors

            configData.getMessages().setCannot_execute_as_console(Message.translateColor(configData.getMessages().getCannot_execute_as_console()));
            configData.getMessages().setUnknown_option(Message.translateColor(configData.getMessages().getUnknown_option()));
            configData.getMessages().setReload_start(Message.translateColor(configData.getMessages().getReload_start()));
            configData.getMessages().setReload_complete(Message.translateColor(configData.getMessages().getReload_complete()));
            
            Captcha captcha = configData.getModules().getCaptcha_module();

            captcha.getMessages().setPre_verify(Message.translateColor(captcha.getMessages().getPre_verify()));
            captcha.getMessages().setPing_too_high(Message.translateColor(captcha.getMessages().getPing_too_high()));
            captcha.getMessages().setInstructions(Message.translateColor(captcha.getMessages().getInstructions()));
            captcha.getMessages().setSuccess(Message.translateColor(captcha.getMessages().getSuccess()));
            captcha.getMessages().setInvalid_code(Message.translateColor(captcha.getMessages().getInvalid_code()));
            captcha.getMessages().setToo_many_attempts(Message.translateColor(captcha.getMessages().getToo_many_attempts()));

            captcha.setBlacklist_message(Message.translateColor(captcha.getBlacklist_message()));
            
            logger.info("Successfully Initialized!");

            return configData;
        } catch (Exception e) {
            logger.error("Failed to load configuration: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Getter
    @Setter
    public static class ConfigData {
        private General general;
        private Messages messages;
        private Permissions permissions;
        private Modules modules;

        public boolean isDebug() {
            return general != null && general.isDebug();
        }

        public String getXenoncord_permission() { return permissions.getXenoncord(); }
        public String getReload_permission() { return permissions.getReload(); }
        public String getReload_message() { return messages.getReload_start(); }
        public String getReload_complete_message() { return messages.getReload_complete(); }
        public String getUnknown_option_message() { return messages.getUnknown_option(); }
        public String getGui_permission() { return permissions.getGui(); }
    }

    @Getter
    @Setter
    public static class General {
        private boolean debug;
        private Whitelist whitelist;
    }

    @Getter
    @Setter
    public static class Whitelist {
        private String[] ips;
        private String[] users;
    }
    
    @Getter
    @Setter
    public static class Messages {
        private String cannot_execute_as_console;
        private String unknown_option;
        private String reload_start;
        private String reload_complete;
    }

    @Getter
    @Setter
    public static class Permissions {
        private String xenoncord;
        private String reload;
        private String bplugins;
        private String bplugins_toggle;
        private String gui;
    }

    @Getter
    @Setter
    public static class Modules {
        private GuiModule gui_module;
        private IPWhitelist ip_whitelist_module;
        private BrandModule brand_module;
        private AntiProxyModule anti_proxy_module;
        private AccountLimit account_limit_module;
        private Captcha captcha_module;
        
    }

    @Getter
    @Setter
    public static class GuiModule {
        private long refresh_rate;
        private boolean enabled;
    }

    @Getter
    @Setter
    public static class AntiProxyModule {
        private boolean enabled;
        private int update_interval;
        private String[] links;
    }

    @Getter
    @Setter
    public static class BrandModule {
        private boolean enabled;
        private String name;
    }

    @Getter
    @Setter
    public static class IPWhitelist {
        private boolean enabled;
        private String mode;
        private String[] list;
    }

    @Getter
    @Setter
    public static class AccountLimit {
        private boolean enabled;
        private int max_accounts;
        private String kick_message;
    }

    @Getter
    @Setter
    public static class Captcha {
        private boolean enabled;
        private int verification_duration;
        private int pre_verify_duration;
        private int max_ping;
        private int difficulty;
        private int blacklist_threshold;
        private int blacklist_duration;
        private String blacklist_message;
        private CaptchaMessages messages;
    }

    @Getter
    @Setter
    public static class CaptchaMessages {
        private String pre_verify;
        private String ping_too_high;
        private String instructions;
        private String success;
        private String invalid_code;
        private String too_many_attempts;
    }
}