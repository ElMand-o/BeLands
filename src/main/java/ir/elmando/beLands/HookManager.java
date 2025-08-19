package ir.elmando.beLands;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class HookManager {

    private static Economy economy;
    private static Permission permission;
    private static GriefPrevention griefPrevention;
    private static boolean papiEnabled;

    public static void init() {
        setupVault();
        setupGriefPrevention();
        setupPlaceholderAPI();
    }

    private static void setupVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().warning("[BeLands] Vault not found. Economy features disabled.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
            Bukkit.getLogger().info("[BeLands] Hooked economy into Vault.");
        }
        RegisteredServiceProvider<Permission> psp = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (psp != null) {
            permission = psp.getProvider();
            Bukkit.getLogger().info("[BeLands] Hooked permissions into Vault.");
        }
    }

    private static void setupGriefPrevention() {
        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") == null) {
            Bukkit.getLogger().warning("[BeLands] GriefPrevention not found. Land features disabled.");
            return;
        }
        griefPrevention = GriefPrevention.instance;
        Bukkit.getLogger().info("[BeLands] Hooked into GriefPrevention.");
    }

    private static void setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papiEnabled = true;
            Bukkit.getLogger().info("[BeLands] Hooked into PlaceholderAPI.");
        } else {
            papiEnabled = false;
            Bukkit.getLogger().warning("[BeLands] PlaceholderAPI not found.");
        }
    }

    // =============================
    // Full access getters
    // =============================

    /**
     * Full access to Vault Economy
     */
    public static Economy economy() {
        return economy;
    }

    public static Permission permission() {
        return permission;
    }

    /**
     * Full access to GriefPrevention
     */
    public static GriefPrevention griefPrevention() {
        return griefPrevention;
    }

    /**
     * True if PlaceholderAPI is present
     */
    public static boolean hasPapi() {
        return papiEnabled;
    }

    // =============================
    // Helpers (optional)
    // =============================

    public static boolean hasEconomy() {
        return economy != null;
    }

    public static boolean hasPermissions() {
        return permission != null;
    }

    public static boolean hasGriefPrevention() {
        return griefPrevention != null;
    }

    public static String applyPlaceholders(org.bukkit.entity.Player player, String text) {
        if (papiEnabled) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
