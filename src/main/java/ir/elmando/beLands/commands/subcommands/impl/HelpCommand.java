package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.commands.subcommands.SubCommand;
import ir.elmando.beLands.commands.LandCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements SubCommand, CommandExecutor {

    private LandCommand landCommand;

//    public HelpCommand(LandCommand landCommand) {
//        this.landCommand = landCommand;
//    }

    public void setLandCommand(LandCommand landCommand) {
        this.landCommand = landCommand;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays a list of all land commands.";
    }

    @Override
    public String getUsage() {
        return "/land help";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("?");
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage("§6=== BeLands Commands ===");
        landCommand.getHelpSubCommands().values().forEach(sub -> {
            player.sendMessage("§e" + sub.getUsage() + " §7- " + sub.getDescription());
        });
    }

    // CommandExecutor implementation
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
