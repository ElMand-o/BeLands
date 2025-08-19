package ir.elmando.beLands.commands.subcommands.impl;

import com.griefprevention.visualization.VisualizationType;
import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import me.ryanhamshire.GriefPrevention.Claim;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhoseCommand implements SubCommand, CommandExecutor {
    private final LandManager landManager;
    private final ConfigManager config;

    public WhoseCommand(LandManager landManager, ConfigManager config) {
        this.landManager = landManager;
        this.config = config;
    }

    @Override
    public String getName() {
        return "here";
    }

    @Override
    public String getDescription() {
        return "Gets the land info where the player is standing in";
    }

    @Override
    public String getUsage() {
        return "/land here";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[]{"info", "whose"});
    }

    @Override
    public void execute(Player player, String[] args) {
        Claim claim = landManager.getClaim(player.getLocation());
        Component message;
        if (claim == null) message = config.getMessage("messages.land.no_info");
        else {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("owner", claim.getOwnerName());
            placeholders.put("claimId", claim.getID().toString());
            message = config.getMessage("messages.land.info", placeholders);
            landManager.showClaim(claim, player, VisualizationType.CLAIM);
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
