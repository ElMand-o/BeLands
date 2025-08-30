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

import java.util.*;

public class TrustCommand implements SubCommand, CommandExecutor {
    private final LandManager landManager;
    private final ConfigManager config;

    public TrustCommand(final LandManager landManager, final ConfigManager config) {
        this.landManager = landManager;
        this.config = config;
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Grant a player access to your land/claim";
    }

    @Override
    public String getUsage() {
        return "/land invite <claim_id> <player>";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[]{"trust"});
    }

    @Override
    public void execute(Player player, String[] args) {
        String target = ArgParser.getString(args, 1, null);
        int claimId = ArgParser.getInt(args, 0, -1);
//        String e = ArgParser.getString(args, 0, null);

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

        if (target.equalsIgnoreCase("e")) {
            List<String> trusted_users = new ArrayList<String>();

            for (String manager : claim.managers) {
                Player temp_player = Bukkit.getOfflinePlayer(UUID.fromString(manager)).getPlayer();

                if (temp_player != null) {
                    trusted_users.add(temp_player.getName());
                }
            }

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("trusted_users", String.join("<orange>,</orange> ", trusted_users));

            player.sendMessage(config.getMessage("messages.land.trust_e", placeholders));
            return;
        }

        if (landManager.isTrusted(claimId, player, target)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", target);
            player.sendMessage(config.getMessage("messages.land.trust.already_trusted", placeholders));
            return;
        }

        if (landManager.trustUser(claimId, player, target)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", target);
            placeholders.put("claimId", String.valueOf(claimId));
            player.sendMessage(config.getMessage("messages.land.trust.success", placeholders));

            Player target_player = Bukkit.getPlayer(target);
            if (target_player != null) Bukkit.getPlayer(target).sendMessage(config.getMessage("messages.land.trust.granted_to_target", placeholders));
        } else {
            player.sendMessage(config.getMessage("messages.land.trust.not_owner"));
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage("§cUsage: " + getUsage());
            return false;
        }
        ;

        execute((Player) sender, args);
        return true;
    }
}
