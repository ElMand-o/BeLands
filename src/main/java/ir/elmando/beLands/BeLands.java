package ir.elmando.beLands;

import ir.elmando.beLands.commands.LandCommand;
import ir.elmando.beLands.commands.LandManager;
import ir.elmando.beLands.commands.subcommands.SubCommand;
import ir.elmando.beLands.commands.subcommands.impl.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class BeLands extends JavaPlugin {

    ConfigManager config = new ConfigManager(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Enabling BeLands...");

        // Initialize hooks (Vault, GriefPrevention, PlaceholderAPI)
        HookManager.init();

        LandManager landManager = new LandManager(config, HookManager.economy(), HookManager.griefPrevention());

        HelpCommand helpCommand = new HelpCommand();
        StartPCommand startPCommand = new StartPCommand(this, config, landManager);
        EndPCommand endPCommand = new EndPCommand(this, config, landManager, startPCommand);

        // Create all subcommands
        List<SubCommand> subCommands = Arrays.asList(
                startPCommand,
                endPCommand,
                helpCommand,
                new BuyCommand(landManager, config, startPCommand, endPCommand, HookManager.economy()),
                new SellCommand(landManager, config, HookManager.griefPrevention(), HookManager.economy()),
                new TransferCommand(landManager, config),
                new TrustCommand(landManager, config),
                new UntrustCommand(landManager, config),
                new ListCommand(landManager, config),
                new HereCommand(landManager, config),
                new WhoseCommand(landManager, config),
                new PriceCommand(HookManager.griefPrevention(), landManager, config),
                new MoveCommand(config, landManager),
                new ReloadCommand(config, HookManager.permission()),
                new DebugCommand(startPCommand, endPCommand, this)
        );
        // Register main LandCommand
        LandCommand landCommand = new LandCommand(this, subCommands);

        helpCommand.setLandCommand(landCommand);

        // Register main commands and aliases
        String[] landAliases = {
                "land", "claim", "landbuy", "landsell", "landinfo",
                "landtp", "landtrust", "landuntrust", "landtransfer", "landclear"
        };

        for (String cmd : landAliases) {
            if (getCommand(cmd) != null) {
                getCommand(cmd).setExecutor(landCommand);
                getCommand(cmd).setTabCompleter(landCommand);
            }
        }

        // Register selection commands separately for direct usage
        if (getCommand("startp") != null) getCommand("startp").setExecutor(startPCommand);
        if (getCommand("endp") != null) getCommand("endp").setExecutor(endPCommand);

        getLogger().info("BeLands loaded successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling BeLands...");
    }
}
