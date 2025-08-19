package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import ir.elmando.beLands.utils.ArgParser;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceCommand implements SubCommand, CommandExecutor {

    private final GriefPrevention griefPrevention;
    private final LandManager landManager;
    private final ConfigManager config;

    public PriceCommand(GriefPrevention griefPrevention, LandManager landManager, ConfigManager config) {
        this.griefPrevention = griefPrevention;
        this.landManager = landManager;
        this.config = config;
    }

    @Override
    public String getName() {
        return "price";
    }

    @Override
    public String getDescription() {
        return "Gets the buy/sell price of a land/claim";
    }

    @Override
    public String getUsage() {
        return "/land price <claim_id>";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[] {"howmuch"});
    }

    @Override
    public void execute(Player player, String[] args) {
        int claim_id = ArgParser.getInt(args, 0, -1);
        if (claim_id == -1) {
            player.sendMessage("§cUsage: " + getUsage());
            return;
        }

        Claim claim = griefPrevention.dataStore.getClaim(claim_id);

        Location pos1 = claim.getLesserBoundaryCorner();
        Location pos2 = claim.getGreaterBoundaryCorner();

        int buy_price = landManager.getLandBuyPrice(pos1, pos2);
        int sell_price = landManager.getLandSellPrice(pos1, pos2);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("claimId", String.valueOf(claim_id));
        placeholders.put("buy_price", String.valueOf(buy_price));
        placeholders.put("sell_price", String.valueOf(sell_price));

        player.sendMessage(config.getMessage("messages.land.price", placeholders));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        execute((Player) sender, args);
        return true;
    }
}
