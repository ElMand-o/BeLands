package ir.elmando.beLands.commands.subcommands.impl;

import com.griefprevention.visualization.VisualizationType;
import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.CreateClaimResult;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyCommand implements SubCommand, CommandExecutor {
    private final LandManager landManager;
    private final ConfigManager config;
    private final StartPCommand startPCommand;
    private final EndPCommand endPCommand;
    private final Economy economy;

    public BuyCommand(LandManager landManager, ConfigManager config, StartPCommand startPCommand, EndPCommand endPCommand, Economy economy) {
        this.landManager = landManager;
        this.config = config;
        this.startPCommand = startPCommand;
        this.endPCommand = endPCommand;
        this.economy = economy;
    }

    @Override
    public String getName() {
        return "buy";
    }

    @Override
    public String getDescription() {
        return "Buys the player selection";
    }

    @Override
    public String getUsage() {
        return "/land buy";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public void execute(Player player, String[] args) {
        Location pos1 = startPCommand.getStartPosition(player);
        Location pos2 = endPCommand.getEndPosition(player);
        if (pos1 == null || pos2 == null) {
             player.sendMessage(config.getMessage("messages.selection.too_small"));
             return;
        }

        CreateClaimResult claim = landManager.createClaim(pos1, pos2, player);
        Component message;

        int price = landManager.getLandBuyPrice(pos1, pos2);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("price", String.valueOf(price));

        if (price > economy.getBalance(player))
            message = config.getMessage("messages.land.not_enough_money", placeholders);
        else if (!claim.succeeded) {
            message = config.getMessage("messages.land.overlap");
        } else {
            message = config.getMessage("messages.land.bought", placeholders);
            landManager.showClaim(claim.claim, player, VisualizationType.CLAIM);
            economy.withdrawPlayer(player, price);
        }


        player.sendMessage(message);
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
