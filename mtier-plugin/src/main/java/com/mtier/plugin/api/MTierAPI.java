package com.mtier.plugin.api;

import com.mtier.plugin.api.models.MatchData;
import com.mtier.plugin.api.WebSyncManager.PlayerData;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The public API for MTier-Core.
 * Gamemode plugins should use this to sync stats and match results.
 */
public interface MTierAPI {

    /**
     * Get the current stats for a player from the local cache.
     * Useful for showing info on join or in GUIs.
     */
    WebSyncManager.PlayerData getCachedPlayerData(UUID uuid);

    /**
     * Submit a completed match result to the web server.
     * This will automatically update all participants' MMR and Match History.
     */
    CompletableFuture<Boolean> submitMatch(MatchData match);

    /**
     * Manually update a player's MMR for a specific gamemode.
     */
    CompletableFuture<Boolean> updatePlayerMMR(String username, String gamemode, int newMmr);
}
