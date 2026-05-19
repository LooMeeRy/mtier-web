package com.mtier.pvp.manager;

import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.WebSyncManager;
import com.mtier.pvp.MTierPvP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

    private final Map<UUID, Long> searchingPlayers = new ConcurrentHashMap<>();

    public QueueManager() {
        // Continuous Matchmaking Task (Runs every 2 seconds)
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : searchingPlayers.keySet()) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) attemptMatch(p);
                }
            }
        }.runTaskTimer(MTierPvP.getInstance(), 40L, 40L);
    }

    public void startSearching(Player player) {
        searchingPlayers.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage("§6§lMTier §8» §eStarted searching for a match...");
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
        int pMmr = (pData != null && pData.stats().containsKey("PvP")) ? pData.stats().get("PvP").mmr() : 0;

        for (UUID otherUuid : searchingPlayers.keySet()) {
            if (otherUuid.equals(player.getUniqueId())) continue;

            Player other = Bukkit.getPlayer(otherUuid);
            if (other == null) {
                searchingPlayers.remove(otherUuid);
                continue;
            }

            WebSyncManager.PlayerData oData = MTierPlugin.getAPI().getCachedPlayerData(otherUuid);
            int oMmr = (oData != null && oData.stats().containsKey("PvP")) ? oData.stats().get("PvP").mmr() : 0;

            // MMR range matching: +/- 300 MMR
            if (Math.abs(pMmr - oMmr) <= 300) {
                searchingPlayers.remove(player.getUniqueId());
                searchingPlayers.remove(otherUuid);
                
                // Trigger Match Found in Listener
                MTierPvP.getInstance().getRoomManager().createMatchmakingRoom(player, other);
            }
        }
    }
}
