package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import ir.elmando.beLands.utils.ArgParser;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellCommand implements SubCommand, CommandExecutor {
    private final LandManager landManager;
    private final ConfigManager config;
    private final GriefPrevention griefPrevention;
    private final Economy economy;

    public SellCommand(LandManager landManager, ConfigManager config, GriefPrevention griefPrevention, Economy economy) {
        this.landManager = landManager;
        this.config = config;
        this.griefPrevention = griefPrevention;
        this.economy = economy;
    }

    @Override
    public String getName() {
        return "sell";
    }

    @Override
    public String getDescription() {
        return "Sells the selected land/claim";
    }

    @Override
    public String getUsage() {
        return "/land sell <land_id>";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[] { "abandon", "unclaim", "disband" });
    }

    @Override
    public void execute(Player player, String[] args) {
        int claim_id = ArgParser.getInt(args, 0, -1);
        if (claim_id == -1) {
            player.sendMessage("§cUsage: " + getUsage());
            return;
        }

        Claim claim = griefPrevention.dataStore.getClaim(claim_id);
        if (claim == null) {
            player.sendMessage(config.getMessage("messages.land.no_info"));
            return;
        }

        if (!player.getUniqueId().equals(claim.getOwnerID())) {
            player.sendMessage(config.getMessage("messages.land.not_owner"));
            return;
        }

        int price = landManager.getLandSellPrice(claim);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("price", String.valueOf(price));

        player.sendMessage(config.getMessage("messages.land.sold", placeholders));
        landManager.removeClaim(claim);
        economy.depositPlayer(player, price);
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
