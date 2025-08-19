package ir.elmando.beLands.commands.subcommands;

import org.bukkit.entity.Player;

import java.util.List;

public interface SubCommand {

    /**
     * @return The name of the subcommand (e.g., "buy", "sell")
     */
    String getName();

    /**
     * @return Short description for help messages
     */
    String getDescription();

    /**
     * @return Usage string for the command (e.g., "/land buy")
     */
    String getUsage();

    List<String> getAliases();

    /**
     * Execute the subcommand.
     *
     * @param player The player executing the command
     * @param args   Arguments after the subcommand
     */
    void execute(Player player, String[] args);
}
