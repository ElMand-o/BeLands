package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import ir.elmando.beLands.utils.ArgParser;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveCommand implements SubCommand, CommandExecutor {

    private final ConfigManager config;
    private final LandManager landManager;

    public MoveCommand(ConfigManager configManager, LandManager landManager) {
        this.config = configManager;
        this.landManager = landManager;
    }

    @Override
    public String getName() {
        return "move";
    }

    @Override
    public String getDescription() {
        return "Moves the player to requested claim/land";
    }

    @Override
    public String getUsage() {
        return "/land move <id>";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[] {"tp", "teleport"});
    }

    @Override
    public void execute(Player player, String[] args) {
        int claimId = ArgParser.getInt(args, 0, -1);

        if (claimId == -1) {
            player.sendMessage("§cUsage: " + getUsage());
            return;
        }

        Claim claim = landManager.getClaim(claimId);
        if (claim == null) {
            player.sendMessage(config.getMessage("messages.land.no_info"));
            return;
        }

        landManager.teleport(claimId, player);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("claimId", String.valueOf(claimId));
        player.sendMessage(config.getMessage("messages.land.teleport", placeholders));
    }



    // Implement CommandExecutor for direct registration
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute((Player) sender, args);
        return true;
    }
}
