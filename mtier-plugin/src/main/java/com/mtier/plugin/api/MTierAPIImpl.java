package com.mtier.plugin.api;

import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.models.MatchData;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MTierAPIImpl implements MTierAPI {

    @Override
    public WebSyncManager.PlayerData getCachedPlayerData(UUID uuid) {
        return MTierPlugin.getInstance().getPlayerCache().get(uuid);
    }

    @Override
    public CompletableFuture<Boolean> submitMatch(MatchData match) {
        return MTierPlugin.getInstance().getSyncManager().submitMatch(match);
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerMMR(String username, String gamemode, int newMmr) {
        return MTierPlugin.getInstance().getSyncManager().updatePlayerMMR(username, gamemode, newMmr);
    }
}
