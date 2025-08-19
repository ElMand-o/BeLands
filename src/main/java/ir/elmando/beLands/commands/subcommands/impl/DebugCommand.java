package ir.elmando.beLands.commands.subcommands.impl;

import ir.elmando.beLands.HookManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DebugCommand implements SubCommand, CommandExecutor {

    private final StartPCommand startPCommand;
    private final EndPCommand endPCommand;
    private final Plugin plugin;

    public DebugCommand(StartPCommand startPCommand, EndPCommand endPCommand, Plugin plugin) {
        this.startPCommand = startPCommand;
        this.endPCommand = endPCommand;
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "Shows your stored start and end positions.";
    }

    @Override
    public String getUsage() {
        return "/land debug";
    }

    @Override
    public void execute(Player player, String[] args) {
        sendDebugInfo(player);
    }

    private void sendDebugInfo(Player player) {
        Location start = startPCommand.getStartPosition(player);
        Location end = endPCommand.getEndPosition(player);

        player.sendMessage("§e--- Your Land Debug Info ---");

        if (start != null) {
            player.sendMessage("§aStart: X:" + start.getBlockX() + " Y:" + start.getBlockY() + " Z:" + start.getBlockZ());
        } else {
            player.sendMessage("§cStart: Not set");
        }

        if (end != null) {
            player.sendMessage("§aEnd: X:" + end.getBlockX() + " Y:" + end.getBlockY() + " Z:" + end.getBlockZ());
        } else {
            player.sendMessage("§cEnd: Not set");
        }

        player.sendMessage("§e---------------------------");

//        Claim claim = HookManager.griefPrevention().dataStore.getClaim(15422);
//        if (claim == null) {
//            player.sendMessage("Claim not found!");
//            return;
//        }
//
//        UUID testUUID = UUID.fromString("a01e3843-e521-3998-958a-f459800e4d11");
//        String perm = String.valueOf(claim.getPermission(testUUID.toString())); // always use UUID as string
//        String perm2 = String.valueOf(claim.getPermission("Player")); // always use UUID as string
//        if (perm != null) {
//            player.sendMessage("Permission: " + perm + " | class: " + perm.getClass());
//        } else {
//            player.sendMessage("No permission found for this UUID!");
//        }
//        if (perm2 != null) {
//            player.sendMessage("Permission: " + perm2 + " | class: " + perm2.getClass());
//        } else {
//            player.sendMessage("No permission found for this UUID!");
//        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        sendDebugInfo(player);
        return true;
    }

    @Override
    public List<String> getAliases() {
        return List.of(new String[]{"dbg"});
    }
}
