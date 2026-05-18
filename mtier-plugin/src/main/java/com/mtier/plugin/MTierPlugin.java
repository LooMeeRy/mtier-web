package com.mtier.plugin;

import com.mtier.plugin.api.MTierAPI;
import com.mtier.plugin.api.MTierAPIImpl;
import com.mtier.plugin.api.MenuManager;
import com.mtier.plugin.api.WebSyncManager;
import com.mtier.plugin.commands.StatsCommand;
import com.mtier.plugin.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MTierPlugin extends JavaPlugin {

    private static MTierPlugin instance;
    private Logger logger;
    private WebSyncManager syncManager;
    private MenuManager menuManager;
    private MTierAPI api;
    private final Map<UUID, WebSyncManager.PlayerData> playerCache = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        this.logger = getLogger();

        // Save default config if not exists
        saveDefaultConfig();
        
        this.syncManager = new WebSyncManager();
        this.menuManager = new MenuManager();
        this.api = new MTierAPIImpl();

        // Register Commands
        if (getCommand("mtier") != null) {
            getCommand("mtier").setExecutor(new StatsCommand());
            getCommand("mtier").setTabCompleter(new com.mtier.plugin.commands.CommandCompleter());
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(this.menuManager, this);

        // Register Placeholders
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new com.mtier.plugin.api.PlaceholderManager().register();
            logger.info("PlaceholderAPI expansion registered.");
        }

        logger.info("MTier Super-Core v3.0 has been enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("MTier Super-Core has been disabled.");
    }

    public static MTierPlugin getInstance() {
        return instance;
    }

    public static MTierAPI getAPI() {
        return instance.api;
    }

    public WebSyncManager getSyncManager() {
        return syncManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.syncManager = new WebSyncManager();
        this.menuManager = new MenuManager();
        this.api = new MTierAPIImpl();
    }

    public Map<UUID, WebSyncManager.PlayerData> getPlayerCache() {
        return playerCache;
    }
}
