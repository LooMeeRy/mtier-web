package com.mtier.pvp.commands;

import com.mtier.pvp.MTierPvP;
import com.mtier.pvp.manager.RoomManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MTierCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length >= 2 && args[0].equalsIgnoreCase("pvp_spec")) {
            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                player.sendMessage("§6§lMTier §8» §cPlayer not found.");
                return true;
            }

            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(target);
            if (room == null || (room.getPhase() != RoomManager.Phase.BAN_PHASE && room.getPhase() != RoomManager.Phase.SELECTION_PHASE && room.getPhase() != RoomManager.Phase.PVP_PHASE)) {
                player.sendMessage("§6§lMTier §8» §cThis player is not currently in a match!");
                return true;
            }

            // Spectate Logic
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(target.getLocation());
            room.getSpectators().add(player.getUniqueId());
            
            player.sendMessage("§6§lMTier §8» §eYou are now spectating §f" + target.getName() + "§e.");
            player.sendMessage("§7You will be returned to the main world once the match ends.");
            return true;
        }

        player.sendMessage("§6§lMTier §8» §eUsage: /mtier pvp_spec <player>");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length <= 1) {
            List<String> sub = new ArrayList<>();
            sub.add("pvp_spec");
            String search = args.length == 0 ? "" : args[0].toLowerCase();
            return sub.stream().filter(s -> s.startsWith(search)).collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("pvp_spec")) {
            // Return list of players currently in a match phase
            return Bukkit.getOnlinePlayers().stream()
                .filter(p -> {
                    RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(p);
                    return room != null && (room.getPhase() == RoomManager.Phase.BAN_PHASE || room.getPhase() == RoomManager.Phase.SELECTION_PHASE || room.getPhase() == RoomManager.Phase.PVP_PHASE);
                })
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
