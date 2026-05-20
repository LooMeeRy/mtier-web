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

    @Override
    public void registerGamemode(String id, String displayName, org.bukkit.Material icon, java.util.function.Consumer<org.bukkit.entity.Player> action) {
        MTierPlugin.getInstance().getMenuManager().registerMode(id, displayName, icon, action);
    }

    @Override
    public void openSelector(org.bukkit.entity.Player player) {
        MTierPlugin.getInstance().getMenuManager().openMenu(player);
    }

    @Override
    public void registerSubCommand(String name, org.bukkit.command.CommandExecutor executor, org.bukkit.command.TabCompleter completer) {
        MTierPlugin.getInstance().getSubCommands().put(name.toLowerCase(), new MTierPlugin.SubCommand(executor, completer));
    }
}
