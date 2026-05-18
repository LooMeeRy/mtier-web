package com.mtier.plugin.listeners;

import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.WebSyncManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class PlayerListener implements Listener {

    private final WebSyncManager syncManager;

    public PlayerListener() {
        this.syncManager = new WebSyncManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String username = event.getPlayer().getName();

        // Sync player login event to the web app
        syncManager.syncPlayerData(uuid, username, "PLAYER_JOIN", Map.of(
            "ip", event.getPlayer().getAddress().getHostString()
        ));
    }
}
