package com.mtier.pvp;

import com.mtier.plugin.MTierPlugin;
import com.mtier.pvp.gui.PvPMainMenu;
import com.mtier.pvp.listener.PvPListener;
import com.mtier.pvp.manager.QueueManager;
import com.mtier.pvp.manager.RoomManager;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class MTierPvP extends JavaPlugin {

    private static MTierPvP instance;
    private QueueManager queueManager;
    private RoomManager roomManager;

    @Override
    public void onEnable() {
        instance = this;
        this.queueManager = new QueueManager();
        this.roomManager = new RoomManager();

        // Register with MTier-Core
        MTierPlugin.getAPI().registerGamemode(
            "PvP", 
            "PvP Sector", 
            Material.DIAMOND_SWORD, 
            player -> {
                new PvPMainMenu().open(player);
            }
        );

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PvPListener(), this);

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
}
