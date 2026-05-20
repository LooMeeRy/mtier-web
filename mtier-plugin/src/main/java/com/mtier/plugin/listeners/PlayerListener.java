package com.mtier.plugin.listeners;

import com.mtier.plugin.MTierPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String username = event.getPlayer().getName();

        // 1. Sync join event first
        MTierPlugin.getInstance().getSyncManager().syncPlayerData(uuid, username, "PLAYER_JOIN", Map.of(
            "ip", event.getPlayer().getAddress().getHostString()
        )).thenRun(() -> {
            // 2. ONLY after sync is complete, fetch stats for cache
            MTierPlugin.getInstance().getSyncManager().fetchPlayerData(uuid, username).thenAccept(data -> {
                if (data != null) {
                    MTierPlugin.getInstance().getPlayerCache().put(event.getPlayer().getUniqueId(), data);
                    MTierPlugin.getInstance().getLogger().info("Successfully loaded Supabase data for " + username);
                }
            });
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        MTierPlugin.getInstance().getPlayerCache().remove(event.getPlayer().getUniqueId());
    }
}
