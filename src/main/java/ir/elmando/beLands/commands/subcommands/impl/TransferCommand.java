package ir.elmando.beLands.commands.subcommands.impl;

import com.google.common.util.concurrent.CycleDetectingLockFactory;
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

public class TransferCommand implements SubCommand, CommandExecutor {
    private final LandManager landManager;
    private final ConfigManager config;

    public TransferCommand(final LandManager landManager, final ConfigManager config) {
        this.landManager = landManager;
        this.config = config;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Transfers land/claim to the selected player";
    }

    @Override
    public String getUsage() {
        return "/land give <claim_id> <player>";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[]{"transfer"});
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
            player.sendMessage(config.getMessage("messages.land.transfer.not_owner"));
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("owner", player.getName());
        placeholders.put("player", target);
        placeholders.put("claimId", String.valueOf(claimId));
        if (landManager.transferClaim(claimId, player, target)) {

            player.sendMessage(config.getMessage("messages.land.transfer.success", placeholders));
            if (Bukkit.getOfflinePlayer(target).isConnected())
                Bukkit.getOfflinePlayer(target).getPlayer().sendMessage(config.getMessage("messages.land.transfer.received_transfer", placeholders));
        } else {
            player.sendMessage(config.getMessage("messages.land.transfer.player_not_found", placeholders));
        }

    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage("§cUsage: " + getUsage());
            return false;
        }

        execute((Player) sender, args);
        return true;
    }
}
