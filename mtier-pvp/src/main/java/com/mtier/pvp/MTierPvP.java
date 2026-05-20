package com.mtier.pvp;

import com.mtier.plugin.MTierPlugin;
import com.mtier.pvp.gui.PvPMainMenu;
import com.mtier.pvp.listener.PvPListener;
import com.mtier.pvp.manager.QueueManager;
import com.mtier.pvp.manager.RoomManager;
import com.mtier.pvp.manager.SlimeManager;
import com.mtier.pvp.manager.BanManager;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class MTierPvP extends JavaPlugin {

    private static MTierPvP instance;
    private QueueManager queueManager;
    private RoomManager roomManager;
    private SlimeManager slimeManager;
    private BanManager banManager;
    private com.mtier.pvp.listener.PvPListener pvpListener;

    @Override
    public void onEnable() {
        instance = this;
        this.queueManager = new QueueManager();
        this.roomManager = new RoomManager();
        this.slimeManager = new SlimeManager();
        this.banManager = new BanManager();
        this.pvpListener = new com.mtier.pvp.listener.PvPListener();

        // Register with MTier-Core
        MTierPlugin.getAPI().registerGamemode(
            "PvP", 
            "PvP Sector", 
            Material.DIAMOND_SWORD, 
            player -> {
                new com.mtier.pvp.gui.PvPMainMenu().open(player);
            }
        );

        // Register Listeners
        getServer().getPluginManager().registerEvents(this.pvpListener, this);
        getServer().getPluginManager().registerEvents(new com.mtier.pvp.listener.MatchListener(), this);

        // Register Commands
        if (getCommand("banmenu") != null) {
            getCommand("banmenu").setExecutor(new com.mtier.pvp.commands.BanCommand());
        }
        
        // Register Subcommand with Core
        com.mtier.pvp.commands.MTierCommand mTierCommand = new com.mtier.pvp.commands.MTierCommand();
        MTierPlugin.getAPI().registerSubCommand("pvp_spec", mTierCommand, mTierCommand);

        getLogger().info("MTier-PvP v1.0 has been enabled and registered!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MTier-PvP disabled.");
    }

    public static MTierPvP getInstance() {
        return instance;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public SlimeManager getSlimeManager() {
        return slimeManager;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public com.mtier.pvp.listener.PvPListener getPvPListener() {
        return pvpListener;
    }
    }

