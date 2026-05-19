package com.mtier.pvp.manager;

import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.WebSyncManager;
import com.mtier.pvp.MTierPvP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

    // K: Player UUID, V: Start time in ms
    private final Map<UUID, Long> searchingPlayers = new ConcurrentHashMap<>();

    private static class QueueEntry {
        Player player;
        int mmr;
        long waitTimeMs;

        public QueueEntry(Player player, int mmr, long waitTimeMs) {
            this.player = player;
            this.mmr = mmr;
            this.waitTimeMs = waitTimeMs;
        }
    }

    public QueueManager() {
        // Continuous Matchmaking Task (Centralized Engine)
        new BukkitRunnable() {
            @Override
            public void run() {
                processMatchmaking();
            }
        }.runTaskTimer(MTierPvP.getInstance(), 40L, 40L); // Run every 2 seconds
    }

    public void startSearching(Player player) {
        searchingPlayers.put(player.getUniqueId(), System.currentTimeMillis());
        // Message sent in listener or here
    }

    public void stopSearching(Player player) {
        searchingPlayers.remove(player.getUniqueId());
    }

    public boolean isSearching(Player player) {
        return searchingPlayers.containsKey(player.getUniqueId());
    }

    private void processMatchmaking() {
        if (searchingPlayers.size() < 2) return;

        long now = System.currentTimeMillis();
        List<QueueEntry> pool = new ArrayList<>();

        // 1. Collect all valid players
        for (Map.Entry<UUID, Long> entry : searchingPlayers.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p == null || !p.isOnline()) {
                searchingPlayers.remove(entry.getKey());
                continue;
            }

            WebSyncManager.PlayerData pData = MTierPlugin.getAPI().getCachedPlayerData(p.getUniqueId());
            int mmr = 0;
            if (pData != null && pData.stats() != null && pData.stats().containsKey("PvP")) {
                mmr = pData.stats().get("PvP").mmr();
            }

            pool.add(new QueueEntry(p, mmr, now - entry.getValue()));
        }

        if (pool.size() < 2) return;

        // 2. Sort by MMR (ascending)
        pool.sort(Comparator.comparingInt(e -> e.mmr));

        // 3. Pairing Logic
        Set<UUID> matched = new HashSet<>();

        for (int i = 0; i < pool.size() - 1; i++) {
            QueueEntry p1 = pool.get(i);
            if (matched.contains(p1.player.getUniqueId())) continue;

            QueueEntry p2 = pool.get(i + 1);
            if (matched.contains(p2.player.getUniqueId())) continue;

            int mmrDiff = Math.abs(p1.mmr - p2.mmr);
            
            // Dynamic MMR Range: expand allowed difference if waiting longer
            long maxWait = Math.max(p1.waitTimeMs, p2.waitTimeMs);
            int allowedDiff = 200; // Base difference
            if (maxWait > 20000) allowedDiff = 500; // > 20s
            if (maxWait > 40000) allowedDiff = 1000; // > 40s

            if (mmrDiff <= allowedDiff) {
                // Match Found!
                matched.add(p1.player.getUniqueId());
                matched.add(p2.player.getUniqueId());
                searchingPlayers.remove(p1.player.getUniqueId());
                searchingPlayers.remove(p2.player.getUniqueId());

                // Trigger Room Creation & UI
                MTierPvP.getInstance().getRoomManager().createMatchmakingRoom(p1.player, p2.player);
            }
        }
    }
}
