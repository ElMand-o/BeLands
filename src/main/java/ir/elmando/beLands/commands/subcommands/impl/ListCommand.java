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

import java.util.*;

public class ListCommand implements SubCommand, CommandExecutor {
    private final LandManager landManager;
    private final ConfigManager config;

    public ListCommand(LandManager landManager, ConfigManager config) {
        this.landManager = landManager;
        this.config = config;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Buys the player selection";
    }

    @Override
    public String getUsage() {
        return "/land list";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public void execute(Player player, String[] args) {
        int page = ArgParser.getInt(args, 0, 1);
        Collection<Claim> claims = landManager.getAllClaims();

        claims = claims.stream()
                .skip((page - 1) * 5L)
                .limit(5)
                .toList();

        StringBuilder what_to_send = new StringBuilder();
        for (Claim claim : claims) {
            what_to_send
                    .append("<gold>")
                    .append(claim.getOwnerName())
                    .append("</gold>: <yellow>")
                    .append(claim.getID().toString())
                    .append("</yellow>\n");
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("claimIds", what_to_send.toString());

        player.sendMessage(config.getMessage("messages.land.all_lands", placeholders));
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        execute(player, args);
        return true;
    }
}
