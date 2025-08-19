package ir.elmando.beLands.commands;

import ir.elmando.beLands.BeLands;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class LandCommand implements CommandExecutor, TabCompleter {

    private final BeLands plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final Map<String, SubCommand> helpSubCommands = new HashMap<>();

    public LandCommand(BeLands plugin, List<SubCommand> commands) {
        this.plugin = plugin;
        for (SubCommand cmd : commands) {
            subCommands.put(cmd.getName().toLowerCase(), cmd);
            helpSubCommands.put(cmd.getName().toLowerCase(), cmd);
            for (String alias : cmd.getAliases()) {
                subCommands.put(alias.toLowerCase(), cmd);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Case: command is /landbuy etc (direct alias)
        if (label.toLowerCase().startsWith("land") && args.length == 0) {
            String aliasSub = label.toLowerCase().substring("land".length());
            SubCommand sub = subCommands.get(aliasSub);
            if (sub != null) {
                sub.execute(player, new String[0]);
                return true;
            }
        }

        if (args.length == 0) {
            player.sendMessage("§eUse /land help for a list of commands.");
            return true;
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            player.sendMessage("§cUnknown subcommand. Use /land help.");
            return true;
        }

        sub.execute(player, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> matches = new ArrayList<>();
            String current = args[0].toLowerCase();
            for (String key : subCommands.keySet()) {
                if (key.startsWith(current)) {
                    matches.add(key);
                }
            }
            return matches;
        }
        return Collections.emptyList();
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    public Map<String, SubCommand> getHelpSubCommands() {
        return helpSubCommands;
    }
}
