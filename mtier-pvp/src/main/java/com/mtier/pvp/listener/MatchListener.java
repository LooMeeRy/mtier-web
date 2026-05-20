package com.mtier.pvp.listener;

import com.mtier.pvp.MTierPvP;
import com.mtier.pvp.manager.RoomManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MatchListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
        if (room != null && (room.getPhase() == RoomManager.Phase.BAN_PHASE || room.getPhase() == RoomManager.Phase.SELECTION_PHASE || room.getPhase() == RoomManager.Phase.PVP_PHASE)) {
            // Forfeit Logic
            Player winner = room.getOwner().equals(player) ? room.getChallenger() : room.getOwner();
            if (winner != null) {
                winner.sendMessage("§6§lMTier §8» §c" + player.getName() + " disconnected! You win by forfeit.");
            }
            MTierPvP.getInstance().getBanManager().endMatch(room, winner);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
        if (room != null && room.getPhase() != RoomManager.Phase.WAITING && room.getPhase() != RoomManager.Phase.POST_MATCH) {
            String cmd = event.getMessage().toLowerCase().split(" ")[0];
            // Allow-list
            if (!cmd.equals("/mtier") && !cmd.equals("/banmenu") && !cmd.equals("/msg") && !cmd.equals("/r") && !cmd.equals("/w") && !cmd.equals("/list")) {
                event.setCancelled(true);
                player.sendMessage("§6§lMTier §8» §cYou cannot use this command during a match!");
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
        if (room != null && (room.getPhase() == RoomManager.Phase.BAN_PHASE || room.getPhase() == RoomManager.Phase.SELECTION_PHASE)) {
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room == null) return;

            if (room.getPhase() == RoomManager.Phase.BAN_PHASE || room.getPhase() == RoomManager.Phase.SELECTION_PHASE || room.getPhase() == RoomManager.Phase.POST_MATCH) {
                event.setCancelled(true);
                return;
            }

            if (room.getPhase() == RoomManager.Phase.PVP_PHASE) {
                if (player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    player.setHealth(20.0);
                    player.setFoodLevel(20);
                    player.setGameMode(GameMode.SPECTATOR);
                    
                    Player winner = room.getOwner().equals(player) ? room.getChallenger() : room.getOwner();
                    MTierPvP.getInstance().getBanManager().endMatch(room, winner);
                }
            }
        }
    }

    @EventHandler
    public void onHunger(org.bukkit.event.entity.FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room != null && (room.getPhase() != RoomManager.Phase.PVP_PHASE)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(event.getPlayer());
        if (room != null && (room.getPhase() != RoomManager.Phase.PVP_PHASE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(event.getPlayer());
        if (room != null && (room.getPhase() != RoomManager.Phase.PVP_PHASE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(event.getPlayer());
        if (room != null && (room.getPhase() != RoomManager.Phase.PVP_PHASE)) {
            if (event.getClickedBlock() != null) {
                event.setCancelled(true);
            }
        }
    }
}
