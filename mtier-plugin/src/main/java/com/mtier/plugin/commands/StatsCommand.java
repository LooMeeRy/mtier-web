package com.mtier.plugin.commands;

import com.mtier.plugin.MTierPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // If it's a player and no arguments are provided, open the GUI menu
        if (sender instanceof Player player && args.length == 0) {
            MTierPlugin.getAPI().openSelector(player);
            return true;
        }

        // /mtier reload
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mtier.admin")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            MTierPlugin.getInstance().reloadPluginConfig();
            sender.sendMessage(ChatColor.GREEN + "MTier configuration reloaded.");
            return true;
        }

        // /mtier play (Alias for GUI)
        if (args.length > 0 && args[0].equalsIgnoreCase("play")) {
            if (sender instanceof Player player) {
                MTierPlugin.getAPI().openSelector(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Players only.");
            }
            return true;
        }

        // /mtier test
        if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
            if (!sender.hasPermission("mtier.admin")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Triggering sync test...");
            MTierPlugin.getInstance().getSyncManager().syncPlayerData(
                "test-uuid", "TestPlayer", "PLAYER_JOIN", java.util.Map.of("test", true)
            );
            return true;
        }

        // /mtier setmmr <player> <mode> <amount>
        if (args.length >= 4 && args[0].equalsIgnoreCase("setmmr")) {
            if (!sender.hasPermission("mtier.admin")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            String target = args[1];
            String mode = args[2];
            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount.");
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "Updating MMR for " + target + "...");
            MTierPlugin.getInstance().getSyncManager().updatePlayerMMR(target, mode, amount).thenAccept(success -> {
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + "Successfully updated MMR for " + target);
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to update MMR. Check console.");
                }
            });
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Players only.");
            return true;
        }

        // Default help message (if arguments didn't match anything)
        player.sendMessage(ChatColor.GOLD + "--- MTier Super-Core v3.0 ---");
        player.sendMessage(ChatColor.GRAY + "Your stats are synced with the web dashboard.");
        player.sendMessage(ChatColor.YELLOW + "URL: " + ChatColor.WHITE + "https://loona-tier.vercel.app/player/" + player.getName());
        player.sendMessage(ChatColor.YELLOW + "Usage: " + ChatColor.WHITE + "/mtier (Opens Menu)");

        return true;
    }
}
