package com.mtier.plugin.api;

import com.mtier.plugin.MTierPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public class PlaceholderManager extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "mtier";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MTier";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        WebSyncManager.PlayerData data = MTierPlugin.getInstance().getPlayerCache().get(player.getUniqueId());
        if (data == null || data.stats() == null) return "---";

        // Handle: mmr_pvp, rank_pvp, mmr_bridge, rank_bridge
        String[] parts = params.split("_");
        if (parts.length < 2) return null;

        String type = parts[0].toLowerCase(); // mmr or rank
        String requestedMode = parts[1].toLowerCase();

        // Find the mode in cache case-insensitively
        WebSyncManager.GamemodeStats modeStats = null;
        for (Map.Entry<String, WebSyncManager.GamemodeStats> entry : data.stats().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(requestedMode)) {
                modeStats = entry.getValue();
                break;
            }
        }

        if (modeStats == null) return "---";

        if (type.equals("mmr")) {
            return String.valueOf(modeStats.mmr());
        } else if (type.equals("rank")) {
            return modeStats.rank();
        }

        return null;
    }
}
