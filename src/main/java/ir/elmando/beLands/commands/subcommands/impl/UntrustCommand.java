package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import ir.elmando.beLands.utils.ArgParser;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UntrustCommand implements SubCommand, CommandExecutor {
    private final LandManager landManager;
    private final ConfigManager config;

    public UntrustCommand(final LandManager landManager, final ConfigManager config) {
        this.landManager = landManager;
        this.config = config;
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Grant a player access to your land/claim";
    }

    @Override
    public String getUsage() {
        return "/land kick <claim_id> <player>";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[] {"untrust"});
    }

    @Override
    public void execute(Player player, String[] args) {
        String target = ArgParser.getString(args, 1, null);
        int claimId = ArgParser.getInt(args, 0, -1);

        if (target == null || claimId == -1) {
            player.sendMessage("§cUsage: " + getUsage());
            return;
        }

        Claim claim = landManager.getClaim(claimId);
        if (claim == null) {
            player.sendMessage(config.getMessage("messages.land.no_info"));
            return;
        }

        if (!claim.ownerID.equals(player.getUniqueId())) {
            player.sendMessage(config.getMessage("messages.land.trust.not_owner"));
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target);
        placeholders.put("claimId", String.valueOf(claimId));

        if (!landManager.isTrusted(claimId, player, target)) {
            player.sendMessage(config.getMessage("messages.land.trust.not_trusted", placeholders));
            return;
        }

        if (landManager.untrustUser(claimId, player, target)) {
            player.sendMessage(config.getMessage("messages.land.trust.removed", placeholders));

            Player target_player = Bukkit.getPlayer(target);
            if (target_player != null) Bukkit.getPlayer(target).sendMessage(config.getMessage("messages.land.trust.removed_from_target", placeholders));
        } else {
            player.sendMessage(config.getMessage("messages.land.trust.not_owner"));
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage("§cUsage: " + getUsage());
            return false;
        };

        execute((Player) sender, args);
        return true;
    }
}
