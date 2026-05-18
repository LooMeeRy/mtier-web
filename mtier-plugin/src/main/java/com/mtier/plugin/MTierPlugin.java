package com.mtier.plugin;

import com.mtier.plugin.commands.StatsCommand;
import com.mtier.plugin.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MTierPlugin extends JavaPlugin {

    private static MTierPlugin instance;
    private Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        this.logger = getLogger();

        // Save default config if not exists
        saveDefaultConfig();

        // Register Commands
        if (getCommand("mtier") != null) {
            getCommand("mtier").setExecutor(new StatsCommand());
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        logger.info("MTier Pro Plugin has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        logger.info("MTier Pro Plugin has been disabled.");
    }

    public static MTierPlugin getInstance() {
        return instance;
    }
}
