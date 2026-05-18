package com.mtier.pvp.manager;

import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.WebSyncManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

    private final Map<UUID, Long> searchingPlayers = new ConcurrentHashMap<>();

    public void startSearching(Player player) {
        searchingPlayers.put(player.getUniqueId(), System.currentTimeMillis());
        // Start matching task if not already running or check every second
        attemptMatch(player);
    }

    public void stopSearching(Player player) {
        searchingPlayers.remove(player.getUniqueId());
    }

    public boolean isSearching(Player player) {
        return searchingPlayers.containsKey(player.getUniqueId());
    }

    private void attemptMatch(Player player) {
        if (!isSearching(player)) return;

        WebSyncManager.PlayerData pData = MTierPlugin.getAPI().getCachedPlayerData(player.getUniqueId());
        int pMmr = (pData != null && pData.stats().containsKey("PvP")) ? pData.stats().get("PvP").mmr() : 1000;

        for (UUID otherUuid : searchingPlayers.keySet()) {
            if (otherUuid.equals(player.getUniqueId())) continue;

            Player other = Bukkit.getPlayer(otherUuid);
            if (other == null) {
                searchingPlayers.remove(otherUuid);
                continue;
            }

            WebSyncManager.PlayerData oData = MTierPlugin.getAPI().getCachedPlayerData(otherUuid);
            int oMmr = (oData != null && oData.stats().containsKey("PvP")) ? oData.stats().get("PvP").mmr() : 1000;

            // Simple MMR range matching (e.g. +/- 300)
            if (Math.abs(pMmr - oMmr) <= 300) {
                // Found a match!
                searchingPlayers.remove(player.getUniqueId());
                searchingPlayers.remove(otherUuid);
                
                // Transition to Waiting Room (to be implemented)
                Bukkit.broadcastMessage("§aMatch Found! §e" + player.getName() + " §7vs §e" + other.getName());
                // TODO: Open Waiting Room Menu for both
            }
        }
    }
}
