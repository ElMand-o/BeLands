package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.ConfigManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ReloadCommand implements SubCommand, CommandExecutor {

    private final ConfigManager configManager;
    private final Permission permission;

    public ReloadCommand(ConfigManager configManager, Permission permission) {
        this.configManager = configManager;
        this.permission = permission;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin's config file";
    }

    @Override
    public String getUsage() {
        return "/land reload";
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[] {"rl"});
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.isOp() && !permission.has(player, "lands.reload")) {player.sendMessage(configManager.getMessage("messages.no_permission")); return;}
        configManager.reload();
        player.sendMessage(configManager.getMessage("messages.config_reloaded"));
    }

    // Implement CommandExecutor for direct registration
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute((Player) sender, args);
        return true;
    }
}
