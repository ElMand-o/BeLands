package ir.elmando.beLands.commands.subcommands.impl;

import com.griefprevention.visualization.VisualizationType;
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

public class EndPCommand implements SubCommand, CommandExecutor {

    private final BeLands plugin;
    private final ConfigManager config;
    private final LandManager landManager;
    private final StartPCommand startPCommand;

    // Stores player UUID -> location
    private final Map<UUID, Location> endPositions = new ConcurrentHashMap<>();

    public EndPCommand(BeLands plugin, ConfigManager config, LandManager landManager, StartPCommand startPCommand) {
        this.plugin = plugin;
        this.config = config;
        this.landManager = landManager;
        this.startPCommand = startPCommand;
    }

    @Override
    public String getName() {
        return "endp";
    }

    @Override
    public String getDescription() {
        return "Sets the second land/claim corner where the player is standing.";
    }

    @Override
    public String getUsage() {
        return "/endp";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("endpoint");
    }

    @Override
    public void execute(Player player, String[] args) {
        Location loc = player.getLocation().getBlock().getLocation();
        if (landManager.getClaim(loc) != null) {
            player.sendMessage(config.getMessage("messages.land.overlap"));
            return;
        }
        Location pos1 = startPCommand.getStartPosition(player);
        endPositions.put(player.getUniqueId(), loc);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("pos", "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());
        placeholders.put("price", String.valueOf(landManager.getLandBuyPrice(pos1, loc)));

        player.sendMessage(config.getMessage("messages.selection.second_position_set", placeholders));
        player.sendMessage(config.getMessage("messages.land.how_much", placeholders));

        landManager.showArea(pos1, loc, player, VisualizationType.CONFLICT_ZONE);

        // Schedule removal after 10 minutes (20 ticks * 60 sec * 10 min = 12000 ticks)
        new BukkitRunnable() {
            @Override
            public void run() {
                endPositions.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, 20L * 60 * 10);
    }

    /**
     * Get the saved end position for a player
     */
    public Location getEndPosition(Player player) {
        return endPositions.get(player.getUniqueId());
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
