package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import ir.elmando.beLands.utils.ArgParser;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WhoseCommand implements SubCommand, CommandExecutor {

    private final LandManager landManager;
    private final ConfigManager config;

    public WhoseCommand(final LandManager landManager, final ConfigManager config) {
        this.landManager = landManager;
        this.config = config;
    }

    @Override
    public String getName() {
        return "whose";
    }

    @Override
    public String getDescription() {
        return "View all your claim IDs.";
    }

    @Override
    public String getUsage() {
        return "/land whose";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[]{"myclaims", "mylands"});
    }

    @Override
    public void execute(Player player, String[] args) {
        String target = ArgParser.getString(args, 0, null);
        List<Claim> claims = landManager.getClaims(player);

        if (claims == null || claims.isEmpty()) {
            player.sendMessage(config.getMessage("messages.land.none"));
            return;
        }

        if (target != null)
            sendPlayerClaims(player, target, claims);
        else sendPlayerClaims(player, player.getName(), claims);
    }

    private void sendPlayerClaims(Player sender, String target, List<Claim> claims) {
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        StringBuilder target_sb = new StringBuilder("§a" + targetPlayer.getName() + " Claim IDs: §e");
        for (Claim claim : claims) {
            if (claim.getOwnerID().equals(targetPlayer.getUniqueId()))
                target_sb.append(claim.getID()).append("§7, §e");
        }
        if (claims.isEmpty()) {
            sender.sendMessage(config.getMessage("messages.land.none"));
            return;
        }
        if (target_sb.lastIndexOf("§7, §e") != -1) {
            target_sb.delete(target_sb.lastIndexOf("§7, §e"), target_sb.length());
        }

        sender.sendMessage(target_sb.toString());
        return;
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
