package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class ClaimsListCommand implements SubCommand, CommandExecutor {

    private final LandManager landManager;
    private final ConfigManager config;

    public ClaimsListCommand(final LandManager landManager, final ConfigManager config) {
        this.landManager = landManager;
        this.config = config;
    }

    @Override
    public String getName() {
        return "claims";
    }

    @Override
    public String getDescription() {
        return "View all your claim IDs.";
    }

    @Override
    public String getUsage() {
        return "/land claims";
    }

    @Override
    public List<String> getAliases() {
        return List.of("listclaims", "mylands", "lands", "list");
    }

    @Override
    public void execute(Player player, String[] args) {
        List<Claim> claims = landManager.getClaims(player);

        if (claims == null || claims.isEmpty()) {
            player.sendMessage(config.getMessage("messages.land.none"));
            return;
        }

        StringBuilder sb = new StringBuilder("§aYour Claim IDs: §e");
        for (Claim claim : claims) {
            if (claim.getOwnerID().equals(player.getUniqueId()))
                sb.append(claim.getID()).append("§7, §e");
        }

        if (sb.isEmpty()) {
            player.sendMessage(config.getMessage("messages.land.none"));
            return;
        }

        // remove trailing comma+space
        if (sb.lastIndexOf("§7, §e") != -1) {
            sb.delete(sb.lastIndexOf("§7, §e"), sb.length());
        }

        player.sendMessage(sb.toString());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return false;
        }

        execute(player, args);
        return true;
    }
}
