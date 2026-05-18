package com.mtier.plugin.api;

import com.mtier.plugin.MTierPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
        String mode = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase(); // pvp -> PvP, bridge -> Bridge

        WebSyncManager.GamemodeStats modeStats = data.stats().get(mode);
        if (modeStats == null) return "---";

        if (type.equals("mmr")) {
            return String.valueOf(modeStats.mmr());
        } else if (type.equals("rank")) {
            return modeStats.rank();
        }

        return null;
    }
}
