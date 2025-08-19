package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.BeLands;
import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StartPCommand implements SubCommand, CommandExecutor {

    private final BeLands plugin;
    private final ConfigManager config;
    private final LandManager landManager;

    // Stores player UUID -> location
    private final Map<UUID, Location> startPositions = new ConcurrentHashMap<>();

    public StartPCommand(BeLands plugin, ConfigManager config, LandManager landManager) {
        this.plugin = plugin;
        this.config = config;
        this.landManager = landManager;
    }

    @Override
    public String getName() {
        return "startp";
    }

    @Override
    public String getDescription() {
        return "Sets the first land/claim corner where the player is standing.";
    }

    @Override
    public String getUsage() {
        return "/startp";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("startpoint");
    }

    @Override
    public void execute(Player player, String[] args) {
        Location loc = player.getLocation().getBlock().getLocation();
        if (landManager.getClaim(loc) != null) {
            player.sendMessage(config.getMessage("messages.land.overlap"));
            return;
        }
        startPositions.put(player.getUniqueId(), loc);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("pos", "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());

        player.sendMessage(config.getMessage("messages.selection.first_position_set", placeholders));

        // Schedule removal after 10 minutes (20 ticks * 60 sec * 10 min = 12000 ticks)
        new BukkitRunnable() {
            @Override
            public void run() {
                startPositions.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, 20L * 60 * 10);
    }

    /**
     * Get the saved start position for a player
     */
    public Location getStartPosition(Player player) {
        return startPositions.get(player.getUniqueId());
    }

    /**
     * Utility to format a location nicely
     */
    private String formatLocation(Location loc) {
        return "X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ();
    }

    /* CommandExecutor implementation */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        execute(player, args);
        return true;
    }
}
