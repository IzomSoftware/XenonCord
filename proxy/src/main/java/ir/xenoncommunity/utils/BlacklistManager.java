package ir.xenoncommunity.utils;

import ir.xenoncommunity.XenonCore;
import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BlacklistManager {
    private static final File BLACKLIST_FILE = new File("XenonCord/blacklist.csv");
    private static final File OLD_BLACKLIST_FILE = new File("blacklist.txt");
    private final Map<String, Long> blacklistedIps = Collections.synchronizedMap(new HashMap<>());

    public BlacklistManager() {
        loadBlacklist();
    }
    
    public void add(String ip, int durationHours) {
        long expiry = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(durationHours);
        blacklistedIps.put(ip, expiry);
        saveBlacklist();
    }
    public void remove(String ip) {
        if (blacklistedIps.remove(ip) != null) {
            saveBlacklist();
        }
    }
    public boolean isBlacklisted(String ip) {
        Long expiry = blacklistedIps.get(ip);
        if (expiry == null) return false;
        if (System.currentTimeMillis() > expiry) {
            remove(ip);
            return false;
        }
        return true;
    }
    private void loadBlacklist() {
        // Migrate old blacklist if exists
        if (OLD_BLACKLIST_FILE.exists()) {
            try (BufferedReader reader = Files.newBufferedReader(OLD_BLACKLIST_FILE.toPath())) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        // Default to 72h for migrated IPs
                        long expiry = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(72);
                        blacklistedIps.put(line.trim(), expiry);
                    }
                }
                reader.close();
                OLD_BLACKLIST_FILE.delete();
                saveBlacklist();
            } catch (IOException e) {
                XenonCore.instance.getLogger().error("Failed to migrate old blacklist: " + e.getMessage());
            }
        }
        if (!BLACKLIST_FILE.exists()) {
            try {
                if (BLACKLIST_FILE.getParentFile() != null && !BLACKLIST_FILE.getParentFile().exists()) {
                    BLACKLIST_FILE.getParentFile().mkdirs();
                }
                BLACKLIST_FILE.createNewFile();
            } catch (IOException e) {
                XenonCore.instance.getLogger().error("Failed to create blacklist file: " + e.getMessage());
            }
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(BLACKLIST_FILE.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        try {
                            blacklistedIps.put(parts[0], Long.parseLong(parts[1]));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (IOException e) {
            XenonCore.instance.getLogger().error("Failed to load blacklist: " + e.getMessage());
        }
    }
    private void saveBlacklist() {
        try (BufferedWriter writer = Files.newBufferedWriter(BLACKLIST_FILE.toPath())) {
            for (Map.Entry<String, Long> entry : blacklistedIps.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            XenonCore.instance.getLogger().error("Failed to save blacklist: " + e.getMessage());
        }
    }
}