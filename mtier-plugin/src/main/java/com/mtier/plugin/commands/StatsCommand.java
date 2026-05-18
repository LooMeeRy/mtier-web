package com.mtier.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
            if (!sender.hasPermission("mtier.admin")) {
                sender.sendMessage(ChatColor.RED + "You need mtier.admin permission to test.");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Manually triggering sync test...");
            new com.mtier.plugin.api.WebSyncManager().syncPlayerData(
                "test-uuid", "TestPlayer", "PLAYER_JOIN", java.util.Map.of("test", true)
            );
            sender.sendMessage(ChatColor.GREEN + "Test request sent. Check server console for results.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("mtier.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "--- MTier Pro Status ---");
        player.sendMessage(ChatColor.GRAY + "Your stats are being synchronized with the web dashboard.");
        player.sendMessage(ChatColor.YELLOW + "View your full profile at: " + ChatColor.WHITE + ChatColor.UNDERLINE + "http://localhost:3000/player/" + player.getName());

        return true;
    }
}
